package com.idatrix.unisecurity.common.domain;

import net.sf.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

/**
 * 开发公司：粤数大数据
 */
public class URolePermission implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long rid;

    private Long pid;

    private Date createTime;

    public URolePermission() {
    }

    public URolePermission(Long rid, Long pid) {
        this.rid = rid;
        this.pid = pid;
    }

    public Long getRid() {
        return rid;
    }

    public void setRid(Long rid) {
        this.rid = rid;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String toString() {
        return JSONObject.fromObject(this).toString();
    }
}