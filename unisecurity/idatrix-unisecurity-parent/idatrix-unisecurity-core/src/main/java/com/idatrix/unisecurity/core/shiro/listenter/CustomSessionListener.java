package com.idatrix.unisecurity.core.shiro.listenter;

import com.idatrix.unisecurity.core.shiro.session.dao.ShiroSessionRepository;
import com.idatrix.unisecurity.properties.LoginProperties;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 监听每一个会话的创建和销毁
 * shiro session listener
 */
public class CustomSessionListener implements SessionListener {

    private Logger log = LoggerFactory.getLogger(getClass());

    private ShiroSessionRepository shiroSessionRepository;

    private LoginProperties loginProperties;

    /**
     * 一个回话的生命周期开始，那么有可能就是一个会话的结束了
     */
    @Override
    public void onStart(Session session) {
        /*
        try {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            String userCookie = CookieUtils.getCookieValue(request, loginProperties.getCookieUserNameKey());
            if(StringUtils.isNotEmpty(userCookie)) {
                // 删除cookie中的用户名
                CookieUtils.deleteCookie(request, response, loginProperties.getCookieUserNameKey(), loginProperties.getSecureMode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("delete cookie username error：{}", e.getMessage());
        }*/
        log.info("shiro session listener create session sessionId：" + session.getId());
    }

    /**
     * 一个回话的生命周期结束
     */
    @Override
    public void onStop(Session session) {
        log.info("shiro session listener session stop sessionId：" + session.getId());
    }

    @Override
    public void onExpiration(Session session) {
        shiroSessionRepository.deleteSession(session.getId());
    }

    public ShiroSessionRepository getShiroSessionRepository() {
        return shiroSessionRepository;
    }

    public void setShiroSessionRepository(ShiroSessionRepository shiroSessionRepository) {
        this.shiroSessionRepository = shiroSessionRepository;
    }

    public void setLoginProperties(LoginProperties loginProperties) {
        this.loginProperties = loginProperties;
    }
}

