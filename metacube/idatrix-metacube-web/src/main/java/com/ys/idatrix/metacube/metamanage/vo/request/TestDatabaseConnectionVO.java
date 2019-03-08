package com.ys.idatrix.metacube.metamanage.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 测试数据库连接VO
 *
 * @author wzl
 */
@Data
@ApiModel
public class TestDatabaseConnectionVO {

    @ApiModelProperty("数据库账号")
    private String username;

    @ApiModelProperty("数据库密码")
    private String password;

    @ApiModelProperty(value = "数据库类型", hidden = true)
    private String type;

    @ApiModelProperty("服务器ip")
    private String ip;

    @ApiModelProperty("数据库端口")
    private String port;

    @ApiModelProperty("数据库名称")
    private String dbName;
}
