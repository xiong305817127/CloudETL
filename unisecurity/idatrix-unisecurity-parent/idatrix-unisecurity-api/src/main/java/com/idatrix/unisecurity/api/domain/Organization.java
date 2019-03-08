package com.idatrix.unisecurity.api.domain;

import java.io.Serializable;
import java.sql.Timestamp;

public class Organization implements Serializable {

    private Long id;

    private Long parentId;

    private Long renterId;

    private Long ascriptionDeptId;

    private String renterName;

    private String deptCode;

    private String deptName;

    // 统一信用代码
    private String unifiedCreditCode;

    // 创建时间
    private Timestamp createTime;

    // 更新时间
    private Timestamp lastUpdatedBy;

    // 备注
    private String remark;

    // 是否可用
    private Boolean isActive;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getRenterId() {
        return renterId;
    }

    public void setRenterId(Long renterId) {
        this.renterId = renterId;
    }

    public String getRenterName() {
        return renterName;
    }

    public void setRenterName(String renterName) {
        this.renterName = renterName;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getUnifiedCreditCode() {
        return unifiedCreditCode;
    }

    public void setUnifiedCreditCode(String unifiedCreditCode) {
        this.unifiedCreditCode = unifiedCreditCode;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(Timestamp lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getAscriptionDeptId() {
        return ascriptionDeptId;
    }

    public void setAscriptionDeptId(Long ascriptionDeptId) {
        this.ascriptionDeptId = ascriptionDeptId;
    }
}
