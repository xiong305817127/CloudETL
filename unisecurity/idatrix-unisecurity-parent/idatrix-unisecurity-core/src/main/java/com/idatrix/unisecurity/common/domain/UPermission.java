package com.idatrix.unisecurity.common.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import net.sf.json.JSONObject;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@ApiModel(description = "权限DTO")
public class UPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * 权限的名称
     */
    @ApiModelProperty(value = "权限名称")
    @NotBlank(message = "权限名称不能为空")
    private String name;

    /**
     * 权限的url
     */
    @ApiModelProperty(value = "权限的url")
    @NotBlank(message = "权限url不能为空")
    private String url;

    /**
     * 权限类型
     */
    @ApiModelProperty(value = "权限类型")
    @NotBlank(message = "权限类型不能为空")
    private String type;

    /**
     * 权限描述
     */
    @ApiModelProperty(value = "权限描述")
    private String urlDesc;

    /**
     * 是否显示
     */
    @ApiModelProperty(value = "是否显示")
    @NotNull(message = "需要制定是否显示")
    private Boolean isShow;

    /**
     * 显示顺序
     */
    @ApiModelProperty(value = "显示顺序")
    private Integer showOrder;

    /**
     * 父权限Id
     */
    @ApiModelProperty(value = "父权限Id")
    @NotNull(message = "父权限ID为空，错误的参数！！！")
    private Long parentId;

    /**
     * 客户端系统Id
     */
    @ApiModelProperty(value = "客户端系统Id")
    @NotBlank(message = "客户端系统Id为空，错误的参数！！！")
    private String clientSystemId;

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

    public String getClientSystemId() {
        return clientSystemId;
    }

    public void setClientSystemId(String clientSystemId) {
        this.clientSystemId = clientSystemId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsShow() {
        return isShow;
    }

    public void setIsShow(Boolean isShow) {
        this.isShow = isShow;
    }

    public Integer getShowOrder() {
        return showOrder;
    }

    public void setShowOrder(Integer showOrder) {
        this.showOrder = showOrder;
    }

    public String getUrlDesc() {
        return urlDesc;
    }

    public void setUrlDesc(String urlDesc) {
        this.urlDesc = urlDesc;
    }

    public String toString() {
        return JSONObject.fromObject(this).toString();
    }
}