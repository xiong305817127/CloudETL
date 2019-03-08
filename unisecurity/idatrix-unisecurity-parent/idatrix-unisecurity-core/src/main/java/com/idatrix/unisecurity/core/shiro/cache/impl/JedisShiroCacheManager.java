package com.idatrix.unisecurity.core.shiro.cache.impl;

import com.idatrix.unisecurity.core.jedis.JedisClient;
import com.idatrix.unisecurity.core.shiro.cache.ShiroCacheManager;
import org.apache.shiro.cache.Cache;

/**
 * redis 实现缓存管理
 */
public class JedisShiroCacheManager implements ShiroCacheManager {

    private JedisClient jedisClient;

    private Integer DB_INDEX;

    private Integer expireTime;

    @Override
    public <K, V> Cache<K, V> getCache(String name) {
        return new JedisShiroCache<K, V>(name, jedisClient, DB_INDEX, expireTime);
    }

    @Override
    public void destroy() {
        //如果和其他系统，或者应用在一起就不能关闭
        //getJedisManager().getJedis().shutdown();
    }

    public void setExpireTime(Integer expireTime) {
        this.expireTime = expireTime;
    }

    public void setJedisClient(JedisClient jedisClient) {
        this.jedisClient = jedisClient;
    }

    public void setDB_INDEX(int DB_INDEX) {
        this.DB_INDEX = DB_INDEX;
    }
}
