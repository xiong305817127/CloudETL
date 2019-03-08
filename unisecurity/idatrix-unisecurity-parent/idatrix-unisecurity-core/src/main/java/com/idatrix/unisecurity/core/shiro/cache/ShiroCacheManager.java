package com.idatrix.unisecurity.core.shiro.cache;

import org.apache.shiro.cache.Cache;

/**
 * 定义缓存管理接口
 */
public interface ShiroCacheManager {

    <K, V> Cache<K, V> getCache(String name);

    void destroy();
}
