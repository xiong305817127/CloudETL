package com.idatrix.unisecurity.core.jedis.impl;

import com.idatrix.unisecurity.core.jedis.JedisClient;
import redis.clients.jedis.JedisCluster;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName JedisClientCluster
 * @Description 操作redis，集群版，生产环境使用
 * @Author ouyang
 * @Date 2018/11/19 16:57
 * @Version 1.0
 */
public class JedisClientCluster implements JedisClient {

    private JedisCluster jedisCluster;

    public JedisCluster getJedisCluster() {
        return jedisCluster;
    }

    public void setJedisCluster(JedisCluster jedisCluster) {
        this.jedisCluster = jedisCluster;
    }

    @Override
    public String get(String key) {
        return null;
    }

    @Override
    public String set(String key, String value) {
        return null;
    }

    @Override
    public String setex(String key, String value, int expireTime) {
        return null;
    }

    @Override
    public byte[] get(int dbIndex, byte[] key) throws Exception {
        return new byte[0];
    }

    @Override
    public void setex(int dbIndex, byte[] key, byte[] value, int expireTime) throws Exception {

    }

    @Override
    public Long incr(String key) {
        return null;
    }

    @Override
    public Boolean exists(String key) {
        return null;
    }

    @Override
    public Long hset(String key, String field, String value) {
        return null;
    }

    @Override
    public String hget(String key, String field) {
        return null;
    }

    @Override
    public List<String> hmget(String key, String... item) {
        return null;
    }

    @Override
    public String hmset(String key, Map<String, String> param) {
        return null;
    }

    @Override
    public List<String> hvals(String key) {
        return null;
    }

    @Override
    public Boolean hexists(String key, String field) {
        return null;
    }

    @Override
    public Long hdel(String key, String... field) {
        return null;
    }

    @Override
    public Long expire(String key, int seconds) {
        return null;
    }

    @Override
    public Long ttl(String key) {
        return null;
    }

    @Override
    public Long del(String key) {
        return null;
    }

    @Override
    public void del(int dbIndex, byte[] key) throws Exception {

    }

    @Override
    public Set<byte[]> keys(int dbIndex, String key) throws Exception {
        return null;
    }

    @Override
    public Set<byte[]> values(int dbIndex, Set<byte[]> keys) throws Exception {
        return null;
    }
}
