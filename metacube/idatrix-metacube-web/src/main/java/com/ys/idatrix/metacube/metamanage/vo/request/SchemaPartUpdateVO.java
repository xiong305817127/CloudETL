package com.ys.idatrix.metacube.metamanage.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

/**
 * 部分更新模式VO
 *
 * @author wzl
 */
@ApiModel
@Data
public class SchemaPartUpdateVO {

    @ApiModelProperty("模式中文名称、hdfs目录中文名")
    private String nameCn;

    @ApiModelProperty("组织编码 多个以英文逗号分隔")
    private List<String> orgCode;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("状态 0正常 1禁用")
    private Integer status;
}
