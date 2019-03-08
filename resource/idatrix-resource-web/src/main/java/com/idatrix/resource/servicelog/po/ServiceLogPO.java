package com.idatrix.resource.servicelog.po;

import java.util.Date;

public class ServiceLogPO {

    /*主键*/
    private Long id;

    /*服务名称*/
    private String serviceName;

    /*服务类型: HTTP/WEBSERVICE*/
    private String serviceType;

    /*服务代码*/
    private String serviceCode;

    /*调用方部门ID*/
    private Long callerDeptId;

    /*调用方部门编码*/
    private String callerDeptCode;

    /*调用方部门名称*/
    private String callerDeptName;

    /*执行时长*/
    private Integer execTime;

    /*是否成功：0失败，1成功*/
    private Integer isSuccess;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date modifyTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public Long getCallerDeptId() {
        return callerDeptId;
    }

    public void setCallerDeptId(Long callerDeptId) {
        this.callerDeptId = callerDeptId;
    }

    public String getCallerDeptCode() {
        return callerDeptCode;
    }

    public void setCallerDeptCode(String callerDeptCode) {
        this.callerDeptCode = callerDeptCode;
    }

    public String getCallerDeptName() {
        return callerDeptName;
    }

    public void setCallerDeptName(String callerDeptName) {
        this.callerDeptName = callerDeptName;
    }

    public Integer getExecTime() {
        return execTime;
    }

    public void setExecTime(Integer execTime) {
        this.execTime = execTime;
    }

    public Integer getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(Integer isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public String toString() {
        return "ServiceLogPO{" +
                "id=" + id +
                ", serviceName='" + serviceName + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", serviceCode='" + serviceCode + '\'' +
                ", callerDeptCode='" + callerDeptCode + '\'' +
                ", callerDeptName=" + callerDeptName +
                ", execTime=" + execTime +
                ", isSuccess=" + isSuccess +
                ", creator='" + creator + '\'' +
                ", createTime=" + createTime +
                ", modifier='" + modifier + '\'' +
                ", modifyTime=" + modifyTime +
                '}';
    }
}
