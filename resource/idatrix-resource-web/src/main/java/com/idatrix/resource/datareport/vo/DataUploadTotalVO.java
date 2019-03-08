package com.idatrix.resource.datareport.vo;

import java.util.List;

/**
 * 数据上报详细信息表
 * @Description： 数据上报时,上传数据所生成的信息
 * @Date: 2018/06/11
 */

public class DataUploadTotalVO {
	/* 资源ID */
	private Long resourceId;

	/* 数据批次 */
	String dataBatch;

	/* 数据类别 DB/FILE */
	String dataType;

	/* 数据上报细节信息 */
	List<DataUploadDetailVO> dataUploadDetailVOList;

	public Long getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long resourceId) {
		this.resourceId = resourceId;
	}

	public String getDataBatch() {
		return dataBatch;
	}

	public void setDataBatch(String dataBatch) {
		this.dataBatch = dataBatch;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public List<DataUploadDetailVO> getDataUploadDetailVOList() {
		return dataUploadDetailVOList;
	}

	public void setDataUploadDetailVOList(List<DataUploadDetailVO> dataUploadDetailVOList) {
		this.dataUploadDetailVOList = dataUploadDetailVOList;
	}

	@Override
	public String toString() {
		return "DataUploadTotalVO{" +
				"resourceId=" + resourceId +
				", dataBatch='" + dataBatch + '\'' +
				", dataType='" + dataType + '\'' +
				", dataUploadDetailVOList=" + dataUploadDetailVOList +
				'}';
	}
}
