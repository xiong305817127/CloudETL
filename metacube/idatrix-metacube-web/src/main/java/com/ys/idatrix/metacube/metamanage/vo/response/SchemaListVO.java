package com.ys.idatrix.metacube.metamanage.vo.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@ApiModel
public class SchemaListVO {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("创建人")
    private String creator;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改人")
    private String modifier;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @ApiModelProperty("修改时间")
    private Date modifyTime;

    @ApiModelProperty("数据库id")
    private Long dbId;

    @ApiModelProperty("服务名称 oracle实例名")
    private String serviceName;

    @ApiModelProperty("模式名称、es索引名称、hdfs目录")
    private String name;

    @ApiModelProperty("模式中文名称、hdfs目录中文名")
    private String nameCn;

    @ApiModelProperty("数据库账号")
    private String username;

    @ApiModelProperty("数据库密码")
    private String password;

    @ApiModelProperty("组织编码 多个以英文逗号分隔")
    private String orgCode;

    @ApiModelProperty("组织名称 多个以英文逗号分隔")
    private String orgName;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("类型 1新建 2注册")
    private Integer type;

    @ApiModelProperty("状态 0正常 1禁用")
    private Integer status;
}
