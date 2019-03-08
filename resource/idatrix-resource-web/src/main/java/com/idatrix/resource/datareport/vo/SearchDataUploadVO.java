package com.idatrix.resource.datareport.vo;

public class SearchDataUploadVO {
	private Long id;
	private String importTaskId;
	private String subscribeId;
	private String dataType;
	private String code;
	private String name;
	private String pubFileName;
	private String dataBatch;
	private String createTime;
	private String importTime;
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

	public String getPubFileName() {
		return pubFileName;
	}

	public void setPubFileName(String pubFileName) {
		this.pubFileName = pubFileName;
	}

	public String getDataBatch() {
		return dataBatch;
	}

	public void setDataBatch(String dataBatch) {
		this.dataBatch = dataBatch;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getImportTime() {
		return importTime;
	}

	public void setImportTime(String importTime) {
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
}
