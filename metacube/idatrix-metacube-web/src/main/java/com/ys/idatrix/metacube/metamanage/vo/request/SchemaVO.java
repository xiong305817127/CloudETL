package com.ys.idatrix.metacube.metamanage.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 模式VO
 */
@Data
@ApiModel
public class SchemaVO {

    @ApiModelProperty("数据库类型 1 MySQL 2 Oracle 3 DM 4 PostgreSQL 5 Hive 6 HBase 7 HDFS 8 Elasticsearch")
    private Integer dbType;

    @ApiModelProperty("数据库id")
    private Long dbId;

    @ApiModelProperty("模式名称、es索引名称、hdfs目录")
    private String name;

    @ApiModelProperty("模式中文名称、hdfs目录中文名")
    private String nameCn;

    @ApiModelProperty("服务名称 oracle实例名")
    private String serviceName;

    @ApiModelProperty("数据库账号")
    private String username;

    @ApiModelProperty("数据库密码")
    private String password;

    @ApiModelProperty("所属组织编码 多个以英文逗号分隔")
    private String orgCode;

    @ApiModelProperty("备注")
    private String remark;
}
