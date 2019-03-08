package com.ys.idatrix.metacube.metamanage.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 数据库更新VO
 *
 * @author wzl
 */
@Data
@ApiModel
public class DatabaseUpdateVO {

    @ApiModelProperty("数据库归属 1 ODS-操作数据存储 2 DW-数据仓库3 DM-数据集市")
    private Integer belong;

    @ApiModelProperty("端口")
    private Integer port;

    @ApiModelProperty("管理员账号")
    private String username;

    @ApiModelProperty("数据库管理员密码")
    private String password;
}
