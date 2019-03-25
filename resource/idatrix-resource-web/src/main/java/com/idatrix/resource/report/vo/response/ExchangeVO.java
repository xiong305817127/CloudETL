package com.idatrix.resource.report.vo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;

@ApiModel
@Data
public class ExchangeVO {

    @ApiModelProperty("交换任务名称")
    private String taskName;

    @ApiModelProperty("执行开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private String startTime;

    @ApiModelProperty("执行结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endTime;

    @ApiModelProperty("任务状态 WAIT_IMPORT 等待入库, IMPORTING 入库中,IMPORT_COMPLETE 已入库,STOP_IMPORT 终止入库,"
            + " IMPORT_ERROR 入科失败")
    private String status;

    @ApiModelProperty("处理数据量")
    private Long count;
}
