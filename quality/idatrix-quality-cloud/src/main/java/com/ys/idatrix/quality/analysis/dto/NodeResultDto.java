package com.ys.idatrix.quality.analysis.dto;

import java.util.Date;
import java.util.Map;

import javax.persistence.Id;
import javax.persistence.Table;

import com.google.common.collect.Maps;
import com.ys.idatrix.quality.ext.utils.DatabaseHelper.FieldLength;

@Table(catalog="idatrix.analysis.node.result.tableName",name="tbl_nodeResult")
public class NodeResultDto {
	
	@Id
	private String uuid	;
	
	//所属租户ID
    private String renterId;
	private String userName	;		
	private String analysisName	;	
	private String nodId	;		
	private String nodeType	;
	
	private String execId	;	
	
	private String fieldName	;		
	private String referenceValue	;		
	private long number	;
	
	private boolean isMatch = true ;
	
	private Date updateTime ;
	
	//两个备用字段
	@FieldLength(length=200 )
	private String optional1 ;
	
	@FieldLength(length=1000 )
	private Map<String,Object> optional2 ;
	
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	
	public String getRenterId() {
		return renterId;
	}
	public void setRenterId(String renterId) {
		this.renterId = renterId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getAnalysisName() {
		return analysisName;
	}
	public void setAnalysisName(String analysisName) {
		this.analysisName = analysisName;
	}
	public String getExecId() {
		return execId;
	}
	public void setExecId(String execId) {
		this.execId = execId;
	}
	public String getNodId() {
		return nodId;
	}
	public void setNodId(String nodId) {
		this.nodId = nodId;
	}
	public String getNodeType() {
		return nodeType;
	}
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	public String getReferenceValue() {
		return referenceValue;
	}
	public void setReferenceValue(String referenceValue) {
		this.referenceValue = referenceValue;
	}
	public long getNumber() {
		return number;
	}
	public void setNumber(long number) {
		this.number = number;
	}
	
	public long increaseNumber() {
		if( this.number <= 0 ) {
			this.number = 0;
		}
		return ++this.number;
	}
	
	public long addNumber(long num) {
		if( this.number <= 0 ) {
			this.number = 0;
		}
		 this.number = this.number+num;
		 return  this.number ;
	}
	
	public Date getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}
	public boolean isMatch() {
		return isMatch;
	}
	public void setMatch(boolean isMatch) {
		this.isMatch = isMatch;
	}
	public String getOptional1() {
		return optional1;
	}
	public void setOptional1(String optional1) {
		this.optional1 = optional1;
	}
	public Map<String, Object> getOptional2() {
		return optional2;
	}
	public void setOptional2(Map<String, Object> optional2) {
		this.optional2 = optional2;
	}
	
	public void addOption(String key , Object value) {
		if( optional2 == null ) {
			optional2 = Maps.newHashMap();
		}
		optional2.put(key, value) ;
	}

}
