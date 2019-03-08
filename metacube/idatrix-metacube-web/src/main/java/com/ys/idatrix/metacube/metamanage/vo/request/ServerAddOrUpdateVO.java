package com.ys.idatrix.metacube.metamanage.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import lombok.Data;

/**
 * 新增服务器VO
 *
 * @author wzl
 */
@Data
@ApiModel
public class ServerAddOrUpdateVO {

    @ApiModelProperty("服务器名称 必填")
    @NotBlank(message = "服务器名称不能为空")
    private String name;

    @ApiModelProperty("服务器用途：1前置库 2平台库 3平台库-Hadoop 必填")
    @NotNull(message = "服务器用途不能为空")
    private Integer use;

    @ApiModelProperty("服务器ip 必填且格式正确")
    @NotBlank(message = "ip不能为空")
    @Pattern(regexp = "^([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}$"
            , message = "ip格式不正确")
    private String ip;

    @ApiModelProperty("服务器主机名")
    private String hostname;

    @ApiModelProperty("组织编码 必填")
    @NotBlank(message = "组织编码不能为空")
    private String orgCode;

    @ApiModelProperty("位置信息")
    private String location;

    @ApiModelProperty("联系人")
    private String contact;

    @ApiModelProperty("联系人电话")
    private String contactNumber;

    @ApiModelProperty("备注")
    private String remark;
}
