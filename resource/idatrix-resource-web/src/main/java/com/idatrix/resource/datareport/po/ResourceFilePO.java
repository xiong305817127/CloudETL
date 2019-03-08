package com.idatrix.resource.datareport.po;

import java.util.Date;

/**
 * 政务信息资源文件表
 * @Description： 数据上报时,上传文件类时生成的政务信息资源文件表
 * @Date: 2018/06/11
 */
public class ResourceFilePO {
	/*主键*/
	private Long id;

	/*资源ID*/
	private Long resourceId;

	/*原始库中的文件名(UUID)*/
	private String originFileName;

	/*发布出来的文件名*/
	private String pubFileName;

	/*文件描述*/
	private String fileDescription;

	/*文件版本号，每次覆盖+1*/
	private Integer fileVersion;

	/*数据批次，格式为yyyy-MM-dd*/
	private String dataBatch;

	private String fileSize;

	private String fileType;

	private String creator;

	private Date createTime;

	private String modifier;

	private Date modifyTime;

	public String getFileDescription() {
		return fileDescription;
	}

	public void setFileDescription(String fileDescription) {
		this.fileDescription = fileDescription;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	public String getOriginFileName() {
		return originFileName;
	}

	public void setOriginFileName(String originFileName) {
		this.originFileName = originFileName;
	}

	public String getPubFileName() {
		return pubFileName;
	}

	public void setPubFileName(String pubFileName) {
		this.pubFileName = pubFileName;
	}

	public Integer getFileVersion() {
		return fileVersion;
	}

	public void setFileVersion(Integer fileVersion) {
		this.fileVersion = fileVersion;
	}

	public String getDataBatch() {
		return dataBatch;
	}

	public void setDataBatch(String dataBatch) {
		this.dataBatch = dataBatch;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getModifier() {
		return modifier;
	}

	public void setModifier(String modifier) {
		this.modifier = modifier;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	@Override
	public String toString() {
		return "ResourceFilePO{" +
				"id=" + id +
				", resourceId=" + resourceId +
				", originFileName='" + originFileName + '\'' +
				", pubFileName='" + pubFileName + '\'' +
				", fileVersion=" + fileVersion +
				", dataBatch='" + dataBatch + '\'' +
				", fileSize='" + fileSize + '\'' +
				", fileType='" + fileType + '\'' +
				", creator='" + creator + '\'' +
				", createTime=" + createTime +
				", modifier='" + modifier + '\'' +
				", modifyTime=" + modifyTime +
				'}';
	}
}