package com.ys.idatrix.quality.repository.database.dto;

import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.persistence.Id;
import javax.persistence.Table;

import com.ys.idatrix.quality.ext.CloudSession;
import com.ys.idatrix.quality.ext.utils.DatabaseHelper.FieldLength;
import com.ys.idatrix.quality.ext.utils.DatabaseHelper.FieldUpperCase;

@FieldUpperCase
@Table(catalog="idatrix.system.settings.tableName",name="QUALITY_SYSTEM_SETTINGS")
public class SystemSettingsDto {

	@Id
	private String id;
	
	private String renterId; //所属租户
	private String key;
	private String value;
	
	@FieldLength(length=2)
	private String status; //0 :可用 , 非0: 不可用
	private String operator;
	private Date createTime;
	private Date updateTime ;
	
	@FieldLength(length=500)
	private Map<String,Object> others;
	
	public SystemSettingsDto() {
		this.id =   UUID.randomUUID().toString().replaceAll("-", "");
		this.renterId = CloudSession.getLoginRenterId();
	}
	
	public SystemSettingsDto( String key, String value, String status) {
		this();
		this.key = key;
		this.value = value;
		this.status = status;
	}
	
	public SystemSettingsDto( String key, String value ) {
		this( key,value, "0");
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRenterId() {
		return renterId;
	}

	public void setRenterId(String renterId) {
		this.renterId = renterId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Map<String, Object> getOthers() {
		return others;
	}

	public void setOthers(Map<String, Object> others) {
		this.others = others;
	}

}
