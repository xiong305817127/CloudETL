package com.idatrix.resource.report.vo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class ServiceStatisticsVO {

    @ApiModelProperty("服务调用次数")
    private Long count;

    @ApiModelProperty("成功次数")
    private Long success;

    @ApiModelProperty("失败次数")
    private Long failure;
}
