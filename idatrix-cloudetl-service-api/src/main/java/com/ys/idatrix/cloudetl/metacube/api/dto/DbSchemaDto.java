/**
 * 
 */
package com.ys.idatrix.cloudetl.metacube.api.dto;

import java.io.Serializable;
import java.util.List;

/**
 * 数据库 Schema DTo.
 * @author WGZ
 * @since 05-12-2017
 *
 */
public class DbSchemaDto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1375501555929105957L;
	
	private String connection;
    private String schema;
    private List<String> schemas;
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

    public List<String> getSchemas() {
		return schemas;
	}
	public void setSchemas(List<String> schemas) {
		this.schemas = schemas;
	}
	public void setSchema(String schema) {
        this.schema = schema;
    }
    public String getSchema() {
        return schema;
    }
	@Override
	public String toString() {
		return "DbSchemaDto [connection=" + connection + ", schema=" + schema + ", schemas=" + schemas + ", isSuccess="
				+ isSuccess + ", mess=" + mess + "]";
	}

}
