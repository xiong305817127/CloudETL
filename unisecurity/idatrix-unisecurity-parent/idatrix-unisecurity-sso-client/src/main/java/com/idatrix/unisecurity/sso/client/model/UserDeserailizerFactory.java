package com.idatrix.unisecurity.sso.client.model;

/**
 * 用户反序列化器工厂
 */
public class UserDeserailizerFactory {

    private UserDeserailizerFactory() {
    }

    public static UserDeserializer create() {
        // 此处直接通过new方法实现
        // 若要实现更灵活配置方式，可通过配置文件或注解方式实现
        return new JsonUserDeserailizer();
    }
}
