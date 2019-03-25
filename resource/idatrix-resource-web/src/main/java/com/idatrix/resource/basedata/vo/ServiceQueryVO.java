package com.idatrix.resource.basedata.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 服务查询VO
 *
 * @author wzl
 */
@Data
@ApiModel("服务查询请求信息")
public class ServiceQueryVO {

    @ApiModelProperty("服务名称")
    private String serviceName;

    @ApiModelProperty("服务编码")
    private String serviceCode;

    @ApiModelProperty("服务类型")
    private String serviceType;

    @ApiModelProperty("资源提供方")
    private String providerName;

    @ApiModelProperty(value = "所属租户ID",hidden = true)
    private Long rentId;

    @ApiModelProperty(value = "分页起始页",example = "1")
    private Integer page;

    @ApiModelProperty(value = "分页页大小",example = "10")
    private Integer pageSize;

    public ServiceQueryVO() {
        page = 1;
        pageSize = 10;
    }
}
