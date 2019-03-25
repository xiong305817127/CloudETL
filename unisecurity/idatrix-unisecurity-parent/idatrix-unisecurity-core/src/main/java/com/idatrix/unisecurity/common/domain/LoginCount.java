package com.idatrix.unisecurity.common.domain;

import lombok.Data;

import java.util.Date;

@Data
public class LoginCount {
    private Long id;

    private String username;

    private String ip;

    private Long userId;

    private Long renterId;

    private Date loginTime;

    public LoginCount() {
    }

    public LoginCount(String username, String ip, Long userId, Long renterId, Date loginTime) {
        this.userId = userId;
        this.ip = ip;
        this.username = username;
        this.renterId = renterId;
        this.loginTime = loginTime;
    }
}