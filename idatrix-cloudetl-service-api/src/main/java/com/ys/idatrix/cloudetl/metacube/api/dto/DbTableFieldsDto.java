/**
 * 
 */
package com.ys.idatrix.cloudetl.metacube.api.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 数据库表域 列表DTo
 * @author WGZ
 * @since 05-12-2017
 *
 */
public class DbTableFieldsDto implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1826478121345470605L;
	private String connection;
    private String schema;
    private String table;
    private String mess;
    private boolean success;
    private List<DbTableFieldDto> fields;
    
    public void setConnection(String connection) {
        this.connection = connection;
    }
    public String getConnection() {
        return connection;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
    public String getSchema() {
        return schema;
    }

    public void setTable(String table) {
        this.table = table;
    }
    public String getTable() {
        return table;
    }

    public void setFields(List<DbTableFieldDto> fields) {
        this.fields = fields;
    }
    public List<DbTableFieldDto> getFields() {
        return fields;
    }
    
    
    
	public String getMess() {
		return mess;
	}
	public void setMess(String mess) {
		this.mess = mess;
	}
	
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DbTableFieldsDto [connection=" + connection + ", schema=" + schema + ", table=" + table + ", mess="
				+ mess + ", success=" + success + ", fields=" + fields + "]";
	}
	

}
