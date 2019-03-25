package com.idatrix.unisecurity.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel
@Data
public class SearchVO {

    @ApiModelProperty(value = "租户id", hidden = true)
    private Long renterId;

    @ApiModelProperty("当前页码")
    private Integer page = 1;

    @ApiModelProperty("分页大小")
    private Integer size = 10;
}