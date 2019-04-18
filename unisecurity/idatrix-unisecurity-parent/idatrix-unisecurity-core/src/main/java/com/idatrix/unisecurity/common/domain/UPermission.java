package com.idatrix.unisecurity.common.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import net.sf.json.JSONObject;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
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
     * 权限类型
     */
    @ApiModelProperty(value = "权限类型：系统，菜单，按钮，中转站")
    @NotBlank(message = "权限类型不能为空")
    private String type;

    /**
     * 权限的url
     */
    @ApiModelProperty(value = "权限的url")
    private String url;

    @ApiModelProperty(value = "跳转的url")
    private String redirectUrl;

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

    public String toString() {
        return JSONObject.fromObject(this).toString();
    }
}