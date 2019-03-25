package com.idatrix.resource.webservice.po;

import java.util.Date;

/**
 * 共享服务信息表
 *
 * @author wzl
 */
public class ServicePO {

    /**
     * 主键
     */
    private Long id;
    /**
     * 服务名称
     */
    private String serviceName;
    /**
     * 服务类型：SOAP,RESTful
     */
    private String serviceType;
    /**
     * 服务代码
     */
    private String serviceCode;
    /**
     * 服务描述
     */
    private String remark;
    /**
     * 服务url
     */
    private String url;

    private String creator;
    private Date createTime;
    private String modifier;
    private Date modifyTime;
    /**
     * 服务提供者ID
     */
    private String providerId;
    /**
     * 服务提供者名称
     */
    private String providerName;
    /**
     * 租户ID
     */
    private Long rentId;
    /**
     * 技术支持单位
     */
    private String technicalSupportUnit;
    /**
     * 技术支持联系人
     */
    private String technicalSupportContact;
    /**
     * 技术支持联系电话
     */
    private String technicalSupportContactNumber;
    /**
     * 请求示例
     */
    private String requestExample;
    /**
     * 成功返回示例
     */
    private String successfulReturnExample;
    /**
     * 失败返回示例
     */
    private String failureReturnExample;
    /**
     * 逻辑删除字段 1 表示删除， 0 表示未删除
     */
    private Integer isDeleted;

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

    public Long getRentId() {
        return rentId;
    }

    public void setRentId(Long rentId) {
        this.rentId = rentId;
    }

    public String getTechnicalSupportUnit() {
        return technicalSupportUnit;
    }

    public void setTechnicalSupportUnit(String technicalSupportUnit) {
        this.technicalSupportUnit = technicalSupportUnit;
    }

    public String getTechnicalSupportContact() {
        return technicalSupportContact;
    }

    public void setTechnicalSupportContact(String technicalSupportContact) {
        this.technicalSupportContact = technicalSupportContact;
    }

    public String getTechnicalSupportContactNumber() {
        return technicalSupportContactNumber;
    }

    public void setTechnicalSupportContactNumber(String technicalSupportContactNumber) {
        this.technicalSupportContactNumber = technicalSupportContactNumber;
    }

    public String getRequestExample() {
        return requestExample;
    }

    public void setRequestExample(String requestExample) {
        this.requestExample = requestExample;
    }

    public String getSuccessfulReturnExample() {
        return successfulReturnExample;
    }

    public void setSuccessfulReturnExample(String successfulReturnExample) {
        this.successfulReturnExample = successfulReturnExample;
    }

    public String getFailureReturnExample() {
        return failureReturnExample;
    }

    public void setFailureReturnExample(String failureReturnExample) {
        this.failureReturnExample = failureReturnExample;
    }

    public Integer getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }
}
