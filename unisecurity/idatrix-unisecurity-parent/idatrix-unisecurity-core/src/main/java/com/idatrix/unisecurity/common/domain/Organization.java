package com.idatrix.unisecurity.common.domain;

import com.idatrix.unisecurity.anotation.IdatrixMaxLen;
import com.idatrix.unisecurity.anotation.IdatrixPattern;
import com.idatrix.unisecurity.anotation.NotBlank;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

@ApiModel(description = "组织DTO")
public class Organization implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;

    @ApiModelProperty(value = "父id")
    private Long parentId;

    @ApiModelProperty(value = "父组织名称")
    private String parentDeptName;

    @ApiModelProperty(value = "租户id")
    private Long renterId;

    @ApiModelProperty(value = "租户名称")
    private String renterName;

    @ApiModelProperty(value = "组织代码")
    @NotBlank(message = "请填写组织机构代码")
    @IdatrixPattern(regexp = "^[a-zA-Z0-9]{1,20}$", message = "只能允许数字、字母和作为组织机构代码")
    private String deptCode;

    @ApiModelProperty(value = "组织名称")
    @NotBlank(message = "请填写组织机构名称")
    @IdatrixMaxLen(maxLen = 50, message = "组织机构名称不能超过50个字符")
//    @IdatrixPattern(regexp="^[_a-zA-Z0-9\\u4e00-\\u9fa5]{1,50}$",message="只能允许中文、数字、字母和下划线作为组织机构名称")
    private String deptName;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "是否有效")
    private Boolean isActive;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date lastUpdatedBy;

    @ApiModelProperty(value = "统一信用代码 ")
    @NotBlank(message = "请填写统一信用代码")
    private String unifiedCreditCode;

    @ApiModelProperty(value = "所属组织id")
    private Long ascriptionDeptId;

    @ApiModelProperty(value = "所属组织名称")
    private String ascriptionDeptName;

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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

    public String getUnifiedCreditCode() {
        return unifiedCreditCode;
    }

    public void setUnifiedCreditCode(String unifiedCreditCode) {
        this.unifiedCreditCode = unifiedCreditCode;
    }

    public Long getAscriptionDeptId() {
        return ascriptionDeptId;
    }

    public void setAscriptionDeptId(Long ascriptionDeptId) {
        this.ascriptionDeptId = ascriptionDeptId;
    }

    public String getParentDeptName() {
        return parentDeptName;
    }

    public void setParentDeptName(String parentDeptName) {
        this.parentDeptName = parentDeptName;
    }

    public String getAscriptionDeptName() {
        return ascriptionDeptName;
    }

    public void setAscriptionDeptName(String ascriptionDeptName) {
        this.ascriptionDeptName = ascriptionDeptName;
    }
}
