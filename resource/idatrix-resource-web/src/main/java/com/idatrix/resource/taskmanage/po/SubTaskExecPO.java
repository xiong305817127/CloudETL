package com.idatrix.resource.taskmanage.po;

import java.util.Date;

/**
 *  交换任务执行表
 */
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


    public String getEtlSubscribeId() {
        return etlSubscribeId;
    }

    public void setEtlSubscribeId(String etlSubscribeId) {
        this.etlSubscribeId = etlSubscribeId;
    }

    public String getEtlExecId() {
        return etlExecId;
    }

    public void setEtlExecId(String etlExecId) {
        this.etlExecId = etlExecId;
    }

    public String getEtlRunningId() {
        return etlRunningId;
    }

    public void setEtlRunningId(String etlRunningId) {
        this.etlRunningId = etlRunningId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubTaskId() {
        return subTaskId;
    }

    public void setSubTaskId(String subTaskId) {
        this.subTaskId = subTaskId;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getImportCount() {
        return importCount;
    }

    public void setImportCount(Long importCount) {
        this.importCount = importCount;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}
