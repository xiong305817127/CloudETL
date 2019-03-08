package com.idatrix.resource.datareport.po;

import java.util.Date;

public class SearchDataUploadPO {
	private Long id;
	private String importTaskId;
	private String subscribeId;
	private String dataType;
	private String code;
	private String name;
	private String fileNames;
	private String dataBatch;
	private Date createTime;
	private Date importTime;
	private Long importCount;
	private String status;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getImportTaskId() {
		return importTaskId;
	}

	public void setImportTaskId(String importTaskId) {
		this.importTaskId = importTaskId;
	}

	public String getSubscribeId() {
		return subscribeId;
	}

	public void setSubscribeId(String subscribeId) {
		this.subscribeId = subscribeId;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFileNames() {
		return fileNames;
	}

	public void setFileNames(String fileNames) {
		this.fileNames = fileNames;
	}

	public String getDataBatch() {
		return dataBatch;
	}

	public void setDataBatch(String dataBatch) {
		this.dataBatch = dataBatch;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getImportTime() {
		return importTime;
	}

	public void setImportTime(Date importTime) {
		this.importTime = importTime;
	}

	public Long getImportCount() {
		return importCount;
	}

	public void setImportCount(Long importCount) {
		this.importCount = importCount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "SearchDataUploadPO{" +
				"id=" + id +
				", importTaskId='" + importTaskId + '\'' +
				", code='" + code + '\'' +
				", name='" + name + '\'' +
				", dataBatch='" + dataBatch + '\'' +
				", createTime=" + createTime +
				", importTime=" + importTime +
				", importCount=" + importCount +
				", status='" + status + '\'' +
				'}';
	}
}
