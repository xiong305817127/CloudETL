package com.ys.idatrix.cloudetl.subscribe.api.dto;

import java.io.Serializable;

import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.SubscribeMeasureDto;

public class SubscribeResultDto  implements Serializable{
	
	private static final long serialVersionUID = 3363993745526283081L;
	
	//订阅任务名
	private String name;
	//类别
	private String group;
	//订阅编号
	private String subscribeId;
	//状态码,服务调用是否成功。0：成功 ，非0：失败
	private int status = 0 ;
	//失败原因
	private String errorMessage;
	
	/*************************以下可能为空************************/
	//job当前运行状态
	private String curStatus ;
	//job当前执行id
	private String curExecId;
	//执行日志
	private String log;
	//执行的度量信息列表
	private SubscribeMeasureDto measure ;
	
	public SubscribeResultDto() {
		super();
	}
	
	public SubscribeResultDto(String name,String group, String subscribeId, int status, String errorMessage, String curStatus,
			String curExecId) {
		super();
		this.name = name;
		this.group = group;
		this.subscribeId = subscribeId;
		this.status = status;
		this.errorMessage = errorMessage;
		this.curStatus = curStatus;
		this.curExecId = curExecId;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getSubscribeId() {
		return subscribeId;
	}
	public void setSubscribeId(String subscribeId) {
		this.subscribeId = subscribeId;
	}

	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public String getCurStatus() {
		return curStatus;
	}
	public void setCurStatus(String curStatus) {
		this.curStatus = curStatus;
	}
	public String getCurExecId() {
		return curExecId;
	}
	public void setCurExecId(String curExecId) {
		this.curExecId = curExecId;
	}
	public String getLog() {
		return log;
	}
	public void setLog(String log) {
		this.log = log;
	}
	public SubscribeMeasureDto getMeasure() {
		return measure;
	}
	public void setMeasure(SubscribeMeasureDto measure) {
		this.measure = measure;
	}
	
	public SubscribeResultDto clone() {
		SubscribeResultDto c =  new SubscribeResultDto(name,group, subscribeId, status, errorMessage, curStatus, curExecId);
		return c;
	}

	@Override
	public String toString() {
		return "SubscribeResultDto [name=" + name + ", group=" + group + ", subscribeId=" + subscribeId + ", status="
				+ status + ", errorMessage=" + errorMessage + ", curStatus=" + curStatus + ", curExecId=" + curExecId
				+ ", log=" + log + ", measure=" + measure + "]";
	}

}
