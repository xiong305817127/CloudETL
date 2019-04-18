/**
 * 
 */
package com.ys.idatrix.cloudetl.metacube.api.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 数据库多表Dto
 * @author WGZ
 * @since 05-12-2017
 *
 */
public class DbTablesDto implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -86190011472379256L;
	private String connection;
    private String schema;
    private String type;
    private List<DbTableDto> tables;
    
    private boolean isSuccess;
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

    /**
	 * @return type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param  设置 type
	 */
	public void setType(String type) {
		this.type = type;
	}
	public void setTables(List<DbTableDto> tables) {
        this.tables = tables;
    }
    public List<DbTableDto> getTables() {
        return tables;
    }
    
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DbTablesDto [connection=" + connection + ", schema=" + schema + ", tables=" + tables + "]";
	}

}
