package com.ys.idatrix.metacube.metamanage.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * 新增模式VO
 *
 * @author wzl
 */
@ApiModel
@Data
public class SchemaAddVO {

    @ApiModelProperty("数据库id")
    @NotNull(message = "数据库id不能为空")
    private Long dbId;

    @ApiModelProperty(value = "数据库类型")
    @NotNull(message = "数据库类型不能为空")
    private Integer dbType;

    @ApiModelProperty("服务名称 oracle实例名")
    private String serviceName;

    @ApiModelProperty("模式名称、es索引名称、hdfs目录")
    @NotBlank(message = "模式名称不能为空")
    private String name;

    @ApiModelProperty("模式中文名称、hdfs目录中文名")
    private String nameCn;

    @ApiModelProperty("数据库账号")
    private String username;

    @ApiModelProperty("数据库密码")
    private String password;

    @ApiModelProperty("组织编码")
    private List<String> orgCode;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("类型 1新建 2注册")
    @NotNull(message = "type字段不能为空")
    private Integer type;

    @ApiModelProperty("状态 0正常 1禁用")
    private Integer status;
}
