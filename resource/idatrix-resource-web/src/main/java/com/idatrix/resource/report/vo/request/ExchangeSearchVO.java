package com.idatrix.resource.report.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 数据上报查询VO
 *
 * @author wzl
 */
@ApiModel
@Data
public class ExchangeSearchVO extends BaseSearchVO {

    @ApiModelProperty("部门id")
    private String deptId;

}
