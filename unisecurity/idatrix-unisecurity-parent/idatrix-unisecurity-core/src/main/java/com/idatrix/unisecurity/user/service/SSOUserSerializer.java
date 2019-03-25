package com.idatrix.unisecurity.user.service;

import com.idatrix.unisecurity.common.domain.UUser;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 序列化
 */
@Service
public class SSOUserSerializer extends UserSerializer {

    @Override
    protected void translate(UUser loginUser, UserData userData) throws Exception {
        if(loginUser != null) {
            userData.setId(String.valueOf(loginUser.getId()));
            userData.setProperty("username", loginUser.getUsername());
            userData.setProperty("realName", loginUser.getRealName());
            userData.setProperty("sex", StringUtils.isEmpty(loginUser.getSex())? "" : String.valueOf(loginUser.getSex()));
            userData.setProperty("age", StringUtils.isEmpty(loginUser.getAge())? "" : String.valueOf(loginUser.getAge()));
            userData.setProperty("email", loginUser.getEmail());
            userData.setProperty("cardId", loginUser.getCardId());
            userData.setProperty("phone", loginUser.getPhone());
            userData.setProperty("pswd", loginUser.getPswd());
            userData.setProperty("createTime", loginUser.getCreateTime());
            userData.setProperty("status", StringUtils.isEmpty(loginUser.getStatus())? "" : String.valueOf(loginUser.getStatus()));
            userData.setProperty("deptId", StringUtils.isEmpty(loginUser.getDeptId())? "" : String.valueOf(loginUser.getDeptId()));
            userData.setProperty("renterId", StringUtils.isEmpty(loginUser.getRenterId())? "" : String.valueOf(loginUser.getRenterId()));
            userData.setProperty("isRenter", loginUser.isRenter());
            userData.setProperty("roleCodes", loginUser.getRoleCodes());
        }
    }

}
