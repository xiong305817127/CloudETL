package com.idatrix.unisecurity.core.shiro.filter;

import com.idatrix.unisecurity.common.enums.ResultEnum;
import com.idatrix.unisecurity.common.utils.GsonUtil;
import com.idatrix.unisecurity.common.utils.ResultVoUtils;
import com.idatrix.unisecurity.core.jedis.impl.VCache;
import com.idatrix.unisecurity.core.shiro.session.dao.ShiroSessionRepository;
import com.idatrix.unisecurity.core.shiro.token.manager.ShiroTokenManager;
import com.idatrix.unisecurity.user.Config;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户在a登录了，然后在b登录后，a处就会被踢下线
 * 会话并发数量控制filter
 */
public class KickoutSessionFilter extends AccessControlFilter {
	
	public Logger logger = LoggerFactory.getLogger(getClass());

    private static String ONLINE_USER = KickoutSessionFilter.class.getCanonicalName() + "_online_user:";// 在线用户列表在redis中的前缀

    public static String KICKOUT_STATUS = KickoutSessionFilter.class.getCanonicalName() + "_kickout_status";// 踢出状态，true标示踢出

    private boolean kickoutAfter = true; // 踢出之前登录的/之后登录的用户 默认踢出之前登录的用户

    private int maxSession = 1; // 同一个帐号最大会话数 默认1

    private int cacheTimeout = 1800; // 每个用户对应的在线用户列表缓存时间

    private String kickoutUrl;

    private VCache cache;

    private ShiroSessionRepository shiroSessionRepository;

    private Config config;

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        Subject subject = getSubject(request, response);
        if (!subject.isAuthenticated() && !subject.isRemembered()) {
            // 如果没有登录，直接进行之后的流程
            return Boolean.TRUE;
        }

        logger.info("KickoutSessionFilter 判断是否需要做踢出操作");
        Session session = subject.getSession(); // 当前会话
        Serializable sessionId = session.getId(); // 当前会话唯一标识
        Long userId = ShiroTokenManager.getUserId();// 获取userId
        String uniqueId = ONLINE_USER + userId; // 每个user对应的userList在redis中的唯一id

        // 每个用户对应的在线用户列表
        List<Serializable> userList = cache.get(uniqueId, List.class);
        // 如果不存在，创建一个新的map
        userList = userList == null ? new ArrayList<Serializable>(): userList;

        // 如果list里没有当前会话的sessionId 并且 会话没有标记
        if(!userList.contains(sessionId) && session.getAttribute(KICKOUT_STATUS) == null) {
            userList.add(sessionId);
            // 更新存储到缓存1个小时（这个时间最好和session的有效期一致或者大于session的有效期）
            cache.setex(uniqueId, userList, cacheTimeout);
        }

        // 如果list里的 sessionId 数超出最大会话数，开始踢人
        while(userList.size() > maxSession) {
            Serializable kickoutSessionId = null;
            if (kickoutAfter) { // 如果踢出后者
                kickoutSessionId = userList.remove(0);
            } else { // 否则踢出前者
                kickoutSessionId = userList.remove(userList.size() - 1);
            }
            // 更新缓存过期时间（这个时间最好和session的有效期一致或者大于session的有效期）
            cache.setex(uniqueId, userList, cacheTimeout);

            try {
                // 获取被踢出的 sessionId 的 session 对象
                Session kickoutSession = shiroSessionRepository.getSession(kickoutSessionId);
                if(kickoutSession != null) {
                    // 设置会话的kickout属性表示踢出了
                    kickoutSession.setAttribute(KICKOUT_STATUS, true);
                    shiroSessionRepository.saveSession(kickoutSession);// 更新session
                    // 通知客户端清除踢出用户的缓存
                    config.getAuthenticationHandler().systemLogout(ShiroTokenManager.getSessionId());
                }
            } catch (Exception e) {//ignore exception
                logger.error("会话踢出失败，userId：{}, sessionId：{}", userId, sessionId);
                e.printStackTrace();
            }
        }

        // 如果被踢出了，直接退出，重定向到踢出后的地址
        if (session.getAttribute(KICKOUT_STATUS) != null && (Boolean)session.getAttribute(KICKOUT_STATUS) == true) {
            return false;
        }
        return true;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        // 会话被踢出了
        Subject subject = getSubject(request, response);
        logger.debug("onAccessDenied>>> session id:" + subject.getSession().getId());
        try {
            // 客户端清除缓存
            config.getAuthenticationHandler().systemLogout(ShiroTokenManager.getSessionId());
            // 退出登录
            subject.logout();
        } catch (Exception e) { //ignore
        }
        out(response);
        return false;
    }

    public void out(ServletResponse response) {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json");
        try {
            httpServletResponse.getWriter().write(GsonUtil.toJson(ResultVoUtils.error(ResultEnum.USER_KICKED_OUT.getCode(), ResultEnum.USER_KICKED_OUT.getMessage())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setKickoutAfter(boolean kickoutAfter) {
        this.kickoutAfter = kickoutAfter;
    }

    public void setMaxSession(int maxSession) {
        this.maxSession = maxSession;
    }

    public void setCache(VCache cache) {
        this.cache = cache;
    }

    public void setShiroSessionRepository(ShiroSessionRepository shiroSessionRepository) {
        this.shiroSessionRepository = shiroSessionRepository;
    }

    public void setCacheTimeout(int cacheTimeout) {
        this.cacheTimeout = cacheTimeout;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public void setKickoutUrl(String kickoutUrl) {
        this.kickoutUrl = kickoutUrl;
    }
}
