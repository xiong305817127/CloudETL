package com.idatrix.unisecurity.api.domain;

import java.io.Serializable;

/**
 * 开发公司：粤数大数据
 */
public class URenter implements Serializable {
    private Long id;
    private String renterName;
    private String adminAccount;
    private String adminName;
    private String adminPhone;
    private String openedService;
    private String openedResource;
    private Boolean renterStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRenterName() {
        return renterName;
    }

    public void setRenterName(String renterName) {
        this.renterName = renterName;
    }

    public String getAdminAccount() {
        return adminAccount;
    }

    public void setAdminAccount(String adminAccount) {
        this.adminAccount = adminAccount;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminPhone() {
        return adminPhone;
    }

    public void setAdminPhone(String adminPhone) {
        this.adminPhone = adminPhone;
    }

    public String getOpenedService() {
        return openedService;
    }

    public void setOpenedService(String openedService) {
        this.openedService = openedService;
    }

    public String getOpenedResource() {
        return openedResource;
    }

    public void setOpenedResource(String openedResource) {
        this.openedResource = openedResource;
    }

    public Boolean getRenterStatus() {
        return renterStatus;
    }

    public void setRenterStatus(Boolean renterStatus) {
        this.renterStatus = renterStatus;
    }
}
