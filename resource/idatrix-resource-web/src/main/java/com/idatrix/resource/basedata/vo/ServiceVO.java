package com.idatrix.resource.basedata.vo;

import java.util.Date;

/**
 * Created by Robin Wing on 2018-5-23.
 */
public class ServiceVO {

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

    /*服务创建时间*/
    private String createTime;

    /*服务创建者*/
    private String creator;

    /* 服务修改者 */
    private String modifier;

    /* 修改时间 */
    private Date modifyTime;

    /*webservice服务，wsdl内容*/
    private String wsdl;

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getWsdl() {
        return wsdl;
    }

    public void setWsdl(String wsdl) {
        this.wsdl = wsdl;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "ServiceVO{" +
                "id=" + id +
                ", providerId='" + providerId + '\'' +
                ", providerName='" + providerName + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", serviceCode='" + serviceCode + '\'' +
                ", remark='" + remark + '\'' +
                ", url='" + url + '\'' +
                ", createTime='" + createTime + '\'' +
                ", wsdl='" + wsdl + '\'' +
                '}';
    }
}
