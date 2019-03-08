package com.ys.idatrix.cloudetl.subscribe.api.dto.parts;

import java.io.Serializable;

public class SubscribeMeasureDto  implements Serializable{
	
	private static final long serialVersionUID = -8624920130213893968L;
	
	//执行id
	private String runId;
	//执行id
	private String execId;
	//执行状态
	private String	status;
	//输出记录数
	private Long  outputLines;
	//输入记录数
	private Long	inputLines ;
	//更新记录数
	private Long	updateLines ;
	//错误数
	private Long	error ;
	//开始执行时间
	private String	startTime;
	//结束执行时间
	private String	endTime;
	
	public String getRunId() {
		return runId;
	}
	public void setRunId(String runId) {
		this.runId = runId;
	}
	public String getExecId() {
		return execId;
	}
	public void setExecId(String execId) {
		this.execId = execId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public Long getOutputLines() {
		return outputLines;
	}
	public void setOutputLines(Long outputLines) {
		this.outputLines = outputLines;
	}
	public Long getInputLines() {
		return inputLines;
	}
	public void setInputLines(Long inputLines) {
		this.inputLines = inputLines;
	}
	public Long getUpdateLines() {
		return updateLines;
	}
	public void setUpdateLines(Long updateLines) {
		this.updateLines = updateLines;
	}
	public Long getError() {
		return error;
	}
	public void setError(Long error) {
		this.error = error;
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
		return "SubscribeMeasureDto [runId=" + runId + ", execId=" + execId + ", status=" + status + ", outputLines="
				+ outputLines + ", inputLines=" + inputLines + ", updateLines=" + updateLines + ", error=" + error
				+ ", startTime=" + startTime + ", endTime=" + endTime + "]";
	}
	
}
