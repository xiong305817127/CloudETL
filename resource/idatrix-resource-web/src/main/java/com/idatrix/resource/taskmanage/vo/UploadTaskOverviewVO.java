package com.idatrix.resource.taskmanage.vo;

/**
 * Created by Administrator on 2018/8/8.
 */
public class UploadTaskOverviewVO {

    /*作业名称*/
    private String taskName;

    /*etl subscribe id用户监控展示*/
    private String etlSubcribeId;

    /*部门*/
    private String deptName;

    /*类型*/
    private String taskType;

    /*开始时间*/
    private String startTime;

    /*结束时间*/
    private String endTime;

    /*数据量*/
    private Long dataCount;

    /*作业状态*/
    private String status;

    /*任务创建者*/
    private String creator;

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getEtlSubcribeId() {
        return etlSubcribeId;
    }

    public void setEtlSubcribeId(String etlSubcribeId) {
        this.etlSubcribeId = etlSubcribeId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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
