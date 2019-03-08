package com.idatrix.unisecurity.core.shiro.session;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.servlet.ShiroHttpServletRequest;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.WebSessionKey;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import java.io.Serializable;

/**
 * 自定义session管理器
 * shiro web项目默认使用的是DefaultWebSessionManager
 */
public class SecurityWebSessionManager extends DefaultWebSessionManager {

    private Logger log = LoggerFactory.getLogger(getClass());

    /**
     * Stores the Session's ID, usually as a Cookie, to associate with future requests.
     */
    protected void onStart(Session session, SessionContext context) {
        super.onStart(session, context);
        ServletRequest request = WebUtils.getRequest(context);
        if(request == null){
            return ;
        }
        request.setAttribute(ShiroHttpServletRequest.REFERENCED_SESSION_ID_SOURCE, ShiroHttpServletRequest.COOKIE_SESSION_ID_SOURCE);
    }

    /**
     * 解决单次请求需要多次访问redis
     * @param sessionKey
     * @return
     * @throws UnknownSessionException
     */
    @Override
    protected Session retrieveSession(SessionKey sessionKey) throws UnknownSessionException {
        Serializable sessionId = getSessionId(sessionKey);
        ServletRequest request = null;
        if (sessionKey instanceof WebSessionKey) {
            request = ((WebSessionKey) sessionKey).getServletRequest();
        }

        // 从request中直接取session
        if (request != null && sessionId != null) {
            Object sessionObj = request.getAttribute(sessionId.toString());
            if (sessionObj != null) {
                log.debug("SecurityWebSessionManager 从request中获取session");
                return (Session) sessionObj;
            }
        }

        // request中没有获取session，那么从redis中取
        log.debug("SecurityWebSessionManager 从redis中获取session");
        Session session = super.retrieveSession(sessionKey);
        if (request != null && null != sessionId) {
            request.setAttribute(sessionId.toString(), session);
        }
        return session;
    }
}
