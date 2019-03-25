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
 * 数据库 Schema DTo.
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("数据库 schema 信息")
public class DbSchemaDto {
	
	@ApiModelProperty("拥有者")
	private String owner;
	
	@ApiModelProperty("数据库连接")
	private String connection;
	
	@ApiModelProperty("元数据SchemaId")
	private Long schemaId;
	
	@ApiModelProperty("schema名")
    private String schema;
	
	@ApiModelProperty("serverName或者SID,oracle专用")
    private String serverName;
	
	 public String getOwner() {
		 if( Utils.isEmpty( owner )) {
			 owner = CloudSession.getResourceUser() ;
		 }
		 return owner;
	 }
	 public void setOwner(String owner) {
		 this.owner = owner;
	 }
	public Long getSchemaId() {
		return schemaId;
	}
	public void setSchemaId(Long schemaId) {
		this.schemaId = schemaId;
	}
	public String getSchema() {
		return schema;
	}
	public void setSchema(String schema) {
		this.schema = schema;
	}
	
	public String getConnection() {
		return connection;
	}
	public void setConnection(String connection) {
		this.connection = connection;
	}
	
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	@Override
	public String toString() {
		return "DbSchemaDto [owner=" + owner + ", connection=" + connection + ", schemaId=" + schemaId + ", schema="
				+ schema + ", serverName=" + serverName + "]";
	}
	
}
