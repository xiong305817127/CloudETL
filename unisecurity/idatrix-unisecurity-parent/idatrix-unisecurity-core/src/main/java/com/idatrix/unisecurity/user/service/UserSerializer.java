package com.idatrix.unisecurity.user.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.idatrix.unisecurity.common.domain.UUser;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户信息序列化工具
 */
public abstract class UserSerializer {

    /**
     * 用户数据类型
     */
    public static class UserData implements Serializable {
        private String id; // 唯一标识
        private Map<String, Object> properties = new HashMap<String, Object>(); // 其它属性

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Map<String, Object> getProperties() {
            return properties;
        }

        public void setProperties(Map<String, Object> properties) {
            this.properties.putAll(properties);
        }

        // 新增单个属性
        public void setProperty(String key, Object value) {
            this.properties.put(key, value);
        }
    }

    /**
     * 数据转换
     * @param loginUser
     * @throws Exception
     */
    protected abstract void translate(UUser loginUser, UserData userData) throws Exception;

    /**
     * 序列化
     * @param loginUser
     * @return
     * @throws Exception
     */
    public String serial(UUser loginUser) throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        UserData userData = new UserData();
        if (loginUser != null) {
            translate(loginUser, userData);
        }
        return mapper.writeValueAsString(userData);
    }

    public UserData serial(UUser loginUser, String code) throws Exception {
        UserData userData = new UserData();
        if (loginUser != null) {
            translate(loginUser, userData);
        }
        return userData;
    }
}
