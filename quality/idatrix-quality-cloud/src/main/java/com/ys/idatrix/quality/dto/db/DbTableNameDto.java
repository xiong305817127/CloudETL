/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.db;

import org.pentaho.di.core.util.Utils;

import com.ys.idatrix.quality.ext.CloudSession;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 数据库单表Dto
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("数据库单表表信息")
public class DbTableNameDto {
	
	@ApiModelProperty("拥有者")
	private String owner;
	
	@ApiModelProperty("数据库连接")
	private String connection;
	
	@ApiModelProperty("数据库schema名")
    private String schema;
	
	private Long schemaId;
	
	@ApiModelProperty("数据库表名")
    private String table;
    
	@ApiModelProperty("是否最数据库关键字进行转义,加上 `关键字`")
    private boolean isQuote = false ;
    
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

    public void setSchema(String schema) {
        this.schema = schema;
    }
    public String getSchema() {
        return schema;
    }

    public Long getSchemaId() {
		return schemaId;
	}
	public void setSchemaId(Long schemaId) {
		this.schemaId = schemaId;
	}
	public void setTable(String table) {
        this.table = table;
    }
    public String getTable() {
        return table;
    }
    
	/**
	 * @return the isQuote
	 */
	public boolean isQuote() {
		return isQuote;
	}
	/**
	 * @param  设置 isQuote
	 */
	public void setQuote(boolean isQuote) {
		this.isQuote = isQuote;
	}
	@Override
	public String toString() {
		return "DbTableNameDto [owner=" + owner + ", connection=" + connection + ", schema=" + schema + ", schemaId="
				+ schemaId + ", table=" + table + ", isQuote=" + isQuote + "]";
	}



}
