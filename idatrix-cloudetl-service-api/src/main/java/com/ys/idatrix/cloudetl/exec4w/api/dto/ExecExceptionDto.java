package com.ys.idatrix.cloudetl.exec4w.api.dto;

import java.io.Serializable;
import java.util.Date;

public class ExecExceptionDto  implements Serializable {

	private static final long serialVersionUID = -3736237936869911158L;

	private String execId ;

	private String renterId;
	private String owner;
	private String name;
	private String type;
	private String position;
	private Date updateDate;
	private String exceptionDetail ;
	
	//辅助信息
	private String inputSource;
	private String outputSource;
	private String execSource;
	
	
	public String getExecId() {
		return execId;
	}
	public void setExecId(String execId) {
		this.execId = execId;
	}
	public String getRenterId() {
		return renterId;
	}
	public void setRenterId(String renterId) {
		this.renterId = renterId;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}
	public Date getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}
	public String getExceptionDetail() {
		return exceptionDetail;
	}
	public void setExceptionDetail(String exceptionDetail) {
		this.exceptionDetail = exceptionDetail;
	}
	public String getInputSource() {
		return inputSource;
	}
	public void setInputSource(String inputSource) {
		this.inputSource = inputSource;
	}
	public String getOutputSource() {
		return outputSource;
	}
	public void setOutputSource(String outputSource) {
		this.outputSource = outputSource;
	}
	public String getExecSource() {
		return execSource;
	}
	public void setExecSource(String execSource) {
		this.execSource = execSource;
	}
	
}
