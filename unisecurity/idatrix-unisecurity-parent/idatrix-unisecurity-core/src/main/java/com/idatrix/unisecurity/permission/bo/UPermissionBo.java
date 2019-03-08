package com.idatrix.unisecurity.permission.bo;

import com.idatrix.unisecurity.common.domain.UPermission;
import com.idatrix.unisecurity.common.utils.SecurityStringUtils;

import java.io.Serializable;

/**
 * 权限选择
 */
public class UPermissionBo extends UPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否勾选
     */
    private String marker;

    private String roleId;

    public boolean isCheck() {
        return SecurityStringUtils.equals(roleId, marker);
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

}
