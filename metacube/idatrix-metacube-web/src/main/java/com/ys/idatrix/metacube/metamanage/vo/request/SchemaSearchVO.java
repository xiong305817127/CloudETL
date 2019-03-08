package com.ys.idatrix.metacube.metamanage.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 模式查询VO
 *
 * @author wzl
 */
@Data
@ApiModel
public class SchemaSearchVO extends SearchVO {

    @ApiModelProperty("数据库id")
    private Long dbId;

    @ApiModelProperty(value = "创建者", hidden = true)
    private String creator;

    @ApiModelProperty(value = "所属组织", hidden = true)
    private String orgCode;
}
