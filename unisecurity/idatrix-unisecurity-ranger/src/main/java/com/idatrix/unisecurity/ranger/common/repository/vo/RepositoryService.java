package com.idatrix.unisecurity.ranger.common.repository.vo;

import java.util.Date;

public class RepositoryService {
	private int id;
	private String guid;
	private String isEnabled;
	private String createdBy;
	private String updatedBy;
//	private Date createTime;
//	private Date updateTime;
	private String type;
	private String name;
	private int policyVersion;
	private int tagVersion;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public String getIsEnabled() {
		return isEnabled;
	}
	public void setIsEnabled(String isEnabled) {
		this.isEnabled = isEnabled;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public String getUpdatedBy() {
		return updatedBy;
	}
	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
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
	public int getPolicyVersion() {
		return policyVersion;
	}
	public void setPolicyVersion(int policyVersion) {
		this.policyVersion = policyVersion;
	}
	public int getTagVersion() {
		return tagVersion;
	}
	public void setTagVersion(int tagVersion) {
		this.tagVersion = tagVersion;
	}
	@Override
	public String toString() {
		return "RepositoryService [id=" + id + ", guid=" + guid + ", isEnabled=" + isEnabled + ", createdBy="
				+ createdBy + ", updatedBy=" + updatedBy + ", type=" + type + ", name=" + name + ", policyVersion="
				+ policyVersion + ", tagVersion=" + tagVersion + "]";
	}
	
	
	

}
