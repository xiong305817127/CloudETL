/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.hadoop;

import org.pentaho.di.core.util.Utils;

import com.ys.idatrix.quality.ext.CloudSession;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * DTO for hadoop cluster details wrapper.
 * @author JW
 * @since 2017年6月16日
 *
 */
@ApiModel("hadoop 详细信息")
public class HadoopDetailsDto {
	
	@ApiModelProperty("拥有者")
	private String owner;
	
	@ApiModelProperty("名称")
	private String name;
	
	@ApiModelProperty("状态")
	private int status;
	
	@ApiModelProperty("存储类型")
	private String storage;
	
	@ApiModelProperty("主机地址(IP)")
	private String hostname;
	
	@ApiModelProperty("端口")
	private String port;
	
	@ApiModelProperty("用户名")
	private String username;
	
	@ApiModelProperty("密码")
	private String password;
	
	@ApiModelProperty("jobtracker 信息")
	private JobTrackerDto jobTracker;
	
	@ApiModelProperty("zookeeper 信息")
	private ZooKeeperDto zooKeeper;
	
	@ApiModelProperty("url")
	private String url;

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
	
	public void setStatus(int status){
		this.status = status;
	}
	public int getStatus(){
		return this.status;
	}
	
	public void setStorage(String storage){
		this.storage = storage;
	}
	public String getStorage(){
		return this.storage;
	}
	
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
	
	public void setUsername(String username){
		this.username = username;
	}
	public String getUsername(){
		return this.username;
	}
	
	public void setPassword(String password){
		this.password = password;
	}
	public String getPassword(){
		return this.password;
	}
	
	public void setJobTracker(JobTrackerDto jobTracker){
		this.jobTracker = jobTracker;
	}
	public JobTrackerDto getJobTracker(){
		return this.jobTracker;
	}
	
	public void setZooKeeper(ZooKeeperDto zooKeeper){
		this.zooKeeper = zooKeeper;
	}
	public ZooKeeperDto getZooKeeper(){
		return this.zooKeeper;
	}
	
	public void setUrl(String url){
		this.url = url;
	}
	public String getUrl(){
		return this.url;
	}

}
