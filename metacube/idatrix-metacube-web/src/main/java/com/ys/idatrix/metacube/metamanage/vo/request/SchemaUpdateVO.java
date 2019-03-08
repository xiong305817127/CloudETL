package com.ys.idatrix.metacube.metamanage.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 更新模式VO
 *
 * @author wzl
 */
@ApiModel
@Data
public class SchemaUpdateVO {

    @ApiModelProperty("服务名称 oracle实例名")
    private String serviceName;

    @ApiModelProperty("模式中文名称、hdfs目录中文名")
    private String nameCn;

    @ApiModelProperty("数据库账号")
    private String username;

    @ApiModelProperty("数据库密码")
    private String password;

    @ApiModelProperty("组织编码 多个以英文逗号分隔")
    private String orgCode;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("状态 0正常 1禁用")
    private Integer status;
}
