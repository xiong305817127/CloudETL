package com.idatrix.resource.report.vo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class ServiceCountVO {

    @ApiModelProperty("服务名称")
    private String serviceName;

    @ApiModelProperty("服务编码")
    private String serviceCode;

    @ApiModelProperty("服务调用次数|调用数据量")
    private Long count;
}
