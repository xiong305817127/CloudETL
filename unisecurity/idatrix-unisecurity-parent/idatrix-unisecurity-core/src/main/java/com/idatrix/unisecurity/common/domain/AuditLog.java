package com.idatrix.unisecurity.common.domain;

import java.io.Serializable;
import java.util.Date;

public class AuditLog implements Serializable{

    private Long id;

    private String server;

    private String resource;

    private String methodType;

    private String clientIp;

    private Date visitTime;

    private String result;

    private Integer opType;

    private Long userId;

    private String userName;

    private Long renterId;

    public AuditLog(){

    }

    public AuditLog(String server, String resource, String methodType, String clientIp, Date visitTime, Long userId, String userName, Long renterId) {
        this.server = server;
        this.resource = resource;
        this.methodType = methodType;
        this.clientIp = clientIp;
        this.visitTime = visitTime;
        this.userId = userId;
        this.userName = userName;
        this.renterId = renterId;
    }

    public Integer getOpType() {
        return opType;
    }

    public void setOpType(Integer opType) {
        this.opType = opType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server == null ? null : server.trim();
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource == null ? null : resource.trim();
    }

    public String getMethodType() {
        return methodType;
    }

    public void setMethodType(String methodType) {
        this.methodType = methodType == null ? null : methodType.trim();
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp == null ? null : clientIp.trim();
    }

    public Date getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(Date visitTime) {
        this.visitTime = visitTime;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result == null ? null : result.trim();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public Long getRenterId() {
        return renterId;
    }

    public void setRenterId(Long renterId) {
        this.renterId = renterId;
    }

}