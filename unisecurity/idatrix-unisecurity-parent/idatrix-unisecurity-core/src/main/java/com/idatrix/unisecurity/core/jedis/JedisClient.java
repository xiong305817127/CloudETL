package com.idatrix.unisecurity.core.jedis;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName JedisClient
 * @Description
 * @Author ouyang
 * @Date 2018/11/19 16:41
 * @Version 1.0
 */
public interface JedisClient {

    // string 获取值
    String get(String key);

    // string 设置值
    String set(String key, String value);

    // string 设置值，并且指定过期时间
    String setex(String key, String value, int expireTime);

    // 字节 获取值 redis不支持直接将object类型对象存储到redis中，所以转成byte类型
    byte[] get(int dbIndex, byte[] key) throws Exception;

    // 字节 设置值并且指定过期时间
    void setex(int dbIndex, byte[] key, byte[] value, int expireTime) throws Exception;

    // 自增
    Long incr(String key);

    // 判断是否存在
    Boolean exists(String key);

    // hash 设置值
    Long hset(String key, String field, String value);

    // hash 获取值
    String hget(String key, String field);

    // hmget 一次获取一个或多个给定字段的值。
    List<String> hmget(String key, String... item);

    // 一次设置多个值
    String hmset(String key, Map<String, String> param);

    // hash 获取键下的所有值
    List<String> hvals(String key);

    // hash 判断是否存在某个属性
    Boolean hexists(String key, String field);

    // hash 删除属性
    Long hdel(String key, String... field);

    // 设置过期时间
    Long expire(String key, int seconds);

    // 判断是否过期
    Long ttl(String key);

    // 删除键
    Long del(String key);

    // 字节 删除键
    void del(int dbIndex, byte[] key) throws Exception;

    // 模糊查询所有byte key
    Set<byte[]> keys(int dbIndex, String key) throws Exception;

    // 根据 byte keys 查询 byte values
    Set<byte[]> values(int dbIndex, Set<byte[]> keys) throws Exception;

}
