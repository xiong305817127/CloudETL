package com.idatrix.resource.catalog.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Robin Wing on 2018-6-13.
 */
@Data
@ApiModel("注册审批流程请求")
public class ApproveRequestVO {

    @ApiModelProperty("ID")
    private Long id;

    @ApiModelProperty("是否同意")
    private String action;

    @ApiModelProperty("审批意见")
    private String suggestion;

}
