/**
 * 云化数据集成系统 
 * iDatrix quality
 */
package com.ys.idatrix.quality.reference.metacube.dto;

/**
 * DTO - DB Connection overview from MetaCube.
 * 
 * @author JW
 * @since 2017年6月16日
 *
 */
public class MetaCubeDbSchemaDto {

	
	private Long schemaId;
	private String schemaName;
	private String username;
	private String password;
	private String serviceName;
	private int status;
	
	private String name ;
	private Long databaseId;
	private String databaseType;
	private String ip;
	private Integer port;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getSchemaId() {
		return schemaId;
	}

	public void setSchemaId(Long schemaId) {
		this.schemaId = schemaId;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
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

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public Long getDatabaseId() {
		return databaseId;
	}

	public void setDatabaseId(Long databaseId) {
		this.databaseId = databaseId;
	}

	public String getDatabaseType() {
		return databaseType;
	}

	public void setDatabaseType(String databaseType) {
		this.databaseType = databaseType;
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
		return "MetaCubeDbSchemaDto [name=" + name + ", schemaId=" + schemaId + ", schemaName=" + schemaName
				+ ", username=" + username + ", password=" + password + ", serviceName=" + serviceName + ", status="
				+ status + ", databaseId=" + databaseId + ", databaseType=" + databaseType + ", ip=" + ip + ", port="
				+ port + "]";
	}

}
