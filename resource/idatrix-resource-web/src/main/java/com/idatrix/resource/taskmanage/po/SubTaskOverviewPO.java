package com.idatrix.resource.taskmanage.po;


import java.util.Date;

/**
 * 数据库查询到 交换任务概览信息
 */
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

    public String getSubscribeUser() {
        return subscribeUser;
    }

    public void setSubscribeUser(String subscribeUser) {
        this.subscribeUser = subscribeUser;
    }

    public String getSubTaskId() {
        return subTaskId;
    }

    public void setSubTaskId(String subTaskId) {
        this.subTaskId = subTaskId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSubscribeDept() {
        return subscribeDept;
    }

    public void setSubscribeDept(String subscribeDept) {
        this.subscribeDept = subscribeDept;
    }

    public String getProvideDept() {
        return provideDept;
    }

    public void setProvideDept(String provideDept) {
        this.provideDept = provideDept;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public Date getLastRunTime() {
        return lastRunTime;
    }

    public void setLastRunTime(Date lastRunTime) {
        this.lastRunTime = lastRunTime;
    }

    public Long getDataCount() {
        return dataCount;
    }

    public void setDataCount(Long dataCount) {
        this.dataCount = dataCount;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }
}


