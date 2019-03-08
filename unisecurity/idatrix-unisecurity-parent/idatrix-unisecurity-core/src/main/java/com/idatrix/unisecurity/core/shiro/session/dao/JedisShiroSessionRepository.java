package com.idatrix.unisecurity.core.shiro.session.dao;

import com.idatrix.unisecurity.common.utils.LoggerUtils;
import com.idatrix.unisecurity.common.utils.SerializeUtil;
import com.idatrix.unisecurity.core.jedis.JedisClient;
import com.idatrix.unisecurity.core.shiro.session.CustomSessionManager;
import com.idatrix.unisecurity.core.shiro.session.SessionStatus;
import org.apache.shiro.session.Session;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * redis具体的实现
 * Session 管理(crud)
 */
@SuppressWarnings("unchecked")
public class JedisShiroSessionRepository implements ShiroSessionRepository {

    // 会话信息在redis中的前缀
    public static final String REDIS_SHIRO_SESSION = "shiro-idatrix-session:";

    // 获取所有会话时使用的key
    public static final String REDIS_SHIRO_ALL = "*shiro-idatrix-session:*";

    // 放在redis几号数据库中
    private int DB_INDEX = 10;

    // redis的操作类
    private JedisClient jedisClient;

    public void setJedisClient(JedisClient jedisClient) {
        this.jedisClient = jedisClient;
    }

    public void setDB_INDEX(int DB_INDEX) {
        this.DB_INDEX = DB_INDEX;
    }

    @Override
    public void saveSession(Session session) {
        if (session == null || session.getId() == null)
            throw new NullPointerException("session is empty");
        try {
            byte[] key = SerializeUtil.serialize(buildRedisSessionKey(session.getId()));
            // 不存在才添加。
            if (null == session.getAttribute(CustomSessionManager.SESSION_STATUS)) {
                // Session 踢出自存存储。
                SessionStatus sessionStatus = new SessionStatus();
                session.setAttribute(CustomSessionManager.SESSION_STATUS, sessionStatus);// 设置会话为在先状态
            }
            byte[] value = SerializeUtil.serialize(session);
            /**
             * 计算过期时间，在redis中的时间需要比session过期时间长。
             * 不然就会出现服务端TokenManager去同步子系统过期时间时redis中的session对象已经是没有了的。
             * 处理方案：redis中的session过期时间延长五分钟
             */
            Long sessionTimeOut = session.getTimeout() / 1000 + (5 * 60);// 取出session的过期时间，并且延时
            // 保存在redis中
            jedisClient.setex(DB_INDEX, key, value, sessionTimeOut.intValue());
        } catch (Exception e) {
            LoggerUtils.fmtError(getClass(), e, "save session error，id:[%s]", session.getId());
        }
    }

    public void saveSession(Session session, Long expireTime) {
        if (session == null || session.getId() == null)
            throw new NullPointerException("session is empty");
        try {
            byte[] key = SerializeUtil.serialize(buildRedisSessionKey(session.getId()));
            // 不存在才添加。
            if (null == session.getAttribute(CustomSessionManager.SESSION_STATUS)) {
                // Session 踢出自存存储。
                SessionStatus sessionStatus = new SessionStatus();
                session.setAttribute(CustomSessionManager.SESSION_STATUS, sessionStatus);//设置会话为在先状态
            }
            byte[] value = SerializeUtil.serialize(session);
            // 保存在redis中
            jedisClient.setex(DB_INDEX, key, value, expireTime.intValue());
        } catch (Exception e) {
            LoggerUtils.fmtError(getClass(), e, "save session error，id:[%s]", session.getId());
        }
    }

    @Override
    public void deleteSession(Serializable id) {
        if (id == null) {
            throw new NullPointerException("session id is empty");
        }
        try {
            jedisClient.del(DB_INDEX, SerializeUtil.serialize(buildRedisSessionKey(id)));
        } catch (Exception e) {
            LoggerUtils.fmtError(getClass(), e, "删除session出现异常，id:[%s]", id);
        }
    }

    @Override
    public Session getSession(Serializable id) {
        if (id == null)
            throw new NullPointerException("session id is empty");
        Session session = null;
        try {
            byte[] value = jedisClient.get(DB_INDEX, SerializeUtil.serialize(buildRedisSessionKey(id)));
            session = SerializeUtil.deserialize(value, Session.class);
        } catch (Exception e) {
            LoggerUtils.fmtError(getClass(), e, "获取session异常，id:[%s]", id);
        }
        return session;
    }

    @Override
    public Collection<Session> getAllSessions() {
        Collection<Session> sessions = new ArrayList<>();
        try {
            Set<byte[]> keys = jedisClient.keys(DB_INDEX, REDIS_SHIRO_ALL);
            Set<byte[]> values = jedisClient.values(DB_INDEX, keys);
            for (byte[] value : values) {
                Session obj = SerializeUtil.deserialize(value, Session.class);
                sessions.add(obj);
            }
            return sessions;
        } catch (Exception e) {
            LoggerUtils.fmtError(getClass(), e, "获取全部session异常");
        }
        return sessions;
    }

    /**
     * 拼接出在 redis 中的sessionId
     * @param sessionId
     * @return
     */
    private String buildRedisSessionKey(Serializable sessionId) {
        return REDIS_SHIRO_SESSION + sessionId;
    }
}
