package com.ys.idatrix.metacube.metamanage.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName TableConstraintVO
 * @Description table 约束实体类
 * @Author ouyang
 * @Date
 */
@Data
@ApiModel("约束实体类")
public class TableConstraintVO {

    @ApiModelProperty("约束id")
    private Long id;

    @ApiModelProperty("约束名")
    private String constraintName;

    @ApiModelProperty("约束对应列的id")
    private String columnIds;

    @ApiModelProperty("约束对应列的names")
    private String columnNames;

    @ApiModelProperty("约束类型")
    private Integer type;

}