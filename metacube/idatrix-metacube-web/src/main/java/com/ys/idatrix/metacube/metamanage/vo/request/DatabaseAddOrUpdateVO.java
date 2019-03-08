package com.ys.idatrix.metacube.metamanage.vo.request;

import com.ys.idatrix.metacube.common.validator.CheckOpenDatabase;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * 新增数据库VO
 *
 * @author wzl
 */
@Data
@ApiModel
public class DatabaseAddOrUpdateVO {

    @ApiModelProperty("服务器id")
    @NotNull(message = "服务器id不能为空")
    private Long serverId;

    @ApiModelProperty("数据库类型 1 MySQL 2 Oracle 3 DM 4 PostgreSQL 5 Hive 6 HBase 7 HDFS 8 Elasticsearch")
    @CheckOpenDatabase(message = "不支持的数据库类型")
    @NotNull(message = "数据库类型不能为空")
    private Integer type;

    @ApiModelProperty("数据库归属 1 ODS-操作数据存储 2 DW-数据仓库3 DM-数据集市")
    @NotNull(message = "数据库归属不能为空")
    private Integer belong;

    @ApiModelProperty("端口")
    @NotNull(message = "端口不能为空")
    private Integer port;

    @ApiModelProperty("管理员账号")
    private String username;

    @ApiModelProperty("数据库管理员密码")
    private String password;
}
