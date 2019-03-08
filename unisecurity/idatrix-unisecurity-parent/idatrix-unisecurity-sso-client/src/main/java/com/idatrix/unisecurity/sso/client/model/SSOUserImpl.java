package com.idatrix.unisecurity.sso.client.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * SSOUser的实现，服务端生成此对象实例并序列化后传输给客户端
 */
@SuppressWarnings("serial")
public class SSOUserImpl implements SSOUser {

    private String id;

    private Map<String, Object> PROPERTY_MAP = new HashMap<String, Object>();//所有的属性

    private Map<String, Object> properties;

    public SSOUserImpl() {
    }

    public SSOUserImpl(String id, Map<String, Object> properties) {
        this.id = id;
        this.properties = properties;
        setProperties(properties);
    }

    /**
     * 写入属性
     */
    public void setProperties(Map<String, Object> properties) {
        if(PROPERTY_MAP == null) {
            PROPERTY_MAP = new HashMap<String, Object>();//所有的属性
        }
        PROPERTY_MAP.putAll(properties);
    }

    /**
     * 获取属性
     */
    public Map<String, Object> getProperties() {
        return properties;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Object getProperty(String propertyName) {
        return PROPERTY_MAP.get(propertyName);
    }

    @Override
    public Set<String> propertyNames() {
        return PROPERTY_MAP.keySet();
    }

    @Override
    public String toString() {
        return "SSOUserImpl{" +
                "id='" + id + '\'' +
                ", PROPERTY_MAP=" + PROPERTY_MAP +
                ", properties=" + properties +
                '}';
    }
}
