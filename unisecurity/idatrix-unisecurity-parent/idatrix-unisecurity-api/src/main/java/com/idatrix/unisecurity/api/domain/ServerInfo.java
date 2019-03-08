package com.idatrix.unisecurity.api.domain;

import java.io.Serializable;

/**
 */
public class ServerInfo implements Serializable {
    private Long id;
    private Long userId;
    private String openService;

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

    public String getOpenService() {
        return openService;
    }

    public void setOpenService(String openService) {
        this.openService = openService;
    }
}
