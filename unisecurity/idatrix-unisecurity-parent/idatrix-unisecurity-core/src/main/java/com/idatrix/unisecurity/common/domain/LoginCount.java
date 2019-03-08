package com.idatrix.unisecurity.common.domain;

import java.util.Date;

public class LoginCount {
    private Long id;

    private Long userId;

    private String username;

    private Long renterId;

    private Date loginTime;

    public LoginCount() {
    }

    public LoginCount(Long userId, String username, Long renterId, Date loginTime) {
        this.userId = userId;
        this.username = username;
        this.renterId = renterId;
        this.loginTime = loginTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public Long getRenterId() {
        return renterId;
    }

    public void setRenterId(Long renterId) {
        this.renterId = renterId;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }
}