/**
 * 云化数据集成系统 
 * iDatrix quality
 */
package com.ys.idatrix.quality.reference.metacube.dto;

import java.util.List;

/**
 * DTO - DB Connection overview from MetaCube.
 * 
 * @author JW
 * @since 2017年6月16日
 *
 */
public class MetaCubeDbDatabaseDto {

	private Long databaseId;
	private String name;
	private String type;
	private String ip;
	private Integer port;
	private int status;
	List<MetaCubeDbSchemaDto> schemaList ;
	
	public Long getDatabaseId() {
		return databaseId;
	}
	public void setDatabaseId(Long databaseId) {
		this.databaseId = databaseId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
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
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	public List<MetaCubeDbSchemaDto> getSchemaList() {
		return schemaList;
	}
	public void setSchemaList(List<MetaCubeDbSchemaDto> schemaList) {
		this.schemaList = schemaList;
	}
	@Override
	public String toString() {
		return "MetaCubeDbDatabaseDto [databaseId=" + databaseId + ", name=" + name + ", type=" + type + ", ip=" + ip
				+ ", port=" + port + ", status=" + status + ", schemaList=" + schemaList + "]";
	}

}
