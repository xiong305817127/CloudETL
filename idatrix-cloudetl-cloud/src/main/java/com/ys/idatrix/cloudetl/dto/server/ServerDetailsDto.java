/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.server;

import org.pentaho.di.core.util.Utils;

import com.ys.idatrix.cloudetl.ext.CloudSession;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * DTO for slave server details.
 * @author JW
 * @since 2017年7月3日
 *
 */
@ApiModel("服务详细信息")
public class ServerDetailsDto {

	@ApiModelProperty("拥有者")
	private String owner;
	
	@ApiModelProperty("名称")
	private String name;

	@ApiModelProperty("主机地址(IP)")
	private String hostname;

	@ApiModelProperty("端口")
	private String port;

	@ApiModelProperty("服务状态")
	private String status;

	@ApiModelProperty("用户名")
	private String username;

	@ApiModelProperty("密码")
	private String password;

	@ApiModelProperty("是否是主服务器")
	private boolean master;
	
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
	public void setStatus(String status){
		this.status = status;
	}
	public String getStatus(){
		return this.status;
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
	public void setMaster(boolean master){
		this.master = master;
	}
	public boolean getMaster(){
		return this.master;
	}
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ServerDetailsDto [name=" + name + ", hostname=" + hostname + ", port=" + port + ", status=" + status
				+ ", username=" + username + ", password=" + password + ", master=" + master + "]";
	}

}
