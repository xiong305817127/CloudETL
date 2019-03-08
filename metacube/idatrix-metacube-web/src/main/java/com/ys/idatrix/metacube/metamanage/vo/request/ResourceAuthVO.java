package com.ys.idatrix.metacube.metamanage.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName ResourceAuthVO
 * @Description
 * @Author ouyang
 * @Date
 */
@ApiModel(value = "ResourceAuthVO", description = "资源权限VO")
@Data
public class ResourceAuthVO {

    @ApiModelProperty("权限名称")
    private String authName;

    @ApiModelProperty("权限类型，1:读 2:写")
    private Integer authType;

}