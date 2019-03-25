package com.idatrix.resource.report.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 服务接口查询VO
 *
 * @author wzl
 */
@ApiModel
@Data
public class ServiceSearchVO extends BaseSearchVO {

    @ApiModelProperty("服务编码")
    private String serviceCode;
}
