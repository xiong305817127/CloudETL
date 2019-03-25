package com.ys.idatrix.metacube.metamanage.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
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
    @NotBlank(message = "ip不能为空")
    @Pattern(regexp = "^([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}$"
            , message = "ip格式不正确")
    private String ip;

    @ApiModelProperty("数据库端口")
    private String port;

    @ApiModelProperty("数据库名称")
    private String dbName;
}
