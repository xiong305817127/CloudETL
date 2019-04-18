/**
 * GDBD iDatrix CloudETL System.
 */
package com.ys.idatrix.cloudetl.metacube.api.dto;

import java.io.Serializable;
import java.util.List;

/**
 * DTO - DB Connection overview from MetaCube.
 * 
 * @author JW
 * @since 2017年6月16日
 *
 */
public class MetaCubeDbDto implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5431936277143904041L;
	
	private String userId ;
	
	private String name;
	private String type;
	private Integer status;
	private String hostname;
	private String port;
	private String username;
	private String password;
	private String flag;
	private String tableName;
	private String schemaName;
	private String databaseName;
	private String pluginId;
	
	private String accessType;
	
	private List<String> tableNames;
	
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getAccessType() {
		return accessType;
	}
	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getSchemaName() {
		return schemaName;
	}
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
	
	public String getDatabaseName() {
		return databaseName;
	}
	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return this.name;
	}
	
	public void setType(String type){
		this.type = type;
	}
	public String getType(){
		return this.type;
	}
	
	public void setStatus(Integer status){
		this.status = status;
	}
	public Integer getStatus(){
		return this.status;
	}
	
	public String getHostname() {
		return hostname;
	}
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	
	public String getPluginId() {
		return pluginId;
	}
	public void setPluginId(String pluginId) {
		this.pluginId = pluginId;
	}
	
	public List<String> getTableNames() {
		return tableNames;
	}
	public void setTableNames(List<String> tableNames) {
		this.tableNames = tableNames;
	}
	@Override
	public String toString() {
		return "MetaCubeDbDto [name=" + name + ", type=" + type + ", status=" + status + ", hostname=" + hostname
				+ ", port=" + port + ", username=" + username + ", password=" + password + ", flag=" + flag
				+ ", tableName=" + tableName + ", schemaName=" + schemaName + ", databaseName=" + databaseName
				+ ", pluginId=" + pluginId + ", accessType=" + accessType + ", tableNames=" + tableNames + "]";
	}
	
}
