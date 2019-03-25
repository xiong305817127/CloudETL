package com.ys.idatrix.metacube.metamanage.vo.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

@Data
@ApiModel
public class ServerSearchVO extends SearchVO {

    @ApiModelProperty("服务器用途：1前置库 2平台库 3平台库-Hadoop")
    private List<Integer> useList;

    @ApiModelProperty("数据库类型 1 MySQL 2 Oracle 3 DM 4 PostgreSQL 5 Hive 6 HBase 7 HDFS 8 Elasticsearch")
    private List<Integer> typeList;

    @ApiModelProperty("数据库归属 1 ODS-操作数据存储 2 DW-数据仓库3 DM-数据集市")
    private List<Integer> belongList;

    @ApiModelProperty("组织编码")
    private List<String> orgList;

    @JsonIgnore
    @ApiModelProperty(value = "服务器名称")
    private String name;

    @JsonIgnore
    @ApiModelProperty(value = "服务器ip")
    private String ip;
}
