package com.idatrix.resource.report.vo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class DataReportCountVO {

    @ApiModelProperty("部门名称")
    private String deptName;

    @ApiModelProperty("部门编码")
    private String deptCode;

    @ApiModelProperty("上报任务名称")
    private String taskName;

    @ApiModelProperty("上报任务数量|上报数据量")
    private Long count;
}
