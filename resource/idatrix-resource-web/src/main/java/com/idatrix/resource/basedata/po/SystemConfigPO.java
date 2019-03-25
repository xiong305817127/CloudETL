package com.idatrix.resource.basedata.po;

import lombok.Data;

import java.util.Date;

/**
 * Created by Robin Wing on 2018-6-14.
 */
@Data
public class SystemConfigPO {

    /*主键*/
    private Long id;
    /*文件库根目录*/
    private String fileRoot;
    /*文件库根目录 列表*/
    private String fileRootIds;
    /*手工上报原始文件库根目录*/
    private String originFileRoot;
    /*列表*/
    private String originFileRootIds;
    /*数据库类型上报文件（cvs）限制大小，单位是Mb*/
    private int dbUploadSize;
    /*文件类型上报限制大小，单位Mb*/
    private int fileUploadSize;
    /*扫描上传文件时间，单位：分钟*/
    private int importInterval;
    /*部门录入人员角色*/
    private Long deptStaffRole;
    /*部门管理员角色*/
    private Long deptAdminRole;
    /*数据中心管理员角色*/
    private Long centerAdminRole;
    /*订阅审批角色*/
    private Long subApproverRole;

    /*组合ID，租户隔离使用*/
    private Long rentId;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date modifyTime;

    public SystemConfigPO(){
        /*系统默认值*/
        this.dbUploadSize = 50;
        this.fileUploadSize = 200;
        this.importInterval = 5;
    }
}
