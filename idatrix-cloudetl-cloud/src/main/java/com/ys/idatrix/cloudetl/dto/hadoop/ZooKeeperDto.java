/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.hadoop;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * DTO for hadoop cluster zookeeper wrapper.
 * @author JW
 * @since 2017年7月4日
 *
 */
@ApiModel("zookeeper 信息")
public class ZooKeeperDto {
	
	@ApiModelProperty("zookeeper 主机地址(IP)")
	private String hostname;

	@ApiModelProperty("zookeeper 端口")
	private String port;

	public void setHostname(String hostname){
		this.hostname = hostname;
	}
	public String getHostname(){
		return this.hostname;
	}
	
	public void setPort(String port){
		this.port = port;
	}
	public String getPort(){
		return this.port;
	}

}
