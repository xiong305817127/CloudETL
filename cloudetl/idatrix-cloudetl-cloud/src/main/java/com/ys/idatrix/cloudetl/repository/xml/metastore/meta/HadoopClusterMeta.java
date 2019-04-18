/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.repository.xml.metastore.meta;

import org.pentaho.di.core.util.Utils;

import com.ys.idatrix.cloudetl.ext.CloudSession;

import io.swagger.annotations.ApiModelProperty;

/**
 * DTO for hadoop cluster details.
 * @author JW
 * @since 2017年6月16日
 *
 */
public class HadoopClusterMeta {

	@ApiModelProperty("拥有者")
	private String owner;
	private String name;
	private int status;
	private String storage;
	private String hostname;
	private String port;
	private String username;
	private String password;
	private HadoopJobTrackerMeta jobTracker;
	private HadoopZooKeeperMeta zooKeeper;
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
	
	public void setJobTracker(HadoopJobTrackerMeta jobTracker){
		this.jobTracker = jobTracker;
	}
	public HadoopJobTrackerMeta getJobTracker(){
		return this.jobTracker;
	}
	
	public void setZooKeeper(HadoopZooKeeperMeta zooKeeper){
		this.zooKeeper = zooKeeper;
	}
	public HadoopZooKeeperMeta getZooKeeper(){
		return this.zooKeeper;
	}
	
	public void setUrl(String url){
		this.url = url;
	}
	public String getUrl(){
		return this.url;
	}
	
	/* 
	 * Build text.
	 */
	@Override
	public String toString() {
		return "HadoopClusterMeta [name=" + name + ", status=" + status + ", storage=" + storage + ", hostname="
				+ hostname + ", port=" + port + ", username=" + username + ", password=" + password + ", jobTracker="
				+ jobTracker + ", zooKeeper=" + zooKeeper + ", url=" + url + "]";
	}
	
}
