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
public class DataReportSearchVO extends BaseSearchVO {

    @ApiModelProperty("部门编码")
    private String deptCode;

}
