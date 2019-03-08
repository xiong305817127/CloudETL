package com.idatrix.resource.basedata.po;

import java.util.Arrays;
import java.util.Date;

/**
 * 服务信息表
 * @Description： 共享交换平台对外提供的服务
 * @Author: Wangbin
 * @Date: 2018/5/23
 */
public class ServicePO {
    /*主键*/
    private Long id;

    /*服务提供商ID*/
    private String providerId;

    /*服务提供方名称*/
    private String providerName;

    /*服务名称*/
    private String serviceName;

    /*服务类型：http,webservice*/
    private String serviceType;

    /*服务代码*/
    private String serviceCode;

    /*服务描述*/
    private String remark;

    /*服务url*/
    private String url;

    /*webservice服务，wsdl内容*/
    private byte wsdl[];

    /*状态(软删除用n)*/
    private String status;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date modifyTime;

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public byte[] getWsdl() {
        return wsdl;
    }

    public void setWsdl(byte[] wsdl) {
        this.wsdl = wsdl;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateTime() { return createTime; }

    public void setCreateTime(Date createTime) { this.createTime = createTime; }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ServicePO{" +
                "id=" + id +
                ", providerId='" + providerId + '\'' +
                ", providerName='" + providerName + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", serviceCode='" + serviceCode + '\'' +
                ", remark='" + remark + '\'' +
                ", url='" + url + '\'' +
                ", wsdl=" + Arrays.toString(wsdl) +
                ", status='" + status + '\'' +
                ", creator='" + creator + '\'' +
                ", createTime=" + createTime +
                ", modifier='" + modifier + '\'' +
                ", modifyTime=" + modifyTime +
                '}';
    }
}
