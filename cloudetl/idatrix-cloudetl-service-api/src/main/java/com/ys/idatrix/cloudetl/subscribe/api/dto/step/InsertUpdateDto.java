package com.ys.idatrix.cloudetl.subscribe.api.dto.step;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.OutputFieldsDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.SearchFieldsDto;

public class InsertUpdateDto   extends StepDto implements Serializable{

	private static final long serialVersionUID = 4886086680451187132L;

	public static  final String type ="InsertUpdate";

	private Long schemaId ;
	private Long tableId ;
	private String table	;//数据库表名
	private List<SearchFieldsDto> searchFields	;//查询域列表
	private  List<OutputFieldsDto> updateFields;//更新域列表
	
	private boolean incloudBatch = true ; //是否包含 以下三个字段
	private String batchValueKey ="ds_batch" ; //域值在 params中
	private String batchFieldName ="ds_batch" ; //数据批次域名,域值在 params中对应batchValueKey
	
	private boolean incloudFlag= true ;
	private String flagFieldName ="ds_sync_flag" ; //数据同步操作域名 ,插入会插入"I" , 更新会插入"U"
	
	private boolean incloudTime= true ;
	private String timeFieldName ="ds_sync_time" ; //数据同步时间域名

	/**
	 * 
	 */
	public InsertUpdateDto() {
		super();
		if(incloudBatch) {
			addUpdateField(new OutputFieldsDto(batchFieldName, batchFieldName));
		}
	}

	

	public Long getSchemaId() {
		return schemaId;
	}

	public void setSchemaId(Long schemaId) {
		this.schemaId = schemaId;
	}

	public Long getTableId() {
		return tableId;
	}
	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public List<SearchFieldsDto> getSearchFields() {
		return searchFields;
	}

	public void setSearchFields(List<SearchFieldsDto> searchFields) {
		this.searchFields = searchFields;
	}
	
	public void addSearchField(SearchFieldsDto searchFields) {
		if(this.searchFields == null) {
			this.searchFields =  new ArrayList<SearchFieldsDto>();
		}
		this.searchFields.add(searchFields);
	}

	public List<OutputFieldsDto> getUpdateFields() {
		return updateFields;
	}

	public void setUpdateFields(List<OutputFieldsDto> updateFields) {
		this.updateFields = updateFields;
	}
	
	public void addUpdateField(OutputFieldsDto outputField) {
		if(this.updateFields == null) {
			this.updateFields =  new ArrayList<OutputFieldsDto>();
		}
		this.updateFields.add(outputField);
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public boolean isJobStep() {
		return false;
	}
	

	public boolean isIncloudBatch() {
		return incloudBatch;
	}

	public String getBatchValueKey() {
		return batchValueKey;
	}

	public void setBatchValueKey(String batchValueKey) {
		this.batchValueKey = batchValueKey;
	}

	public void setIncloudBatch(boolean incloudBatch) {
		if(incloudBatch) {
			OutputFieldsDto batchField = new OutputFieldsDto(batchFieldName, batchFieldName);
			if(updateFields == null || !updateFields.contains(batchField)) {
				addUpdateField(batchField);
			}
		}else {
			//不进行批次注入,删除批次字段
			OutputFieldsDto batchField = new OutputFieldsDto(batchFieldName, batchFieldName);
			if(updateFields != null && updateFields.contains(batchField)) {
				updateFields.remove(batchField);
			}
		}
		this.incloudBatch = incloudBatch;
	}

	/**
	 * @return the batchFieldName
	 */
	public String getBatchFieldName() {
		return batchFieldName;
	}

	/**
	 * @param  设置 batchFieldName
	 */
	public void setBatchFieldName(String batchFieldName) {
		
		if(!this.batchFieldName.equals(batchFieldName)){
			//批次域名进行修改
			OutputFieldsDto oldBatchField = new OutputFieldsDto(this.batchFieldName, this.batchFieldName);
			if(updateFields != null && updateFields.contains(oldBatchField)) {
				//已经包含旧的批次域名,进行更新
				updateFields.remove(oldBatchField);
				addUpdateField(new OutputFieldsDto(batchFieldName, batchFieldName));
			}
		}
		this.batchFieldName = batchFieldName;
	}

	/**
	 * @return the flagFieldName
	 */
	public String getFlagFieldName() {
		return flagFieldName;
	}

	/**
	 * @param  设置 flagFieldName
	 */
	public void setFlagFieldName(String flagFieldName) {
		this.flagFieldName = flagFieldName;
	}
	
	public boolean isIncloudFlag() {
		return incloudFlag;
	}

	public void setIncloudFlag(boolean incloudFlag) {
		this.incloudFlag = incloudFlag;
	}
	/**
	 * @return the timeFieldName
	 */
	public String getTimeFieldName() {
		return timeFieldName;
	}

	/**
	 * @param  设置 timeFieldName
	 */
	public void setTimeFieldName(String timeFieldName) {
		this.timeFieldName = timeFieldName;
	}



	public boolean isIncloudTime() {
		return incloudTime;
	}

	public void setIncloudTime(boolean incloudTime) {
		this.incloudTime = incloudTime;
	}



	@Override
	public String toString() {
		return "InsertUpdateDto [schemaId=" + schemaId + ", tableId=" + tableId + ", table=" + table + ", searchFields="
				+ searchFields + ", updateFields=" + updateFields + ", incloudBatch=" + incloudBatch
				+ ", batchValueKey=" + batchValueKey + ", batchFieldName=" + batchFieldName + ", incloudFlag="
				+ incloudFlag + ", flagFieldName=" + flagFieldName + ", incloudTime=" + incloudTime + ", timeFieldName="
				+ timeFieldName + ", toString()=" + super.toString() + "]";
	}

}
