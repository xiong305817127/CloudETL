package com.ys.idatrix.db.api.hive.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.LinkedHashSet;


/**
 * @ClassName: HiveTable
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Getter
@Setter
@Accessors(chain = true)
public class HiveTable implements Serializable{


	private static final long serialVersionUID = 4615890711848667631L;
	/**
	 * 表名
	 */
	private String tableName;

	/**
	 * 数据库名
	 */
	private String database;

	private LinkedHashSet<HiveColumn> columns;

	/**
	 * 字段分隔符，默认'\t'
	 */
	private char fieldsTerminated;

	/**
	 * 行分隔符，默认'\n'
	 */
	private char linesTerminated;

	/**
	 * 表的备注
	 */
	private String comment;

	/**
	 * 存储文件类型
	 */
	private StoredType storedType;

	public HiveTable() {
		super();
	}

	public HiveTable(String tableName, String database, String comment) {
		this.tableName = tableName;
		this.database = database;
		this.comment = comment;
		this.storedType = StoredType.TEXTFILE;
		this.fieldsTerminated = '\t';
		this.linesTerminated = '\n';
		this.columns = new LinkedHashSet<>();
	}

	public HiveColumn[] getColumns() {
		HiveColumn[] arr = new HiveColumn[columns.size()];
		return columns.toArray(arr);
	}

	public void addColumn(HiveColumn column) {
		this.columns.add(column);
	}

	public void removeColumn(HiveColumn column) {
		columns.remove(column);
	}
	
}
