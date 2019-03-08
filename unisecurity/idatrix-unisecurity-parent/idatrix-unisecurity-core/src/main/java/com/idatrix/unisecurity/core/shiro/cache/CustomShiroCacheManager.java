package com.idatrix.unisecurity.core.shiro.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.util.Destroyable;

/**
 * Destroyable：Shiro bean销毁时调用 Destroyable接口回调
 */
public class CustomShiroCacheManager implements CacheManager, Destroyable {

    private ShiroCacheManager shiroCacheManager;

    @Override
    public <K, V> Cache<K, V> getCache(String name) throws CacheException {
        return shiroCacheManager.getCache(name);
    }

    @Override
    public void destroy() throws Exception {
        shiroCacheManager.destroy();
    }

    public void setShiroCacheManager(ShiroCacheManager shiroCacheManager) {
        this.shiroCacheManager = shiroCacheManager;
    }
}
