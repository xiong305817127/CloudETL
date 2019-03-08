package com.idatrix.unisecurity.core.jedis.impl;

import com.idatrix.unisecurity.common.utils.LoggerUtils;
import com.idatrix.unisecurity.common.utils.SecurityStringUtils;
import com.idatrix.unisecurity.core.jedis.JedisClient;
import org.slf4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 操作redis
 * 单机版，开发时使用
 */
public class JedisClientPool implements JedisClient {

    private Logger logger = org.slf4j.LoggerFactory.getLogger(getClass());

    private JedisPool jedisPool;

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    /**
     * 获取redis连接
     *
     * @return
     */
    public Jedis getJedis() {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
        } catch (JedisConnectionException e) {
            String message = SecurityStringUtils.trim(e.getMessage());
            if ("Could not get a resource from the pool".equalsIgnoreCase(message)) {
                logger.error("请检查你的redis服务：{}", e.getMessage());
            }
            throw new JedisConnectionException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return jedis;
    }

    /**
     * 关闭连接
     *
     * @param jedis
     */
    public void close(Jedis jedis) {
        if (jedis == null)
            return;
        jedis.close();
    }

    @Override
    public String get(String key) {
        Jedis jedis = getJedis();
        String result = jedis.get(key);
        jedis.close();
        return result;
    }

    @Override
    public String set(String key, String value) {
        Jedis jedis = getJedis();
        String result = jedis.set(key, value);
        jedis.close();
        return result;
    }

    @Override
    public String setex(String key, String value, int expireTime) {
        Jedis jedis = getJedis();
        String result = jedis.setex(key, expireTime, value);
        jedis.close();
        return result;
    }

    @Override
    public byte[] get(int dbIndex, byte[] key) throws Exception {
        Jedis jedis = null;
        byte[] result = null;
        try {
            jedis = getJedis();
            jedis.select(dbIndex);
            result = jedis.get(key);
        } catch (Exception e) {
            throw e;
        } finally {
            close(jedis);
        }
        return result;
    }

    @Override
    public void setex(int dbIndex, byte[] key, byte[] value, int expireTime)
            throws Exception {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.select(dbIndex);
            if (expireTime > 0) {
                jedis.setex(key, expireTime, value);
            } else {
                jedis.set(key, value);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            close(jedis);
        }
    }

    @Override
    public Long incr(String key) {
        Jedis jedis = getJedis();
        Long result = jedis.incr(key);
        jedis.close();
        return result;
    }

    @Override
    public Long hset(String key, String field, String value) {
        Jedis jedis = getJedis();
        Long result = jedis.hset(key, field, value);
        jedis.close();
        return result;
    }

    @Override
    public String hget(String key, String field) {
        Jedis jedis = getJedis();
        String result = jedis.hget(key, field);
        jedis.close();
        return result;
    }

    @Override
    public List<String> hmget(String key, String... item) {
        Jedis jedis = getJedis();
        List<String> result = jedis.hmget(key, item);
        jedis.close();
        return result;
    }

    @Override
    public String hmset(String key, Map<String, String> param) {
        Jedis jedis = getJedis();
        String result = jedis.hmset(key, param);
        return result;
    }

    @Override
    public Long hdel(String key, String... field) {
        Jedis jedis = getJedis();
        Long result = jedis.hdel(key, field);
        jedis.close();
        return result;
    }

    @Override
    public Boolean hexists(String key, String field) {
        Jedis jedis = getJedis();
        Boolean result = jedis.hexists(key, field);
        jedis.close();
        return result;
    }

    @Override
    public List<String> hvals(String key) {
        Jedis jedis = getJedis();
        List<String> result = jedis.hvals(key);
        jedis.close();
        return result;
    }

    @Override
    public Long expire(String key, int seconds) {
        Jedis jedis = getJedis();
        Long result = jedis.expire(key, seconds);
        jedis.close();
        return result;
    }

    @Override
    public Long del(String key) {
        Jedis jedis = getJedis();
        Long result = jedis.del(key);
        jedis.close();
        return result;
    }

    @Override
    public void del(int dbIndex, byte[] key) throws Exception {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.select(dbIndex);
            Long result = jedis.del(key);
            LoggerUtils.fmtDebug(getClass(), "删除结果：{}", result);
        } catch (Exception e) {
            throw e;
        } finally {
            close(jedis);
        }
    }

    // 模糊查询所有byte key
    public Set<byte[]> keys(int dbIndex, String key) throws Exception {
        Jedis jedis = null;
        Set<byte[]> byteKeys = new HashSet<>();
        try {
            jedis = getJedis();
            jedis.select(dbIndex);
            byteKeys = jedis.keys((key).getBytes("UTF-8"));
        } catch (Exception e) {
            throw e;
        } finally {
            close(jedis);
        }
        return byteKeys;
    }

    // 根据 byte keys 查询 byte values
    public Set<byte[]> values(int dbIndex, Set<byte[]> keys) throws Exception {
        Jedis jedis = null;
        Set<byte[]> values = new HashSet<byte[]>();
        try {
            jedis = getJedis();
            jedis.select(dbIndex);
            if (keys != null && keys.size() > 0) {
                for (byte[] bytekey : keys) {
                    byte[] byteValue = jedis.get(bytekey);
                    values.add(byteValue);
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            close(jedis);
        }
        return values;
    }

    @Override
    public Boolean exists(String key) {
        Jedis jedis = getJedis();
        Boolean exists = jedis.exists(key);
        jedis.close();
        return exists;
    }

    @Override
    public Long ttl(String key) {
        Jedis jedis = getJedis();
        Long result = jedis.ttl(key);
        jedis.close();
        return result;
    }

    public Set<String> zrange(String key, int start, int end) {
        Jedis jedis = getJedis();
        Set<String> result = jedis.zrange(key, start, end);
        jedis.close();
        return result;
    }

    public Long zadd(String key, double score, String member){
        Jedis jedis = getJedis();
        Long result = jedis.zadd(key, score, member);
        jedis.close();
        return result;
    }

    public Long zrem(String key, String ... members) {
        Jedis jedis = getJedis();
        Long result = jedis.zrem(key, members);
        jedis.close();
        return result;
    }


    /*@SuppressWarnings("unchecked")
    @Override
    public Collection<Session> allSession(int dbIndex, String redisShiroSession) throws Exception {
        Jedis jedis = null;
        Set<Session> sessions = new HashSet<Session>();
        try {
            jedis = getJedis();
            jedis.select(dbIndex);
            Set<byte[]> byteKeys = jedis.keys((JedisShiroSessionRepository.REDIS_SHIRO_ALL).getBytes("UTF-8"));
            if (byteKeys != null && byteKeys.size() > 0) {
                for (byte[] bs : byteKeys) {
                    Session obj = SerializeUtil.deserialize(jedis.get(bs),
                            Session.class);
                    if (obj != null)
                        sessions.add(obj);
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            close(jedis);
        }
        return sessions;
    }*/
}
