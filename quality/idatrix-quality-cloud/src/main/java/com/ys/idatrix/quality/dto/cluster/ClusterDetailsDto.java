/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.cluster;

import java.util.List;

import org.pentaho.di.core.util.Utils;

import com.ys.idatrix.quality.ext.CloudSession;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 集群详情Dto
 * @author JW
 * @since 2017年6月12日
 *
 */
@ApiModel("集群详情")
public class ClusterDetailsDto {
	
	
	@ApiModelProperty("拥有者")
	private String owner;

	@ApiModelProperty("名称")
	private String name;
	
	@ApiModelProperty("端口")
	private String port;
	
	@ApiModelProperty("schema缓存大小")
	private String buffer;
	
	@ApiModelProperty("刷新间隔")
	private String rows;
	
	@ApiModelProperty("是否压缩")
	private boolean compress;
	
	@ApiModelProperty("是否是动态集群")
	private boolean dynamic;
	
	@ApiModelProperty("服务器列表")
	private List<ClusterSlaveDetailsDto> servers ;

	
    public String getOwner() {
    	if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return this.name;
	}
	
	public void setPort(String port){
		this.port = port;
	}
	public String getPort(){
		return this.port;
	}
	
	public void setBuffer(String buffer){
		this.buffer = buffer;
	}
	public String getBuffer(){
		return this.buffer;
	}
	
	public void setRows(String rows){
		this.rows = rows;
	}
	public String getRows(){
		return this.rows;
	}
	
	public void setCompress(boolean compress){
		this.compress = compress;
	}
	public boolean getCompress(){
		return this.compress;
	}
	
	public void setDynamic(boolean dynamic){
		this.dynamic = dynamic;
	}
	public boolean getDynamic(){
		return this.dynamic;
	}
	
	public void setServers(List<ClusterSlaveDetailsDto> servers){
		this.servers = servers;
	}
	public List<ClusterSlaveDetailsDto> getServers(){
		return this.servers;
	}
	
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ClusterDetailsDto [name=" + name + ", port=" + port + ", buffer=" + buffer + ", rows=" + rows
				+ ", compress=" + compress + ", dynamic=" + dynamic + ", servers=" + servers + "]";
	}

}
