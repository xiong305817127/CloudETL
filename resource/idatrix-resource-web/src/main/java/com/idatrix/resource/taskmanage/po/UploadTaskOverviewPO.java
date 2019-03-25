package com.idatrix.resource.taskmanage.po;

import lombok.Data;

import java.util.Date;

/**
 * 数据库查询到 上报任务概览信息
 */
@Data
public class UploadTaskOverviewPO {

    /*主键ID*/
    private Long id;

    /*资源ID*/
    private Long resourceId;

    /*作业名称*/
    private String taskName;

    /*ETL使用到的subscribeId,调用监控需要使用*/
    private String etlSubscribeId;

    /*数据批次*/
    private String dataBatch;

    /*资源代码*/
    private String code;

    /*提供方*/
    private String provideDept;

    /*作业类型*/
    private String taskType;

    /*最近开始时间*/
    private Date lastStartTime;

    /*最近结束时间*/
    private Date lastEndTime;

    /*数据量*/
    private Long dataCount;

    /*任务状态*/
    private String status;

    /*上报任务创建者*/
    private String creator;

}
