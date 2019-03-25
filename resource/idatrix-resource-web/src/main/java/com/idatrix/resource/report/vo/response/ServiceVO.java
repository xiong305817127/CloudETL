package com.idatrix.resource.report.vo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;

@ApiModel
@Data
public class ServiceVO {

    @ApiModelProperty("服务名称")
    private String serviceName;

    @ApiModelProperty("服务编码")
    private String serviceCode;

    @ApiModelProperty("调用时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date callTime;

    @ApiModelProperty("客户端ip")
    private String ip;

    @ApiModelProperty("是否成功 0失败 1成功")
    private Integer success;

    @ApiModelProperty("返回数据量")
    private Long count;
}
