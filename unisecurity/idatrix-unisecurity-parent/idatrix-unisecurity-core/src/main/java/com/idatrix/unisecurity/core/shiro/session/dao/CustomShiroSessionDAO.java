package com.idatrix.unisecurity.core.shiro.session.dao;

import com.idatrix.unisecurity.common.utils.LoggerUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Collection;

/**
 * 自定义的一个session dao（session的crud）
 */
public class CustomShiroSessionDAO extends AbstractSessionDAO {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private ShiroSessionRepository shiroSessionRepository;

    public void setShiroSessionRepository(ShiroSessionRepository shiroSessionRepository) {
        this.shiroSessionRepository = shiroSessionRepository;
    }

    /**
     * 读取session
     * @param sessionId
     * @return
     */
    @Override
    public Session doReadSession(Serializable sessionId) {
        logger.debug("======================shiro-sessionDao：read session======================");
        return shiroSessionRepository.getSession(sessionId);
    }

    /**
     * 创建session
     * @param session
     * @return
     */
    @Override
    public Serializable doCreate(Session session) {
        logger.debug("======================shiro-sessionDao：create session======================");
        // 获取sessionId
        Serializable sessionId = this.generateSessionId(session);
        // 设置sessionId
        this.assignSessionId(session, sessionId);
        shiroSessionRepository.saveSession(session);
        return sessionId;
    }

    /**
     * 修改session
     * @param session
     * @throws UnknownSessionException
     */
    @Override
    public void update(Session session) throws UnknownSessionException {
        logger.debug("======================shiro-sessionDao：update session======================");
        shiroSessionRepository.saveSession(session);
    }

    /**
     * 删除session
     * @param session
     */
    @Override
    public void delete(Session session) {
        logger.debug("======================shiro-sessionDao：delete session======================");
        if (session == null) {
            LoggerUtils.error(getClass(), "Session 不能为null");
            return;
        }
        Serializable id = session.getId();
        if (id != null)
            shiroSessionRepository.deleteSession(id);
    }

    /**
     * 获取所有的session
     * @return
     */
    @Override
    public Collection<Session> getActiveSessions() {
        return shiroSessionRepository.getAllSessions();
    }
}
