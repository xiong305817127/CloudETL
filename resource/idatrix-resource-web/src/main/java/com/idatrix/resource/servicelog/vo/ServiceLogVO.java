package com.idatrix.resource.servicelog.vo;

public class ServiceLogVO {

    /*主键*/
    private Long id;

    /*服务名称*/
    private String serviceName;

    /*服务类型: HTTP/WEBSERVICE*/
    private String serviceType;

    /*服务代码*/
    private String serviceCode;

    /*调用方部门名称*/
    private String callerDeptName;

    /*调用时间*/
    private String callTime;

    /*执行时长*/
    private String execTime;

    /*是否成功：0失败，1成功*/
    private Integer isSuccess;

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

    public String getCallerDeptName() {
        return callerDeptName;
    }

    public void setCallerDeptName(String callerDeptName) {
        this.callerDeptName = callerDeptName;
    }

    public String getCallTime() {
        return callTime;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }

    public String getExecTime() {
        return execTime;
    }

    public void setExecTime(String execTime) {
        this.execTime = execTime;
    }

    public Integer getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(Integer isSuccess) {
        this.isSuccess = isSuccess;
    }

    @Override
    public String toString() {
        return "ServiceLogPO{" +
                "id=" + id +
                ", serviceName='" + serviceName + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", serviceCode='" + serviceCode + '\'' +
                ", callerDeptName=" + callerDeptName +
                ", execTime=" + execTime +
                ", isSuccess=" + isSuccess +
                '}';
    }
}
