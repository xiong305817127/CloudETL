package com.idatrix.unisecurity.user.service.impl;

import com.idatrix.unisecurity.aspects.WebLogAspect;
import com.idatrix.unisecurity.common.dao.ClientSystemMapper;
import com.idatrix.unisecurity.common.domain.*;
import com.idatrix.unisecurity.common.enums.ResultEnum;
import com.idatrix.unisecurity.common.exception.SecurityException;
import com.idatrix.unisecurity.common.sso.StringUtil;
import com.idatrix.unisecurity.common.utils.CookieUtils;
import com.idatrix.unisecurity.common.utils.ResultVoUtils;
import com.idatrix.unisecurity.core.jedis.JedisClient;
import com.idatrix.unisecurity.core.shiro.token.manager.ShiroTokenManager;
import com.idatrix.unisecurity.permission.service.PermissionService;
import com.idatrix.unisecurity.properties.LoginProperties;
import com.idatrix.unisecurity.renter.service.RenterService;
import com.idatrix.unisecurity.server.token.TokenManager;
import com.idatrix.unisecurity.user.Config;
import com.idatrix.unisecurity.user.service.IAuthenticationHandler;
import com.idatrix.unisecurity.user.service.LoginCountService;
import com.idatrix.unisecurity.user.service.UUserService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URL;
import java.util.*;

@Service
public class SSOAuthenticationHandler implements IAuthenticationHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UUserService userService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RenterService renterService;

    @Autowired(required = false)
    private ClientSystemMapper clientSystemMapper;

    @Autowired
    private JedisClient jedisClient;

    @Autowired(required = false)
    private Config config;

    @Autowired
    private LoginProperties loginProperties;

    @Autowired
    private LoginCountService loginCountService;

    @Override
    public List<ClientSystem> loadClientSystem() {
        return clientSystemMapper.loadClientSystem();
    }

    @Override
    public List<ClientSystem> loadClientSystems(Properties configProperties) throws Exception {
        List<ClientSystem> result = new ArrayList<>();
        // 重新加载
        String serverNames = "security_server,metacube_server,cloudetl_server,service_base_server,catalog_server,datalab_server,monitor_server,bi_server";
        String[] serverNameArr = serverNames.split(",");
        for (String serverName : serverNameArr) {
            String serverAddress = configProperties.getProperty(serverName);
            loadFileToClientSystems(serverAddress, result);
        }
        return result;
    }

    public void loadFileToClientSystems(String serverAddress, List<ClientSystem> list) {
        if (serverAddress != null) {
            String[] serverAddressArr = serverAddress.split(",");
            for (String address : serverAddressArr) {
                ClientSystem system = new ClientSystem();
                system.setBaseUrl(address);
                list.add(system);
            }
        }
    }

    @Override
    public UUser authenticate(Credential credential, Boolean isValidateCode) throws Exception {
        return null;
    }

    @Transactional
    @Override
    public Map<String, Object> authenticate(String name, String password, Boolean rememberMe, String backUrl, HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.debug("判断当前账号是否被锁定");
        String isLock = jedisClient.get(loginProperties.getUserIsLockKey() + name);
        if (isLock != null && isLock.equals("LOCK")) {
            logger.debug("用户名或密码错误次数大于" + loginProperties.getMaxLoginCount() + "次,账户已锁定。当前用户名为：" + name);
            throw new SecurityException(ResultEnum.USER_LOCK.getCode(), "用户名或密码错误次数大于" + loginProperties.getMaxLoginCount() + "次,账户已锁定");
        }

        // 访问一次纪录一次
        logger.debug("用户登录计数");
        Long incr = jedisClient.incr(loginProperties.getUserLoginCountKey() + name);
        jedisClient.expire(loginProperties.getUserLoginCountKey() + name, 60 * 60 * 24);// 每过一天计数清空
        if (incr.intValue() >= loginProperties.getMaxLoginCount().intValue()) {
            jedisClient.set(loginProperties.getUserIsLockKey() + name, "LOCK");
            jedisClient.expire(loginProperties.getUserIsLockKey() + name, loginProperties.getLockTime());
            jedisClient.del(loginProperties.getUserLoginCountKey() + name);
        }

        String message;
        if (incr.intValue() < loginProperties.getMaxLoginCount().intValue()) {
            message = "再试" + (loginProperties.getMaxLoginCount().intValue() - incr.intValue()) + "次即将锁定！";
        } else {
            message = "当前账号已被锁定！";
        }

        try {
            Map resultMap = ResultVoUtils.resultMap();
            // 进行shiro认证
            UUser user = new UUser();
            user.setUsername(name);
            user.setPswd(password);
            user = ShiroTokenManager.login(user, rememberMe);

            // 认证成功后往下走，认证失败会抛出异常
            Subject subject = SecurityUtils.getSubject();
            if (SecurityUtils.getSubject().isAuthenticated() || SecurityUtils.getSubject().isRemembered()) {
                logger.debug("shiro sessionId：" + subject.getSession().getId());
            }

            // 清空登录计数
            jedisClient.del(loginProperties.getUserLoginCountKey() + name);
            // 设置未锁定状态
            jedisClient.del(loginProperties.getUserIsLockKey() + name);

            // 成功登陆后，修改最后的登录时间
            user.setLastLoginTime(new Date());
            userService.updateLoginUserInfo(user);

            // 成功登陆后，记录一条登陆数据
            LoginCount loginCount = new LoginCount(user.getUsername(), WebLogAspect.getIp(request), user.getId(), user.getRenterId(), new Date());
            loginCountService.insertSelective(loginCount);

            // 登录成功后，将用户信息缓存到服务端，获取用户的权限返回
            validateSuccess(user, backUrl, resultMap, request, response);

            return resultMap;
        } catch (UnknownAccountException e) {
            // 账号不存在
            logger.error("登录失败，账号不存在，username：{}", name);
            throw new SecurityException(ResultEnum.USERNAME_OR_PASSWORD_ERROR.getCode(), "帐号或密码错误," + message);
        } catch (IncorrectCredentialsException e) {
            logger.error("登录失败，密码错误，username：{}", name);
            // 密码错误
            throw new SecurityException(ResultEnum.USERNAME_OR_PASSWORD_ERROR.getCode(), "帐号或密码错误," + message);
        } catch (DisabledAccountException e) {
            // 清空登录计数
            jedisClient.del(loginProperties.getUserLoginCountKey() + name);
            throw new SecurityException(ResultEnum.USERNAME_OR_PASSWORD_ERROR.getCode(), "账号被禁止登陆");
        } catch (Exception e) {
            // 其它登陆错误
            logger.error("登陆出现未知错误，error：" + e.getMessage());
            throw new SecurityException(ResultEnum.SERVER_ERROR.getCode(), "登陆出现未知错误，请稍后重试。" + message);
        }
    }

    // 令牌验证成功或登录成功后的操作
    public void validateSuccess(UUser uUser, String backUrl, Map resultMap, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 将username写入到cookie中
        CookieUtils.setCookie(request, response, loginProperties.getCookieUserNameKey(), uUser.getUsername(), loginProperties.getSecureMode());

        // 获取令牌（session id）
        String tokenId = ShiroTokenManager.getSessionId();
        // 服务器中保存一份本地缓存
        TokenManager.addToken(tokenId, uUser);

        // 拿到当前用户所拥有的系统权限
        List<ClientSystem> csList = getClientSystems(uUser);
        resultMap.put("sysList", csList);
        // 判断是否需要跳转到哪个页面
        if (StringUtils.isEmpty(backUrl)) {
            resultMap.put("vt", tokenId);
            resultMap.put("loginUser", uUser);
        } else {
            // 获取参数并保存到url中
            URL url = new URL(backUrl);
            String cid = null;
            if (StringUtils.isNotEmpty(url.getFile())) {
                cid = getContext(url.getFile());// 当前要访问的系统id
                if (CollectionUtils.isNotEmpty(csList)) {
                    for (ClientSystem cs : csList) {// 遍历当前用户所拥有的系统权限
                        if (cs.getId().equals(cid) && cs.getBaseUrl().equals(url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() + "/" + cid)) {
                            resultMap.put("permits", permissionService.selectPermByUserIdAndCid(uUser.getId(), cid));
                            break;
                        }
                    }
                }
            }
            resultMap.put("backUrl", StringUtil.appendUrlParameter(backUrl, "__vt_param__", tokenId));
        }
    }

    private String getContext(String s) {
        if (s != null && s.length() > 0) {
            String sArray[] = s.split("/");
            if (sArray != null && sArray.length > 0) {
                return sArray[1];
            }
            return "";
        } else {
            return "";
        }
    }

    @Override
    public List<ClientSystem> getClientSystems(UUser uUser) throws Exception {
        // 查询出当前用户的所拥有的系统权限
        List<ClientSystem> syses = null;

        if (uUser.getUsername().equals("root")) {
            // 当前用户为root，拥有所有系统权限
            syses = clientSystemMapper.loadClientSystem();
            return syses;
        }

        // 如果为租户，那么根据开通的系统权限去查询系统地址
        URenter uRenter = renterService.findByAdminAccount(uUser.getUsername());
        if (uRenter != null) {
            String openedResource = uRenter.getOpenedResource();
            String[] lientSystemArray = openedResource.split(",");
            syses = clientSystemMapper.findByIds(lientSystemArray);
        } else {
            // 普通用户
            syses = clientSystemMapper.selectClientSystemByUserId(uUser.getId());
        }
        return syses;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        /**
         * 1.校验令牌有效性，shiro已经帮我们做了
         * 2.服务端销毁全局会话，客户端销毁局部会话
         * 4.清空缓存的权限
         * 5.shiro登出
         */
        CookieUtils.deleteCookie(request, response, loginProperties.getCookieUserNameKey(), loginProperties.getSecureMode());// 删除cookie中的用户名
        String token = ShiroTokenManager.getSessionId();// 获取会话id
        TokenManager.invalid(token);// 移除服务端的本地缓存
        systemLogout(token);// 通知客户端退出
        ShiroTokenManager.clearNowUserAuth();// 清除当前用户的权限缓存
        SecurityUtils.getSubject().logout();// 最后，shiro登出
    }

    @Override
    public void systemLogout(String token) {
        // 开启一个线程去通知所有的客户端 logout，主要是为了避免其他客户端 logout 时间太长或者不响应
        class LogoutThread implements Runnable {
            List<ClientSystem> servers;

            public LogoutThread(List<ClientSystem> servers) {
                this.servers = servers;
            }

            @Override
            public void run() {
                for (ClientSystem clientSystem : servers) {
                    clientSystem.noticeLogout(token);
                }
            }
        }
        new Thread(new LogoutThread(config.getClientSystems())).start();
    }

    @Override
    public UUser autoLogin(String lt) throws Exception {
        /**
         * // 根据lt查询用户信息
         String loginToken = userMapper.getLTUser(lt);
         UUser loginUser = null;
         if (StringUtils.isNotEmpty(loginToken)) {
         String[] tmps = loginToken.split("=");
         if (lt.equals(tmps[0])) {
         //根据name查询用户信息
         loginUser = userMapper.getUser(tmps[1]);
         }
         }
         if (loginUser != null) {
         return loginUser;
         }
         return null;
         */
        return null;
    }

    @Override
    public String loginToken(UUser loginUser) throws Exception {
        /**
         * UUser uUser = loginUser;
         // 生成一个唯一标识用作lt
         String lt = StringUtil.uniqueKey();
         Map<String, Object> userMap = new HashMap<String, Object>();
         userMap.put("username", uUser.getUsername());
         // 将新lt更新到当前user对应字段
         userMap.put("loginToken", lt + "=" + userMap.get("username"));
         userMapper.updateLoginToken(userMap);
         return lt;
         */
        return null;
    }

    @Override
    public void clearLoginToken(UUser loginUser) throws Exception {
        /**
         * UUser uUser = loginUser;
         Map<String, Object> userMap = new HashMap<String, Object>();
         userMap.put("username", uUser.getUsername());
         userMap.put("loginToken", "null=" + userMap.get("username"));
         userMapper.updateLoginToken(userMap);
         */
    }
}
