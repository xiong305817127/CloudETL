package com.idatrix.resource.datareport.vo;

/**
 * ETL任务相关执行信息
 *
 */

public class ETLTaskDetailVO {
	/* 任务名称 */
	private String taskName;

	/* 上报记录顺序号 */
	private String dataUploadSeqNum;

	/* 任务当前状态 */
	private String curStatus;

	/* 日志信息 */
	private String log;

	private String operator;

	private String startTime;

	private String endTime;

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getDataUploadSeqNum() {
		return dataUploadSeqNum;
	}

	public void setDataUploadSeqNum(String dataUploadSeqNum) {
		this.dataUploadSeqNum = dataUploadSeqNum;
	}

	public String getCurStatus() {
		return curStatus;
	}

	public void setCurStatus(String curStatus) {
		this.curStatus = curStatus;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
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

	@Override
	public String toString() {
		return "ETLTaskDetailVO{" +
				"taskName='" + taskName + '\'' +
				", dataUploadSeqNum='" + dataUploadSeqNum + '\'' +
				", curStatus='" + curStatus + '\'' +
				", log='" + log + '\'' +
				", operator='" + operator + '\'' +
				", startTime='" + startTime + '\'' +
				", endTime='" + endTime + '\'' +
				'}';
	}
}
