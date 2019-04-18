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
 * 数据库信息
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("数据库信息")
public class DbBriefDto {
	
	@ApiModelProperty("拥有者")
	private String owner;
	
	private Long databaseId;
	
	@ApiModelProperty("名称")
	private String name;
	
	@ApiModelProperty("类型")
    private String type;
	
	private String ip;
	
	private Integer port;
	
	public String getOwner() {
    	if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}	
	
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }

	public Long getDatabaseId() {
		return databaseId;
	}
	public void setDatabaseId(Long databaseId) {
		this.databaseId = databaseId;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public Integer getPort() {
		return port;
	}
	public void setPort(Integer port) {
		this.port = port;
	}
	@Override
	public String toString() {
		return "DbBriefDto [owner=" + owner + ", databaseId=" + databaseId + ", name=" + name + ", type=" + type
				+ ", ip=" + ip + ", port=" + port + "]";
	}
    
}
