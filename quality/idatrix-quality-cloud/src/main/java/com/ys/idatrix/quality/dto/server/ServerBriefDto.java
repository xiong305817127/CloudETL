/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.server;

import org.pentaho.di.core.util.Utils;

import com.ys.idatrix.quality.ext.CloudSession;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * slave server 信息
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("服务器信息")
public class ServerBriefDto {
	
	@ApiModelProperty("拥有者")
	private String owner;
	
	@ApiModelProperty("服务器名")
	private String name;
	
	@ApiModelProperty("是否是master")
    private boolean master;
	
	@ApiModelProperty("服务器状态")
    private String status;
    
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

    public void setMaster(boolean master) {
        this.master = master;
    }
    public boolean getMaster() {
        return master;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }
    
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ServerBriefDto [name=" + name + ", master=" + master + ", status=" + status + "]";
	}

}
