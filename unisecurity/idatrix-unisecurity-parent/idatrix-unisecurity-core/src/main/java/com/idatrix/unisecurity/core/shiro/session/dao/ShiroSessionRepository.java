package com.idatrix.unisecurity.core.shiro.session.dao;

import org.apache.shiro.session.Session;

import java.io.Serializable;
import java.util.Collection;

/**
 * 定义 session 操作的 dao 接口
 */
public interface ShiroSessionRepository {

    /**
     * 存储Session
     * @param session
     */
    void saveSession(Session session);

    /**
     * 存储Session，指定过期时间
     * @param session
     * @param expireTime
     */
    void saveSession(Session session, Long expireTime);

    /**
     * 删除session
     * @param sessionId
     */
    void deleteSession(Serializable sessionId);

    /**
     * 获取session
     * @param sessionId
     * @return
     */
    Session getSession(Serializable sessionId);

    /**
     * 获取所有sessoin
     * @return
     */
    Collection<Session> getAllSessions();
}
