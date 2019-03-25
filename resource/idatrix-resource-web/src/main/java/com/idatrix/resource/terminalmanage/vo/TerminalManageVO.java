package com.idatrix.resource.terminalmanage.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("前置机配置信息")
public class TerminalManageVO {
    /*主键*/
    @ApiModelProperty("主键,新增为空或者0,修改设置则为原有ID")
    private Long id;

    /*部门ID*/
    @ApiModelProperty("部门ID")
    private String deptId;

    /*部门最后一个ID*/
    @ApiModelProperty("部门最后一个ID")
    private String deptFinalId;

    /*部门编码*/
    @ApiModelProperty("部门编码")
    private String deptCode;

    /*部门名称*/
    @ApiModelProperty("部门名称")
    private String deptName;

    /*前置机名称*/
    @ApiModelProperty("前置机名称")
    private String tmName;

    /*前置机IP*/
    @ApiModelProperty("前置机IP")
    private String tmIP;

    /*schema名称*/
    @ApiModelProperty("schema名称")
    private String schemaName;

    /*schema ID*/
    @ApiModelProperty("schema ID")
    private String schemaId;

    /*数据库ID*/
    @ApiModelProperty("数据库ID")
    private String tmDBId;

    /*数据库名称*/
    @ApiModelProperty("数据库名称")
    private String tmDBName;

    /*数据库端口*/
    @ApiModelProperty("数据库端口")
    private String tmDBPort;

    /*数据库类型*/
    @ApiModelProperty("数据库类型")
    private String tmDBType;

    /*交换文件存放根目录*/
    @ApiModelProperty("交换文件存放根目录")
    private String sftpSwitchRoot;

    /*sftp对应的hdfs根目录 防止元数据改变目录后, 重新定位导致文件丢失*/
    @ApiModelProperty("sftp对应的hdfs根目录 防止元数据改变目录后, 重新定位导致文件丢失")
    private String hdfsSwitchRoot;

    /*sftp端口*/
    @ApiModelProperty("sftp端口")
    private String sftpPort;

    /*sftp用户名*/
    @ApiModelProperty("sftp用户名")
    private String sftpUsername;

}
