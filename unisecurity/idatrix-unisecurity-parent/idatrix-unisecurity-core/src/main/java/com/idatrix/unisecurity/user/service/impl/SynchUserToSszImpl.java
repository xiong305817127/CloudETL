package com.idatrix.unisecurity.user.service.impl;

import com.idatrix.unisecurity.common.domain.UUser;
import com.idatrix.unisecurity.properties.SszProperties;
import com.idatrix.unisecurity.user.service.SynchUserToSsz;
import com.idatrix.unisecurity.user.service.UUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

/**
 * @ClassName SynchUserToSszImpl
 * @Description 同步用户到神算子中具体实现类
 * @Author ouyang
 * @Date 2018/11/7 10:26
 * @Version 1.0
 */
@Service
public class SynchUserToSszImpl implements SynchUserToSsz {

    private Logger log = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private UUserService userService;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private SszProperties sszProperties;

    @Override
    public void importAll(){
        if(sszProperties.getAddSsz()){
            List<UUser> userList = userService.findAll();
            log.info("import all user synchronized ssz");
            for (UUser user : userList) {
                try{
                    log.info("import user：" + user.getUsername());
                    ResponseEntity<Map> response = restTemplate.getForEntity(sszProperties.getSsz_pre_url() + sszProperties.getSsz_add_user_url() + "?username={1}&appkey={2}", Map.class, user.getUsername(), user.getUsername());
                    Map map = response.getBody();
                    if(map.get("code").equals(0)) {
                        log.debug("import user success！！！username：" + user.getUsername());
                    } else {
                        log.error("import user error！！！username：" + user.getUsername() + "，message：" + map.get("msg"));
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                    log.error("import error！！！username：" + user.getRealName());
                }
            }
        }
    }

    @Override
    public void addUser(String userName) {
        if(sszProperties.getAddSsz()){
            try{
                log.info("add user synchronized ssz，user：" + userName);
                ResponseEntity<Map> response = restTemplate.getForEntity(sszProperties.getSsz_pre_url() + sszProperties.getSsz_add_user_url() + "?username={1}&appkey={2}", Map.class, userName, userName);
                Map map = response.getBody();
                if(map.get("code").equals(0)){
                    log.debug("add user synchronized ssz，success！！！");
                } else {
                    log.error("add user synchronized ssz，error！！！message：" + map.get("msg"));
                }
            } catch(Exception e) {
                e.printStackTrace();
                log.error("add user synchronized ssz，error！！！");
            }
        }
    }

    @Override
    public void deleteUser(String userNames) {
        if(sszProperties.getAddSsz()){
            try{
                log.info("delete user synchronized ssz，userNames：" + userNames);
                String[] userNameArr = userNames.split(",");
                for (String username : userNameArr) {
                    try {
                        ResponseEntity<Map> response = restTemplate.getForEntity(sszProperties.getSsz_pre_url() + sszProperties.getSsz_delete_user_url() + "?username={1}&appkey={2}", Map.class, username, username);
                        Map map = response.getBody();
                        if(map.get("code").equals(0)){
                            log.debug("delete user synchronized ssz，success！！！");
                        } else {
                            log.error("delete user synchronized ssz，error！！！username：" + username + "，message：" + map.get("msg"));
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                        log.error("delete user synchronized ssz，error！！！username：" + username);
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
                log.error("delete user synchronized ssz，error！！！");
            }
        }
    }

}
