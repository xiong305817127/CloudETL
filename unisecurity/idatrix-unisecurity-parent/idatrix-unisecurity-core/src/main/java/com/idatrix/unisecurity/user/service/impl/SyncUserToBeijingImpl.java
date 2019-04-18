package com.idatrix.unisecurity.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.idatrix.unisecurity.common.domain.UUser;
import com.idatrix.unisecurity.properties.BeijingProperties;
import com.idatrix.unisecurity.user.service.SyncUserToBeijing;
import com.idatrix.unisecurity.user.service.UUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @ClassName SyncUserToBeijingImpl
 * @Description
 * @Author ouyang
 * @Date
 */
@Slf4j
@Service
public class SyncUserToBeijingImpl implements SyncUserToBeijing {

    @Autowired
    private UUserService userService;

    @Autowired
    private BeijingProperties propertes;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void addUser(String username, String password, String realName, String email) {
        if(propertes.getIsAddBeijing()) {
            JSONObject json = new JSONObject();
            try{
                json.put("firstname", realName);
                json.put("lastname", realName);
                json.put("username", username);
                json.put("password", password);
                json.put("active", 1);
                json.put("email", email);
                JSONObject responseJson = restTemplate.postForEntity(propertes.getBeijingPreUrl() + propertes.getAddUserUrl(), json, JSONObject.class).getBody();
                Integer code = (Integer) responseJson.get("code");
                if(code.equals(200)){
                    log.debug("add user synchronized beijing success");
                } else {
                    log.error("add user synchronized beijing error, param ：{}, message：{}", json.toJSONString(), responseJson.get("message"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("add user synchronized beijing error, message:：{}", e.getMessage());
            }
        }
    }

    @Override
    public void updateUser(String username, String password, String realName, String email) {
        if(propertes.getIsAddBeijing()) {
            try{
                JSONObject json = new JSONObject();
                json.put("firstname", realName);
                json.put("lastname", realName);
                json.put("username", username);
                json.put("password", password);
                json.put("email", email);
                JSONObject responseJson = restTemplate.postForEntity(propertes.getBeijingPreUrl() + propertes.getUpdateUserUrl(), json, JSONObject.class).getBody();
                Integer code = (Integer) responseJson.get("code");
                if(code.equals(200)){
                    log.debug("update user synchronized beijing，success");
                } else {
                    log.error("update user synchronized beijing error, message：{}", responseJson.get("message"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("update user synchronized beijing error, message：{}", e.getMessage());
            }
        }
    }

    @Override
    public void delete(String userNames) {
        if(propertes.getIsAddBeijing()){
            log.debug("delete user synchronized beijing，userNames：" + userNames);
            for (String username : userNames.split(",")) {
                try{
                    MultiValueMap<String, Object> requestParam = new LinkedMultiValueMap<>();
                    requestParam.set("username", username);
                    JSONObject responseJson = restTemplate.postForEntity(propertes.getBeijingPreUrl() + propertes.getDeleteUserUrl(), requestParam, JSONObject.class).getBody();
                    Integer code = (Integer) responseJson.get("code");
                    if(code.equals(200)) {
                        log.debug("delete user username:{} synchronized beijing，success", username);
                    } else {
                        log.error("delete user username:{} synchronized beijing，error, message：{}", username, responseJson.get("message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("delete user username:{} synchronized beijing error, message：{}", username, e.getMessage());
                }
            }
        }
    }

    @Override
    public void importAll() {
        if(propertes.getIsAddBeijing()){
            List<UUser> userList = userService.findAll();
            log.info("import all user synchronized beijin");
            for (UUser user : userList) {
                addUser(user.getUsername(), user.getPswd(), user.getRealName(), user.getEmail());
            }
        }
    }
}