/**
 * 
 */
package com.ys.idatrix.cloudetl.metacube.api.dto;

import java.io.Serializable;

/**
 * 数据库表域Dto
 * @author WGZ
 * @since 05-12-2017
 *
 */
public class DbTableFieldDto implements Serializable{
	
	private static final long serialVersionUID = 4432090970759763882L;
	
	private String name;
	private String type;
	
	private String metaType; //元数据类型
	
	private Integer length;
	
	/**
	 * 0-不允许,1-允许为空,2-不确定
	 */
	private int nullable;
	
	/**
	 *  1-主键/0-非主键
	 */
	private int isPrimaryKey;
	
	/**
	 *  0-没有精度
	 */
	private int precision = -1; 
	/**
	 * 字段描述
	 */
	private String remarks;
	/**
	 * 默认值
	 */
	private String defaultValue;
	/**
	 * 是否自增
	 */
	private boolean autoincrement ;
	
	
	private boolean isSuccess = true;
	private String mess;
	
    public boolean isSuccess() {
		return isSuccess;
	}
	public void setSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}
	public String getMess() {
		return mess;
	}
	public void setMess(String mess) {
		this.mess = mess;
	}
	public Integer getLength() {
		return length;
	}
	public void setLength(Integer length) {
		this.length = length;
	}
	public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    
	public void setType(String type) {
		this.type =type;
	}
	
	public String getType() {
		return type;
	}
	
	public void setNullable(int originalNullable) {
		this.nullable = originalNullable;
	}
	
	public int getNullable() {
		return nullable;
	}
    	
	public int getIsPrimaryKey() {
		return isPrimaryKey;
	}
	
	public void setIsPrimaryKey(int isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}
	
	public void setPrecision(int precision) {
		this.precision = precision;
	}
	
	public int getPrecision() {
		return precision;
	}
	
	public String getMetaType() {
		return metaType;
	}
	public void setMetaType(String metaType) {
		this.metaType = metaType;
	}
	
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public boolean isAutoincrement() {
		return autoincrement;
	}
	public void setAutoincrement(boolean autoincrement) {
		this.autoincrement = autoincrement;
	}
	
	@Override
	public String toString() {
		return "DbTableFieldDto [name=" + name + ", type=" + type + ", metaType=" + metaType + ", length=" + length
				+ ", nullable=" + nullable + ", isPrimaryKey=" + isPrimaryKey + ", precision=" + precision
				+ ", remarks=" + remarks + ", defaultValue=" + defaultValue + ", autoincrement=" + autoincrement
				+ ", isSuccess=" + isSuccess + ", mess=" + mess + "]";
	}
	
	
}
