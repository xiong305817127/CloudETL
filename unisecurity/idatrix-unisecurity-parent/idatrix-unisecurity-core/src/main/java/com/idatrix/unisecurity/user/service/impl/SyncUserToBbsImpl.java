package com.idatrix.unisecurity.user.service.impl;

import com.idatrix.unisecurity.properties.BbsProperties;
import com.idatrix.unisecurity.user.service.SyncUserToBbs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * @ClassName SyncUserToBbsImpl
 * @Description 同步用户到bbs中具体实现类
 * @Author ouyang
 * @Date 2018/11/7 10:12
 * @Version 1.0
 */
@Service
public class SyncUserToBbsImpl implements SyncUserToBbs {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private BbsProperties bbsProperties;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void addUser(String username, String password, String email, String mobile) {
        if(bbsProperties.getAddBBS()){
            try{
                log.debug("add user synchronized bbs，user：" + username);
                MultiValueMap<String, Object> requestParam = new LinkedMultiValueMap<>();
                requestParam.set("username", username);
                requestParam.set("password", password);
                requestParam.set("email", email);
                requestParam.set("mobile", mobile);
                requestParam.set("isSynchronous", true);
                ResponseEntity<Map> response = restTemplate.postForEntity(bbsProperties.getBbs_pre_url() + bbsProperties.getBbs_add_user(), requestParam, Map.class);
                Map map = response.getBody();
                if(map.get("code").equals(200)){
                    log.debug("add user synchronized bbs，success！！！");
                } else {
                    log.error("add user synchronized bbs，error！！！message：" + map.get("description"));
                }
            } catch (Exception e){
                e.printStackTrace();
                log.error("add user synchronized bbs，error！！！");
            }
        }
    }

    @Override
    public void updateUser(String username, String password, String phone, String email, Boolean encrypted) {
        if(bbsProperties.getAddBBS()){
            try{
                log.debug("update user synchronized bbs，user：" + username);
                MultiValueMap<String, Object> requestParam = new LinkedMultiValueMap<>();
                requestParam.set("username", username);
                requestParam.set("password", password);
                requestParam.set("phone", phone);
                requestParam.set("email", email);
                requestParam.set("encrypted", encrypted);
                ResponseEntity<Map> response = restTemplate.postForEntity(bbsProperties.getBbs_pre_url() + bbsProperties.getBbs_update_user(), requestParam, Map.class);
                Map map = response.getBody();
                if(map.get("code").equals(200)){
                    log.debug("update user synchronized bbs，success！！！");
                } else {
                    log.error("update user synchronized bbs，error！！！message：" + map.get("description"));
                }
            } catch (Exception e){
                e.printStackTrace();
                log.error("update user synchronized bbs，error！！！");
            }
        }
    }

    @Override
    public void deleteUser(String userNames) {
        if(bbsProperties.getAddBBS()){
            try{
                log.debug("delete user synchronized bbs，userNames：" + userNames);
                MultiValueMap<String, Object> requestParam = new LinkedMultiValueMap<>();
                requestParam.set("userNames", userNames);
                ResponseEntity<Map> response = restTemplate.postForEntity(bbsProperties.getBbs_pre_url() + bbsProperties.getBbs_delete_user(), requestParam, Map.class);
                Map map = response.getBody();
                if(map.get("code").equals(200)) {
                    log.debug("delete user synchronized bbs，success！！！");
                } else {
                    log.error("delete user synchronized bbs，error！！！message：" + map.get("description"));
                }
            } catch (Exception e){
                e.printStackTrace();
                log.error("delete user synchronized bbs，error！！！");
            }
        }
    }
}
