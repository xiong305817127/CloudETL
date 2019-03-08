package com.idatrix.resource.taskmanage.po;

import java.util.Date;

/**
 * 数据库查询到 上报任务概览信息
 */
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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getLastStartTime() {
        return lastStartTime;
    }

    public void setLastStartTime(Date lastStartTime) {
        this.lastStartTime = lastStartTime;
    }

    public Date getLastEndTime() {
        return lastEndTime;
    }

    public void setLastEndTime(Date lastEndTime) {
        this.lastEndTime = lastEndTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getEtlSubscribeId() {
        return etlSubscribeId;
    }

    public void setEtlSubscribeId(String etlSubscribeId) {
        this.etlSubscribeId = etlSubscribeId;
    }

    public String getDataBatch() {
        return dataBatch;
    }

    public void setDataBatch(String dataBatch) {
        this.dataBatch = dataBatch;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getProvideDept() {
        return provideDept;
    }

    public void setProvideDept(String provideDept) {
        this.provideDept = provideDept;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public Long getDataCount() {
        return dataCount;
    }

    public void setDataCount(Long dataCount) {
        this.dataCount = dataCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
