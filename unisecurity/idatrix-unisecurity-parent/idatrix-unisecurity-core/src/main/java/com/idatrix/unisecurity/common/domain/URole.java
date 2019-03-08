package com.idatrix.unisecurity.common.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.sf.json.JSONObject;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * 开发公司：粤数大数据
 */
@ApiModel(description = "角色DTO")
public class URole implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id")
    private long renterId;

    /**
     * 角色名称
     */
    @ApiModelProperty(value = "角色名称")
    private String name;

    /**
     * 角色代码
     */
    @ApiModelProperty(value = "角色代码")
    private String code;

    /**
     * 角色类型
     */
    @ApiModelProperty(value = "角色类型")
    private String type;

    /**
     * 是否有效
     */
    @ApiModelProperty(value = "是否有效")
    private Boolean isActive;

    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 最后更新时间
     */
    @ApiModelProperty(value = "更新时间")
    private Date lastUpdateTime;

    //***做 role --> permission 一对多处理
    private List<UPermission> permissions = new LinkedList<UPermission>();

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

    public List<UPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<UPermission> permissions) {
        this.permissions = permissions;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String toString() {
        return JSONObject.fromObject(this).toString();
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