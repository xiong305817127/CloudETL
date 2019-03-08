package com.idatrix.resource.taskmanage.vo;

/**
 * Created by Administrator on 2018/8/11.
 */
public class SubTaskHistoryVO {

    /*对应 sub id*/
    private String taskId;

    /*对应 etl subscribe id*/
    private String etlSubscribeId;

    /*对应 etl subscribe id*/
    private String etlRunningId;

    /*任务运行结构*/
    private String status;

    /*任务开始时间*/
    private String startTime;

    /*任务结束时间*/
    private String endTime;

    /*数据量*/
    private Long dataCount;

    public String getEtlSubscribeId() {
        return etlSubscribeId;
    }

    public void setEtlSubscribeId(String etlSubscribeId) {
        this.etlSubscribeId = etlSubscribeId;
    }

    public String getEtlRunningId() {
        return etlRunningId;
    }

    public void setEtlRunningId(String etlRunningId) {
        this.etlRunningId = etlRunningId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
}
