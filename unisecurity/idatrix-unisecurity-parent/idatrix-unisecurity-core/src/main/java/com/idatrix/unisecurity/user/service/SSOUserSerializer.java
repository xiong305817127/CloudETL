package com.idatrix.unisecurity.user.service;

import com.idatrix.unisecurity.common.domain.UUser;
import com.idatrix.unisecurity.permission.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 序列化
 */
@Service
public class SSOUserSerializer extends UserSerializer {

    @Autowired
    PermissionService permissionService;

    @Override
    protected void translate(UUser loginUser, UserData userData) throws Exception {
        if(loginUser != null) {
            userData.setId(String.valueOf(loginUser.getId()));
            userData.setProperty("username", loginUser.getUsername());
            userData.setProperty("realName", loginUser.getRealName());
            userData.setProperty("sex", loginUser.getSex());
            userData.setProperty("age", loginUser.getAge());
            userData.setProperty("email", loginUser.getEmail());
            userData.setProperty("cardId", loginUser.getCardId());
            userData.setProperty("phone", loginUser.getPhone());
            userData.setProperty("pswd", loginUser.getPswd());
            userData.setProperty("createTime", loginUser.getCreateTime());
            userData.setProperty("status", loginUser.getStatus());
            userData.setProperty("deptId", loginUser.getDeptId());
            userData.setProperty("renterId", StringUtils.isEmpty(loginUser.getRenterId())?"":String.valueOf(loginUser.getRenterId()));
            userData.setProperty("isRenter", loginUser.isRenter());
            userData.setProperty("roleCodes", loginUser.getRoleCodes());
        }
    }

}
