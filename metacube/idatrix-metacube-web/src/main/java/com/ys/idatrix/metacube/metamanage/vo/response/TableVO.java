package com.ys.idatrix.metacube.metamanage.vo.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName TableVO
 * @Description
 * @Author ouyang
 * @Date
 */
@Data
@ApiModel
public class TableVO {

    @ApiModelProperty("实体表名")
    private String table;

    @ApiModelProperty("表中文名")
    private String tableName;

    @JsonIgnore
    private Long schemaId;

    @JsonIgnore
    private Integer resourceType;

}