package com.idatrix.resource.taskmanage.po;

import lombok.Data;

import java.util.Date;

/**
 *  交换任务表
 */
@Data
public class SubTaskPO {

    /*主键, 和rc_subscribe主键一致*/
    private Long id;

    /*任务编号,和rc_subscribe的订阅编号一致*/
    private String subTaskId;

    /*元数据id*/
    private Long srcMetaId;

    /*目标元数据id*/
    private Long destMetaId;

    /*ETL创建任务时候产生的id*/
    private String etlSubscribeId;

    /*作业类型：db数据库，file文件*/
    private String taskType;

    /*当前状态:waiting等待执行;running执行中;error执行故障;warn告警状态*/
    private String status;

    /*最近一次exec时间*/
    private Date lastRunTime;

    /*入库数据总量*/
    private Long importCount;

    /*租户ID，用于租户隔离*/
    private Long rentId;

    /*订阅数据截止时间*/
    private Date endTime;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date modifyTime;

}
