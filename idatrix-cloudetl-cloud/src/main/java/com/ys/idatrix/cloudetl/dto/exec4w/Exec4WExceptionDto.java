package com.ys.idatrix.cloudetl.dto.exec4w;

import java.util.Date;

import javax.persistence.Id;
import javax.persistence.Table;

import com.ys.idatrix.cloudetl.ext.utils.DatabaseHelper.FieldLength;
import com.ys.idatrix.cloudetl.ext.utils.DatabaseHelper.FieldUpperCase;

@FieldUpperCase
@Table(catalog="idatrix.exec.exception.record.tableName",name="ETL_EXEC_EXCEPTION")
public class Exec4WExceptionDto {

	@Id
	private String execId ;

	private String renterId;
	private String owner;
	private String name;
	private String type;
	private String position;
	private Date updateDate;
	@FieldLength(length = 5000)
	private String exceptionDetail ;
	
	//辅助信息
	@FieldLength(length = 1000)
	private String inputSource;
	@FieldLength(length = 500)
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
