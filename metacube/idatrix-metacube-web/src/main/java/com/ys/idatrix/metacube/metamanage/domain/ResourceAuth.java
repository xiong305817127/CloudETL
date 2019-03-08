package com.ys.idatrix.metacube.metamanage.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@ApiModel(value = "ResourceAuth", description = "资源权限实体类")
@Data
public class ResourceAuth {

    @ApiModelProperty("id")
    private Long id;

    @ApiModelProperty("权限名称")
    private String authName;

    @ApiModelProperty("权限类型，1:读 2:写")
    private Integer authType;

    @ApiModelProperty("当前权限值，二进制标识")
    private Integer authValue;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改时间")
    private Date modifyTime;
}