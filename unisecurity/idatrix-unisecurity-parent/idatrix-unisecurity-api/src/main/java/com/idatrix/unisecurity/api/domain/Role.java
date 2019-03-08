package com.idatrix.unisecurity.api.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * 开发公司：粤数大数据
 */
public class Role implements Serializable {
	
    private static final long serialVersionUID = 1L;
    
    private Long id;
    /**
     * 租户id
     */
    private long renterId;
    /**
     * 角色代码
     */
    private String code;
    /**
     * 角色名称
     */
    private String name;
    /**
     * 角色类型
     */
    private String type;
    /**
     * 是否有效
     */
    private Boolean isActive;
    /**
     * 备注
     */
    private String remark;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 最后更新时间
     */
    private Date lastUpdateTime;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getRenterId() {
        return renterId;
    }

    public void setRenterId(long renterId) {
        this.renterId = renterId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }


    public void setType(String type) {
        this.type = type;
    }
   

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}