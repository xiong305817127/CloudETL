/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.reference.metacube.dto;

/**
 * DTO for ETL slave server details.
 * @author JW
 * @since 2017年6月16日
 *
 */
public class MetaCubeServerDetailsDto {

	private String name;
	private String hostname;
	private String port;
	private int status;
	private String username;
	private String password;
	private boolean master;

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

	public void setStatus(int status){
		this.status = status;
	}
	public int getStatus(){
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

}
