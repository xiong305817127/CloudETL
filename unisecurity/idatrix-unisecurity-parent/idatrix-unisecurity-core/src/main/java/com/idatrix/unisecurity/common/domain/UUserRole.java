package com.idatrix.unisecurity.common.domain;

import net.sf.json.JSONObject;

import java.io.Serializable;

public class UUserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long uid;

    private Long rid;

    public UUserRole() {
    }

    public UUserRole(Long uid, Long rid) {
        this.uid = uid;
        this.rid = rid;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Long getRid() {
        return rid;
    }

    public void setRid(Long rid) {
        this.rid = rid;
    }

    public String toString() {
        return JSONObject.fromObject(this).toString();
    }
}