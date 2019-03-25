package com.idatrix.resource.report.vo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class ExchangeCountVO {

    @ApiModelProperty("部门名称")
    private String deptName;

    @ApiModelProperty("部门id")
    private String deptId;

    @ApiModelProperty("交换任务名称")
    private String taskName;

    @ApiModelProperty("交换任务执行次数|交换任务处理数据量")
    private Long count;
}
