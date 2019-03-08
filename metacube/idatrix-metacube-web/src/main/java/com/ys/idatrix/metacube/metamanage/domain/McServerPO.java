package com.ys.idatrix.metacube.metamanage.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 服务器实体
 *
 * @author wzl
 */
@Data
@Accessors(chain = true)
@ApiModel
public class McServerPO extends BasePO {

    @ApiModelProperty("服务器名称")
    private String name;

    @ApiModelProperty("服务器用途：1前置库 2平台库 3平台库-Hadoop")
    private Integer use;

    @ApiModelProperty("服务器ip")
    private String ip;

    @ApiModelProperty("服务器主机名")
    private String hostname;

    @ApiModelProperty("组织编码")
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
