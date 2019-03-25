package com.idatrix.resource.taskmanage.po;

import lombok.Data;

import java.util.Date;

/**
 *  交换任务执行表
 */
@Data
public class SubTaskExecPO {

    /*主键, 和rc_subscribe主键一致*/
    private Long id;

    /*etl creat返回ID*/
    private String etlSubscribeId;

    /*etl start/stop 返回ID*/
    private String etlExecId;

    /*etl 定时跑返回的ID*/
    private String etlRunningId;

    /*任务编号,和rc_subscribe的订阅编号一致*/
    private String subTaskId;

    /*作业类型：db数据库，file文件*/
    private String taskType;

    /*当前状态:waiting等待执行;running执行中;error执行故障;warn告警状态*/
    private String status;

    /*执行开始时间*/
    private Date startTime;

    /*执行结束时间*/
    private Date endTime;

    /*入库数据总量*/
    private Long importCount;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date modifyTime;


}
