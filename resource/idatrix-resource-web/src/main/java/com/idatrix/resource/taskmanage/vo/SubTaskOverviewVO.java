package com.idatrix.resource.taskmanage.vo;

import lombok.Data;

/**
 * Created by Administrator on 2018/8/7.
 */

@Data
public class SubTaskOverviewVO {

    /*监控作业ID*/
    private Long id;

    /*作业名称*/
    private String taskName;

    /*ETL使用到的subscribeId,调用监控需要使用*/
    private String subscribeId;

    /*资源代码*/
    private String code;

    /*资源代码*/
    private String name;

    /*订阅方*/
    private String subscribeDept;

    /*提供方*/
    private String provideDept;

    /*截止日期*/
    private String endTime;

    /*作业类型*/
    private String taskType;

    /*最近执行时间*/
    private String lastRunTime;

    /*最近执行时间*/
    private String startTime;

    /*数据量*/
    private Long dataCount;

    /*任务状态*/
    private String taskStatus;

    /*创建用户名*/
    private String creator;

}
