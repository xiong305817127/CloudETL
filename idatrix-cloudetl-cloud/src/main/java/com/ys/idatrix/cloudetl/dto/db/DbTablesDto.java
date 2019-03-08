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
 * 数据库多表Dto
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("数据库表信息")
public class DbTablesDto {
	
	@ApiModelProperty("拥有者")
	private String owner;
	
	@ApiModelProperty("数据库连接")
	private String connection;
	
	@ApiModelProperty("元数据SchemaId")
	private Long schemaId;
	
	@ApiModelProperty("元数据SchemaId")
	private Long tableId;
	
	@ApiModelProperty("表名")
    private String tableName;
	
	@ApiModelProperty("类型, view/table")
    private String type;
	
    
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
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "DbTablesDto [owner=" + owner + ", connection=" + connection + ", schemaId=" + schemaId + ", tableId="
				+ tableId + ", tableName=" + tableName + ", type=" + type + "]";
	}

}
