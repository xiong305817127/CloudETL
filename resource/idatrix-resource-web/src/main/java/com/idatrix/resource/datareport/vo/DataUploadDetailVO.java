package com.idatrix.resource.datareport.vo;

import java.util.Date;

/**
 * 数据上报详细信息表
 * @Description： 数据上报时,上传数据所生成的信息
 * @Date: 2018/06/11
 */

public class DataUploadDetailVO {
	/*主键*/
	private Long id;

	/*Data Upload表主键 */
	private Long parentId;

	/*原始库中的文件名(UUID)*/
	private String originFileName;

	/*发布出来的文件名*/
	private String pubFileName;

	/* 文件大小 */
	private String fileSize;

	/* 文件类型 */
	private String fileType;

	/*文件描述 */
	private String fileDescription;

	private String creator;

	private Date createTime;

	private String modifier;

	private Date modifyTime;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
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

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileDescription() {
		return fileDescription;
	}

	public void setFileDescription(String fileDescription) {
		this.fileDescription = fileDescription;
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
		return "DataUploadDetailPO{" +
				"id=" + id +
				", parentId=" + parentId +
				", originFileName='" + originFileName + '\'' +
				", pubFileName='" + pubFileName + '\'' +
				", fileSize='" + fileSize + '\'' +
				", fileType='" + fileType + '\'' +
				", fileDescription='" + fileDescription + '\'' +
				", creator='" + creator + '\'' +
				", createTime=" + createTime +
				", modifier='" + modifier + '\'' +
				", modifyTime=" + modifyTime +
				'}';
	}
}
