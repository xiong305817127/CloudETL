package com.idatrix.unisecurity.sso.client;

import com.idatrix.unisecurity.sso.client.enums.ResultEnum;
import com.idatrix.unisecurity.sso.client.model.SSOUser;
import com.idatrix.unisecurity.sso.client.model.UserDeserailizerFactory;
import com.idatrix.unisecurity.sso.client.model.UserDeserializer;
import com.idatrix.unisecurity.sso.client.utils.GsonUtil;
import com.idatrix.unisecurity.sso.client.utils.ResultVoUtils;
import com.idatrix.unisecurity.sso.client.vo.ResultVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 客户端：
 * 令牌管理工具
 */
public class TokenManager {

    private static Logger logger = LoggerFactory.getLogger(TokenManager.class);

    public static String serverIndderAddress; // 服务端内网通信地址

    public static String projectName; // 服务端项目名称

    // 客户端令牌管理器
    private final static ConcurrentHashMap<String, Token> LOCAL_CACHE = new ConcurrentHashMap<String, Token>();

    // 复合结构体，含SSOUser与最后访问时间lastAccessTime两个成员
    private static class Token {
        private SSOUser user; //用户信息
        private Date lastAccessTime; //用户最后访问时间
    }

    private TokenManager() {
    }

    /**
     * 验证vt有效性，先从客户端缓存中验证有效性，如果客户端中无效，那么与远程服务端通讯验证有效性
     * @param vt
     * @return
     * @throws Exception
     */
    public static SSOUser validate(String vt) throws Exception {
        logger.debug("validate 验证vt有效性：" + vt);
    	// 验证本地缓存中是否有效
        SSOUser user = localValidate(vt);
        if (user == null) {
            // 如果本地缓存中没有，从远程服务上找
        	logger.debug("remoteValidate 验证vt有效性 begin :"+vt);
            user = remoteValidate(vt);
        }
        return user;
    }

    /**
     * 验证vt有效性，先从客户端缓存中验证有效性，如果客户端中无效，那么与远程服务端通讯验证有效性 2018.10.31版本
     * @param vt
     * @return
     * @throws Exception
     */
    public static ResultVo validate(String vt, String code) throws Exception {
        logger.debug("validate 验证vt有效性：" + vt);
        // 验证客户端本地缓存中是否有效
        SSOUser user = localValidate(vt);
        if (user != null) {
            return ResultVoUtils.ok(user);
        }
        // 如果客户端本地缓存中没有，从远程服务上找
        logger.debug("remoteValidate 验证vt有效性 begin :"+vt);
        ResultVo resultVo = remoteValidate(vt, "aa");
        return resultVo;
    }

    // 客户端缓存验证 VT 有效性
    private static SSOUser localValidate(String vt) {
        // 从缓存中查找数据
        Token token = LOCAL_CACHE.get(vt);
        if (token != null) { // 用户数据存在
            // 更新最后访问时间
            token.lastAccessTime = new Date();
            // 返回结果
            return token.user;
        }
        return null;
    }

    // 服务端验证 VT 有效性
    private static SSOUser remoteValidate(String vt) throws Exception {
        String address;
        // 拼接出服务端的url
        if (StringUtils.isNotEmpty(projectName)) {
            address = serverIndderAddress + projectName + "/validate_service?vt=" + vt;
        } else {
            address = serverIndderAddress + "/validate_service?vt=" + vt;
        }

        //发送请求到服务端，获取返回值
        URL url = new URL(address);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        InputStream is = conn.getInputStream();
        conn.connect();

        byte[] buff = new byte[is.available()];
        is.read(buff);
        String ret = new String(buff, "utf-8");
        conn.disconnect();
        is.close();
        // 获取用户序列化对象
        UserDeserializer userDeserializer = UserDeserailizerFactory.create();
        // 判断返回值是否为空，不为空则序列化
        SSOUser user = StringUtils.isEmpty(ret) ? null : userDeserializer.deserail(ret);
        if (user != null) {
            logger.debug("请求远程服务端获取用户信息成功 vt = " + vt);
            logger.debug("请求远程服务端获取用户信息成功 vt = " + GsonUtil.toJson(user));
            cacheUser(vt, user);
        }
        return user;
    }

    // 服务端验证 VT 有效性 2018.10.31版本
    private static ResultVo remoteValidate(String vt, String code) throws Exception {
        String address;
        // 拼接出服务端的url
        if (StringUtils.isNotEmpty(projectName)) {
            address = serverIndderAddress + projectName + "/validate_service?vt=" + vt;
        } else {
            address = serverIndderAddress + "/validate_service?vt=" + vt;
        }

        try {
            // 发送请求到服务端，获取返回值
            URL url = new URL(address);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            InputStream is = conn.getInputStream();
            conn.connect();
            byte[] buff = new byte[is.available()];
            is.read(buff);
            String reusltJson = new String(buff, "utf-8");
            conn.disconnect();
            is.close();

            if(StringUtils.isEmpty(reusltJson)) {
                // 如果客户端返回的是空的数据，直接返回是未登录就好。
                return ResultVoUtils.error(ResultEnum.USER_NOT_LOGIN.getCode(), ResultEnum.USER_NOT_LOGIN.getMessage());
            }

            ResultVo resultVo = GsonUtil.fromJson(reusltJson, ResultVo.class);
            if(resultVo.getCode().equals("200")) {
                logger.debug("请求远程服务端获取用户信息成功 vt = " + vt);
                logger.debug("请求远程服务端获取用户信息成功 result = " + resultVo);
                String userJson = (String) resultVo.getData();
                // 获取用户序列化对象
                UserDeserializer userDeserializer = UserDeserailizerFactory.create();
                // 判断返回值是否为空，不为空则序列化，注意，如果是属性为null的不会序列化到属性中的！！！！
                SSOUser ssoUser = userDeserializer.deserail(userJson);
                resultVo.setData(ssoUser);
                // 同步到客户端令牌管理中
                TokenManager.cacheUser(vt, ssoUser);
            }
            return resultVo;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("ssofilter remoteValidate error message：" + e.getMessage());
            return ResultVoUtils.error(ResultEnum.USER_NOT_LOGIN.getCode(), "安全系统可能在重启，请稍等或联系安全系统开发人员");
        }
    }

    // 远程验证 VT 成功后将用户信息写入客户端缓存中
    public static void cacheUser(String vt, SSOUser user) {
        Token token = new Token();
        token.user = user;//用户信息
        token.lastAccessTime = new Date();//最后的活动时间
        LOCAL_CACHE.put(vt, token);
    }

    /**
     * 客户端
     * 处理服务端发送的timeout通知
     * @param vt 令牌
     * @param tokenTimeout 服务端设置的过期时间长
     * @return
     */
    public static Date timeout(String vt, int tokenTimeout) {
        // 从客户端缓存中拿到用户
        Token token = LOCAL_CACHE.get(vt);
        logger.debug("client timeout vt：" + vt);
        // 如果用户不为空，那么计算出当前用户过期时间点，比较一下
        if (token != null) {
            // 获取当前客户端中用户最后的活动时间
            Date lastAccessTime = token.lastAccessTime;
            // 计算当前用户过期时间点
            Calendar ca = Calendar.getInstance();
            ca.setTime(lastAccessTime);
            ca.add(Calendar.MINUTE, tokenTimeout);
            Date expires = ca.getTime(); 
            Date now = new Date();
            logger.debug("client timeout expires: " + expires);

            // 客户端会话过期时间 vs 当前时间
            if (expires.compareTo(now) < 0) { // 客户端会话已过期
            	logger.debug("timeout remove vt: "+vt);
                // 从本地缓存移除
            	invalidate(vt);
                // 返回null表示当前客户端缓存已过期
                return null;
            } else {
                // 如果当前客户端未过期，那么将当前客户端用户过期时间点返回
                return expires;
            }
        } else {
            return null;
        }
    }

    /**
     * 用户退出时 或 过期时间到了 则失效对应缓存
     * @param vt
     */
    public static void invalidate(String vt) {
        LOCAL_CACHE.remove(vt); // 令牌管理器删除缓存
        UserHolder.remove(vt); // 业务缓存删除缓存
    }

    /**
     * 服务端应用关闭时清空本地缓存，失效所有信息
     */
    public static void destroy() {
        LOCAL_CACHE.clear();
    }

    public static String validateLT(String lt) {
        if(StringUtils.isBlank(lt)) {
            return null;
        }
        String vt = null;
        String address;
        // 拼接出服务端的url
        if (StringUtils.isNotEmpty(projectName)) {
            address = serverIndderAddress + projectName + "/role/list.shtml";
        } else {
            address = serverIndderAddress + "/role/list.shtml";
        }
        try {
            // 发送请求到服务端，获取返回值
            URL url = new URL(address);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Cookie", "LT=" + lt);
            InputStream is = conn.getInputStream();
            conn.connect();
            byte[] buff = new byte[is.available()];
            is.read(buff);
            conn.disconnect();
            is.close();

            // 获取cookie
            Map<String,List<String>> map = conn.getHeaderFields();
            List<String> cookies = map.get("Set-Cookie");
            for (String str : cookies) {
                if(str.startsWith("VT=")) {
                    vt = str;
                }
            }
            return vt;
        } catch (Exception e) {
            e.printStackTrace();
            return vt;
        }
    }
}
