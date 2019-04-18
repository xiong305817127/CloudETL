package com.ys.idatrix.cloudetl.subscribe.api.dto.step;

import java.io.Serializable;
import java.util.List;

public class TableInputDto  extends StepDto implements Serializable{

	private static final long serialVersionUID = 5135285165759937513L;

	public static  final String type ="TableInput";
	
	private Long schemaId ;
	//private String connection;//元数据定义的连接名
	//private String schema;//数据库schema
	private Long tableId ; //数据库表Id
	private String tableType;//数据库表类型 view/table
	private String table;//数据库表名

	private List<String> fields; //查询字段列表
	private String where; //sql where 条件 ，eg.  a>1 and b<3
	private String order; //sql order by 条件 ,默认incrementalField字段，eg.  a,b
	
	private String sql; //查询sql语句，以${ktr_row_start}为已处理增量界限(时间戳，序列)占位

	private String incremental;//增量方式 ：  可为空 ，为空则表示该任务不增量获取，不为空：可选 date,sequence，表示增量类型 日期/序列(数字型)
	private String incrementalField;//增量字段，时间戳字段或者序列字段
	private String incrementalInitValue;//增量初始值，默认为0，当序列从0开始时，需要传例如 -1
	
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

	public String getTableType() {
		return tableType;
	}

	public void setTableType(String tableType) {
		this.tableType = tableType;
	}

	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getWhere() {
		return where;
	}

	public void setWhere(String where) {
		this.where = where;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getIncrementalField() {
		return incrementalField;
	}

	public void setIncrementalField(String incrementalField) {
		this.incrementalField = incrementalField;
	}

	public String getSql() {
		return sql;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}

	public String getIncrementalInitValue() {
		return incrementalInitValue;
	}

	public void setIncrementalInitValue(String incrementalInitValue) {
		this.incrementalInitValue = incrementalInitValue;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public boolean isJobStep() {
		return false;
	}
	
	public String getIncremental() {
		return incremental;
	}
	public void setIncremental(String incremental) {
		this.incremental = incremental;
	}

	@Override
	public String toString() {
		return "TableInputDto [schemaId=" + schemaId + ", tableId=" + tableId + ", tableType=" + tableType + ", table="
				+ table + ", fields=" + fields + ", where=" + where + ", order=" + order + ", sql=" + sql
				+ ", incremental=" + incremental + ", incrementalField=" + incrementalField + ", incrementalInitValue="
				+ incrementalInitValue + ", toString()=" + super.toString() + "]";
	}

	
}
