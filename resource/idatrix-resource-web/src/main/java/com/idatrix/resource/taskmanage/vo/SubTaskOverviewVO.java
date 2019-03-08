package com.idatrix.resource.taskmanage.vo;

/**
 * Created by Administrator on 2018/8/7.
 */
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

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSubscribeId() {
        return subscribeId;
    }

    public void setSubscribeId(String subscribeId) {
        this.subscribeId = subscribeId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
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

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getLastRunTime() {
        return lastRunTime;
    }

    public void setLastRunTime(String lastRunTime) {
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

    @Override
    public String toString() {
        return "SubTaskOverviewVO{" +
                "id=" + id +
                ", taskName='" + taskName + '\'' +
                ", subscribeId='" + subscribeId + '\'' +
                ", code='" + code + '\'' +
                ", subscribeDept='" + subscribeDept + '\'' +
                ", provideDept='" + provideDept + '\'' +
                ", endTime='" + endTime + '\'' +
                ", taskType='" + taskType + '\'' +
                ", lastRunTime='" + lastRunTime + '\'' +
                ", startTime='" + startTime + '\'' +
                ", dataCount=" + dataCount +
                ", taskStatus='" + taskStatus + '\'' +
                '}';
    }
}
