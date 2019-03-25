package com.idatrix.resource.report.vo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;

@ApiModel
@Data
public class ResourceVO {

    @ApiModelProperty("资源目录id")
    private Long resourceId;

    @ApiModelProperty("资源目录名称")
    private String resourceName;

    @ApiModelProperty("资源目录编码")
    private String resourceCode;

    @ApiModelProperty("部门名称")
    private String deptCode;

    @ApiModelProperty("注册|订阅|发布时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date approveTime;

    @ApiModelProperty("资源查看量")
    private Long count;
}
