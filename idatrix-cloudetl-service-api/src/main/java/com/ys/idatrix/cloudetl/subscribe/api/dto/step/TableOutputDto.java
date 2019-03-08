package com.ys.idatrix.cloudetl.subscribe.api.dto.step;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.OutputFieldsDto;

public class TableOutputDto   extends StepDto implements Serializable{

	private static final long serialVersionUID = 3529090933713545023L;

	public static  final String type ="TableOutput";

//	private  String connection	;//元数据定义的连接名
//	private  String schema	;//数据库schema
	private Long schemaId ;
	private  Long tableId	;//数据库表Id
	private  String table	;//数据库表名
	private  List<OutputFieldsDto> fields	;//插入字段对应列表 
	
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

	public List<OutputFieldsDto> getFields() {
		return fields;
	}

	public void setFields(List<OutputFieldsDto> fields) {
		this.fields = fields;
	}
	
	public void addField(OutputFieldsDto outputField) {
		if(this.fields == null) {
			this.fields =  new ArrayList<OutputFieldsDto>();
		}
		this.fields.add(outputField);
	}
	@Override
	public String getType() {
		return type;
	}

	@Override
	public boolean isJobStep() {
		return false;
	}

	@Override
	public String toString() {
		return "TableOutputDto [schemaId=" + schemaId +  ", tableId=" + tableId + ", table="
				+ table + ", fields=" + fields + ", toString()=" + super.toString() + "]";
	}


}
