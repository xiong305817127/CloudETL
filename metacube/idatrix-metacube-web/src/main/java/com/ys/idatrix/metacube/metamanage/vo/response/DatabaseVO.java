package com.ys.idatrix.metacube.metamanage.vo.response;

import com.ys.idatrix.metacube.metamanage.domain.McSchemaPO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.Data;

/**
 * 数据库响应VO
 */
@ApiModel
@Data
public class DatabaseVO {

    @ApiModelProperty("服务器id")
    private Long serverId;

    @ApiModelProperty("服务器ip")
    private String ip;

    @ApiModelProperty("数据库id")
    private Long id;

    @ApiModelProperty("数据库类型 1 MySQL 2 Oracle 3 DM 4 PostgreSQL 5 Hive 6 HBase 7 HDFS 8 Elasticsearch")
    private Integer type;

    @ApiModelProperty("数据库名称")
    private String name;

    @ApiModelProperty("数据库归属 1 ODS-操作数据存储 2 DW-数据仓库3 DM-数据集市")
    private Integer belong;

    @ApiModelProperty("端口")
    private Integer port;

    @ApiModelProperty("管理员账号")
    private String username;

    @ApiModelProperty("数据库管理员密码")
    private String password;

    @ApiModelProperty("数据库模式")
    private List<McSchemaPO> schemaList;

    public DatabaseVO() {
    }
}
