/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.db;

import org.pentaho.di.core.util.Utils;

import com.ys.idatrix.cloudetl.ext.CloudSession;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 数据库表域 列表DTo
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("数据库域信息")
public class DbTableFieldsDto {
	
	@ApiModelProperty("拥有者")
	private String owner;
	
	@ApiModelProperty("数据库连接")
	private String connection;
	
	private Long schemaId;
	
	private Long tableId;
	
	@ApiModelProperty("数据库表名")
    private String table;
	
	@ApiModelProperty("数据库字段名")
	private String name;
	
	@ApiModelProperty("数据库字段类型")
	private int type;
    
	public String getOwner() {
    	if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
    public void setConnection(String connection) {
        this.connection = connection;
    }
    public String getConnection() {
        return connection;
    }
	public Long getSchemaId() {
		return schemaId;
	}
	public void setSchemaId(Long schemaId) {
		this.schemaId = schemaId;
	}
	public Long getTableId() {
		return tableId;
	}
	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "DbTableFieldsDto [owner=" + owner + ", connection=" + connection + ", schemaId=" + schemaId
				+ ", tableId=" + tableId + ", table=" + table + ", name=" + name + ", type=" + type + "]";
	}

  
}
