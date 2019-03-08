package com.idatrix.unisecurity.sso.client.model;

import com.idatrix.unisecurity.sso.client.utils.GsonUtil;

/**
 * 反序列化JSON格式数据
 */
public class JsonUserDeserailizer implements UserDeserializer {
/*
    private final ObjectMapper mapper = new ObjectMapper();*/

    // 反序列化
    @SuppressWarnings("unchecked")
    @Override
    public SSOUser deserail(String userData) throws Exception {
        SSOUserImpl ssoUser = GsonUtil.fromJson(userData, SSOUserImpl.class);
        ssoUser.setProperties(ssoUser.getProperties());
        return ssoUser;
/*
        JsonNode rootNode = mapper.readTree(userData);
        String id = rootNode.get("id").getTextValue();
        if (id == null) {
            return null;
        } else {
            JsonNode properties = rootNode.get("properties");
            Map<String, Object> propertyMap = mapper.readValue(properties.toString(), HashMap.class);
            SSOUserImpl user = new SSOUserImpl(id, propertyMap);
            user.setProperties(propertyMap);
            return user;
        }*/
    }

}
