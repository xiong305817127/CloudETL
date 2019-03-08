package com.idatrix.unisecurity.common.domain;

import java.io.Serializable;
import java.util.Date;

public class UserStatistic implements Serializable {

    private Long uid;

    private String clientSystemId;

    private Date lastAccessed;

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getClientSystemId() {
        return clientSystemId;
    }

    public void setClientSystemId(String clientSystemId) {
        this.clientSystemId = clientSystemId;
    }

    public Date getLastAccessed() {
        return new Date(lastAccessed.getTime());
    }

    public void setLastAccessed(Date lastAccessed) {
        this.lastAccessed = new Date(lastAccessed.getTime());
    }
}
