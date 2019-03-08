package com.idatrix.unisecurity.sso.client.model;

/**
 * 将服务端传来的user数据反序列化
 */
public interface UserDeserializer {

    /**
     * 反序列化
     * 
     * @return
     * @throws Exception
     */
    SSOUser deserail(String userDate) throws Exception;
}
