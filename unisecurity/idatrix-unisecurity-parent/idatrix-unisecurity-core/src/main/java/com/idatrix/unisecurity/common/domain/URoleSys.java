package com.idatrix.unisecurity.common.domain;

import java.io.Serializable;

/**
 * 开发公司：粤数大数据
 */
public class URoleSys implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long roleId;

    private String clientSystemId;

    public URoleSys() {

    }

    public URoleSys(Long roleId, String clientSystemId) {
        this.roleId = roleId;
        this.clientSystemId = clientSystemId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getClientSystemId() {
        return clientSystemId;
    }

    public void setClientSystemId(String clientSystemId) {
        this.clientSystemId = clientSystemId;
    }
}
