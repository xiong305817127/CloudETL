package com.idatrix.unisecurity.common.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;
import java.util.Date;

/**
 * 开发公司：粤数大数据
 */
@ApiModel(description = "组织DTO")
public class URenter implements Serializable {

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "租户名称")
    @NotBlank(message = "租户名称不能为空")
    private String renterName;

    @ApiModelProperty(value = "账号")
    @NotBlank(message = "租户账号不能为空")
    private String adminAccount;

    @ApiModelProperty(value = "账户名称")
    @NotBlank(message = "账户名称不能为空")
    private String adminName;

    @ApiModelProperty(value = "手机")
    @NotBlank(message = "手机不能为空")
    private String adminPhone;

    @ApiModelProperty(value = "邮箱")
    @NotBlank(message = "租户邮箱不能为空")
    private String adminEmail = null;

    @ApiModelProperty(value = "开放服务")
    private String openedService;

    @ApiModelProperty(value = "开放系统")
    private String openedResource;

    @ApiModelProperty(value = "状态")
    private Long renterStatus;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date lastUpdatedBy;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRenterName() {
        return renterName;
    }

    public void setRenterName(String renterName) {
        this.renterName = renterName;
    }

    public String getAdminEmail() {
        return adminEmail;
    }

    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    public String getAdminAccount() {
        return adminAccount;
    }

    public void setAdminAccount(String adminAccount) {
        this.adminAccount = adminAccount;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminPhone() {
        return adminPhone;
    }

    public void setAdminPhone(String adminPhone) {
        this.adminPhone = adminPhone;
    }

    public String getOpenedService() {
        return openedService;
    }

    public void setOpenedService(String openedService) {
        this.openedService = openedService;
    }

    public String getOpenedResource() {
        return openedResource;
    }

    public void setOpenedResource(String openedResource) {
        this.openedResource = openedResource;
    }

    public Long getRenterStatus() {
        return renterStatus;
    }

    public void setRenterStatus(Long renterStatus) {
        this.renterStatus = renterStatus;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(Date lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    @Override
    public String toString() {
        return "URenter [id=" + id + ", renterName=" + renterName + ", adminEmail=" + adminEmail + ", adminAccount="
                + adminAccount + ", adminName=" + adminName + ", adminPhone=" + adminPhone + ", openedService="
                + openedService + ", openedResource=" + openedResource + ", renterStatus=" + renterStatus
                + ", createTime=" + createTime + ", lastUpdatedBy=" + lastUpdatedBy + "]";
    }
}
