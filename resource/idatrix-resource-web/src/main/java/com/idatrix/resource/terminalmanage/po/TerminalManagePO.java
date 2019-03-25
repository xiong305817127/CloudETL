package com.idatrix.resource.terminalmanage.po;

import lombok.Data;

import java.util.Date;

@Data
public class TerminalManagePO {
    /*主键*/
    private Long id;

    /*部门ID*/
    private String deptId;

    /*部门最终ID*/
    private String deptFinalId;

    /*部门编码*/
    private String deptCode;

    /*部门名称*/
    private String deptName;

    /*前置机名称*/
    private String tmName;

    /*前置机IP*/
    private String tmIP;

    /*schema名称*/
    private String schemaName;

    /*schema Id*/
    private String schemaId;

    /*数据库ID*/
    private String tmDBId;

    /*数据库名称*/
    private String tmDBName;

    /*数据库端口*/
    private String tmDBPort;

    /*数据库类型*/
    private String tmDBType;

    /*交换文件存放根目录*/
    private String sftpSwitchRoot;

    /*sftp对应的hdfs根目录 防止元数据改变目录后, 重新定位导致文件丢失*/
    private String hdfsSwitchRoot;

    /*sftp端口*/
    private String sftpPort;

    /*sftp用户名*/
    private String sftpUsername;

    /*创建人*/
    private String creator;

    /*创建时间*/
    private Date createTime;

    /*修改人*/
    private String modifier;

    /*修改时间(文件更新时间)*/
    private Date modifyTime;

    /*租户ID，用于租户隔离*/
    private Long rentId;


}
