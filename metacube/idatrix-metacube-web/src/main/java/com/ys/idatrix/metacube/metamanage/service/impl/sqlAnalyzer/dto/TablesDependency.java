package com.ys.idatrix.metacube.metamanage.service.impl.sqlAnalyzer.dto;

public class TablesDependency {
	
	private Long schemaId ;
	private String schemaName ;
	
	private Long tableId ;
	private String tableName ;
	
	private String columns;
	
	private Long refSchemaId ;
	private String refSchemaName ;
	
	private Long refTableId ;
	private String refTableName ;
	
	private String refColumns;

	public Long getSchemaId() {
		return schemaId;
	}

	public void setSchemaId(Long schemaId) {
		this.schemaId = schemaId;
	}

	public String getSchemaName() {
		return schemaName;
	}

	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	public Long getTableId() {
		return tableId;
	}

	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getColumns() {
		return columns;
	}

	public void setColumns(String columns) {
		this.columns = columns;
	}

	public Long getRefSchemaId() {
		return refSchemaId;
	}

	public void setRefSchemaId(Long refSchemaId) {
		this.refSchemaId = refSchemaId;
	}

	public String getRefSchemaName() {
		return refSchemaName;
	}

	public void setRefSchemaName(String refSchemaName) {
		this.refSchemaName = refSchemaName;
	}

	public Long getRefTableId() {
		return refTableId;
	}

	public void setRefTableId(Long resTableId) {
		this.refTableId = resTableId;
	}

	public String getRefTableName() {
		return refTableName;
	}

	public void setRefTableName(String refTableName) {
		this.refTableName = refTableName;
	}

	public String getRefColumns() {
		return refColumns;
	}

	public void setRefColumns(String refColumns) {
		this.refColumns = refColumns;
	}
	
	

}
