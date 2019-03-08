package com.idatrix.unisecurity.core.shiro.cache.impl;

import com.idatrix.unisecurity.common.utils.LoggerUtils;
import com.idatrix.unisecurity.common.utils.SerializeUtil;
import com.idatrix.unisecurity.core.jedis.JedisClient;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;

import java.util.Collection;
import java.util.Set;

/**
 * shiro 缓存具体实现(crud)
 */
@SuppressWarnings("unchecked")
public class JedisShiroCache<K, V> implements Cache<K, V> {

    private static final Class<JedisShiroCache> SELF = JedisShiroCache.class;

    // 为了不和其他的缓存混淆，采用追加前缀方式以作区分
    private static final String REDIS_SHIRO_CACHE = "shiro-idatrix-cache";

    // Redis 分片(分区)，也可以在配置文件中配置
    private int DB_INDEX = 11;

    // redis操作工具类
    private JedisClient jedisClient;

    // 缓存在redis中的过期时间
    private  int expireTime = 1800 / 2; // 默认15分钟

    private String name;

    public JedisShiroCache(String name, JedisClient jedisClient, Integer DB_INDEX, Integer expireTime) {
        this.name = name;
        this.jedisClient = jedisClient;
        if(DB_INDEX != null) {
            this.DB_INDEX = DB_INDEX;
        }
        if(expireTime != null) {
            this.expireTime = expireTime;
        }
    }

    /**
     * 自定义relm中的授权/认证的类名加上授权/认证英文名字
     */
    public String getName() {
        if (name == null)
            return "";
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public V get(K key) throws CacheException {
        LoggerUtils.debug(this.getClass(), "======================shiro-cache：get cache====key：+"+ key +"==================");
        byte[] byteKey = SerializeUtil.serialize(buildCacheKey(key));
        byte[] byteValue = new byte[0];
        try {
            byteValue = jedisClient.get(DB_INDEX, byteKey);
        } catch (Exception e) {
            LoggerUtils.error(SELF, "get value by cache throw exception", e);
        }
        return (V) SerializeUtil.deserialize(byteValue);
    }

    @Override
    public V put(K key, V value) throws CacheException {
        LoggerUtils.debug(this.getClass(), "======================shiro-cache：put cache====key：+"+ key +"value：" + value + "==================");
        V previos = get(key);
        try {
            jedisClient.setex(DB_INDEX, SerializeUtil.serialize(buildCacheKey(key)), SerializeUtil.serialize(value), expireTime);
        } catch (Exception e) {
            LoggerUtils.error(SELF, "put cache throw exception", e);
        }
        return previos;
    }

    @Override
    public V remove(K key) throws CacheException {
        LoggerUtils.debug(this.getClass(), "======================shiro-cache：remove cache====key：+"+ key +"==================");
        V previos = get(key);
        try {
            jedisClient.del(DB_INDEX, SerializeUtil.serialize(buildCacheKey(key)));
        } catch (Exception e) {
            LoggerUtils.error(SELF, "remove cache  throw exception", e);
        }
        return previos;
    }

    @Override
    public void clear() throws CacheException {
        //TODO
    }

    @Override
    public int size() {
        if (keys() == null)
            return 0;
        return keys().size();
    }

    @Override
    public Set<K> keys() {
        //TODO
        return null;
    }

    @Override
    public Collection<V> values() {
        //TODO
        return null;
    }

    private String buildCacheKey(Object key) {
        return REDIS_SHIRO_CACHE + getName() + ":" + key;
    }
}
