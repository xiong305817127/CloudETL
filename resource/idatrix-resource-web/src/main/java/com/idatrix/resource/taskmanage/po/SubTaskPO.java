package com.idatrix.resource.taskmanage.po;

import java.util.Date;

/**
 *  交换任务表
 */
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

    /*订阅数据截止时间*/
    private Date endTime;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date modifyTime;

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getSrcMetaId() {
        return srcMetaId;
    }

    public void setSrcMetaId(Long srcMetaId) {
        this.srcMetaId = srcMetaId;
    }

    public Long getDestMetaId() {
        return destMetaId;
    }

    public void setDestMetaId(Long destMetaId) {
        this.destMetaId = destMetaId;
    }

    public String getEtlSubscribeId() {
        return etlSubscribeId;
    }

    public void setEtlSubscribeId(String etlSubscribeId) {
        this.etlSubscribeId = etlSubscribeId;
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

    public Date getLastRunTime() {
        return lastRunTime;
    }

    public void setLastRunTime(Date lastRunTime) {
        this.lastRunTime = lastRunTime;
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
