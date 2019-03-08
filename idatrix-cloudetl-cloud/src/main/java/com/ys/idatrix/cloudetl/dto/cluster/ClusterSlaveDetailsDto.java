/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.cluster;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 集群详情DTO com.ys.idatrix.cloudetl.dto.cluster.ClusterDetailsDto 的子块
 * @author JW
 * @since 2017年6月12日
 *
 */
@ApiModel("集群服务器详细信息")
public class ClusterSlaveDetailsDto {

	@ApiModelProperty("服务器名称")
	private String serverName;
	
	@ApiModelProperty("是否是主服务器")
	private boolean master;
	
	@ApiModelProperty("状态")
    private String status;

	public void setServerName(String serverName){
		this.serverName = serverName;
	}
	public String getServerName(){
		return this.serverName;
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
		return "ClusterSlaveDetailsDto [serverName=" + serverName + ", master=" + master + ", status=" + status + "]";
	}

}
