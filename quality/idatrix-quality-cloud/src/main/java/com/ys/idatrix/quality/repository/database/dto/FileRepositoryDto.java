package com.ys.idatrix.quality.repository.database.dto;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Id;
import javax.persistence.Table;

import com.ys.idatrix.quality.ext.utils.DatabaseHelper.FieldUpperCase;

@FieldUpperCase
@Table(catalog="idatrix.file.repository.tableName",name="QUALITY_FILE_REPOSITORY")
public class FileRepositoryDto {

	@Id
	private String id;
	
	private String objectId;
	private String renterId;
	private String owner;
	private String group;
	private String type;
	private String name;
	private String description;
	private String directory;
	private Date createTime;
	private Date updateTime ;
	private Date lastExecTime ;
	private String lastStatus = "Waiting" ;
	
	public FileRepositoryDto() {
		super();
	}

	/**
	 * @param objectId
	 * @param renterId
	 * @param owner
	 * @param group
	 * @param type
	 * @param name
	 * @param directory
	 */
	public FileRepositoryDto(String objectId, String renterId, String owner, String group, String type, String name, String directory) {
		super();
		this.id =   UUID.randomUUID().toString().replaceAll("-", "");
		this.objectId = objectId;
		this.renterId = renterId;
		this.owner = owner;
		this.group = group;
		this.type = type;
		this.name = name;
		this.directory = directory;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public String getRenterId() {
		return renterId;
	}

	public void setRenterId(String renterId) {
		this.renterId = renterId;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Date getLastExecTime() {
		return lastExecTime;
	}

	public void setLastExecTime(Date lastExecTime) {
		this.lastExecTime = lastExecTime;
	}

	public String getLastStatus() {
		return lastStatus;
	}

	public void setLastStatus(String lastStatus) {
		this.lastStatus = lastStatus;
	}

}
