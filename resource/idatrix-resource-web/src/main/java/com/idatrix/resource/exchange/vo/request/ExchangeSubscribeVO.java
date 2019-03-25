package com.idatrix.resource.exchange.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 交换订阅数据: 神马订阅交换请求数据
 *
 * @auther robin
 * @date   2018/11/07
 */
@Data
@ApiModel("第三方接口订阅数据")
public class ExchangeSubscribeVO {

    /*订阅ID,用于异步查询结果*/
    @ApiModelProperty("订阅ID,用于异步查询结果")
    private String subscribeId;

    /*信息资源编码*/
    @ApiModelProperty("根据国标的信息资源编码")
    private String resourceCode;

    /*资源订阅细项*/
    @ApiModelProperty(value = "资源订阅细项",hidden = true)
    private String resourceColumnIds;

    /*资源类型 db/file*/
    @ApiModelProperty("资源类型 db/file")
    private String resourceType;

    /*部门信息 暂定为部门统一社会信用编码*/
    @ApiModelProperty("部门信息 暂定为部门统一社会信用编码")
    private String subscribeDeptInfo;

    /*部门信息 部门名称*/
    @ApiModelProperty("部门信息 部门名称")
    private String subscribeDeptInfoName;

    /*订阅截止日志 一锤子买卖的订阅，暂时无用*/
    @ApiModelProperty(value = "订阅截止日志 一锤子买卖的订阅，暂时无用",hidden = true)
    private String endTime;

    /*数据库：IP地址*/
    @ApiModelProperty("数据库：IP地址")
    private String dbIp;

    /*数据库：端口*/
    @ApiModelProperty("数据库：端口")
    private Long dbPort;

    /*数据库：类型*/
    @ApiModelProperty("数据库：类型")
    private String dbType;

    /*数据库：用户*/
    @ApiModelProperty("数据库：用户")
    private String dbUser;

    /*数据库：密码*/
    @ApiModelProperty("数据库：密码")
    private String dbPassword;

    /*数据库名称*/
    @ApiModelProperty("数据库名称")
    private String dbName;

    /*数据库：模式*/
    @ApiModelProperty("数据库：模式")
    private String dbSchemaName;

    /*数据库：表名*/
    @ApiModelProperty("数据库：表名")
    private String dbTableName;

}
