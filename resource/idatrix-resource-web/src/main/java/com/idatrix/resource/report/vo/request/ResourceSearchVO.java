package com.idatrix.resource.report.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 资源目录查询VO
 *
 * @author wzl
 */
@ApiModel
@Data
public class ResourceSearchVO extends BaseSearchVO {

    @ApiModelProperty("资源列表类型 1注册 2订阅 3发布 4资源使用频率")
    private Integer resourceType;
}
