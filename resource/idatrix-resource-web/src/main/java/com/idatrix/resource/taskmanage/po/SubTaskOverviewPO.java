package com.idatrix.resource.taskmanage.po;


import lombok.Data;

import java.util.Date;

/**
 * 数据库查询到 交换任务概览信息
 */

@Data
public class SubTaskOverviewPO {

    /*主键ID*/
    private Long id;

    /*订阅产生名称*/
    private String subTaskId;

    /*资源ID*/
    private Long resourceId;

    /*作业名称*/
    private String taskName;

    /*ETL使用到的subscribeId,调用监控需要使用*/
    private String etlSubscribeId;

    /*资源名称*/
    private String resourceName;

    /*资源代码*/
    private String code;

    /*订阅方*/
    private String subscribeDept;

    /*提供方*/
    private String provideDept;

    /*截止日期*/
    private Date endTime;

    /*作业类型*/
    private String taskType;

    /*最近执行时间*/
    private Date lastRunTime;

    /*数据量*/
    private Long dataCount;

    /*任务状态*/
    private String taskStatus;

    /*订阅账号*/
    private String subscribeUser;

}


