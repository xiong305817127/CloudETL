package com.ys.idatrix.metacube.metamanage.vo.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: SchemaVO
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/12
 */
@Data
@ApiModel
public class SchemaVO {

    @ApiModelProperty("模式id")
    private Long schemaId;

    @ApiModelProperty("模式名称")
    private String schemaName;

    @ApiModelProperty("所属组织")
    private String deptName;

}
