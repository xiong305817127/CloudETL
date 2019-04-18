/**
 * 云化数据集成系统 
 * iDatrxi quality
 */
package com.ys.idatrix.quality.toolkit.domain.property;

import java.io.Serializable;

/**
 * TableProperty <br/>
 * @author JW
 * @since 2017年11月16日
 * 
 */
public class TableProperty  extends BaseProperty implements Serializable{

	private static final long serialVersionUID = 4022388220524912346L;

	private boolean isView = false; //是否是视图
	
	private String charset;

	private String createSQL;

	private String[] fields;
	
	private String[] primaryKeys;

	public TableProperty(String name) {
		super(name);
	}
	
	
	public boolean isView() {
		return isView;
	}

	public void setView(boolean isView) {
		this.isView = isView;
	}

	/**
	 * @return charset
	 */
	public String getCharset() {
		return charset;
	}

	/**
	 * @param charset 要设置的 charset
	 */
	public void setCharset(String charset) {
		this.charset = charset;
	}

	/**
	 * @return createSQL
	 */
	public String getCreateSQL() {
		return createSQL;
	}

	/**
	 * @param createSQL 要设置的 createSQL
	 */
	public void setCreateSQL(String createSQL) {
		this.createSQL = createSQL;
	}

	/**
	 * @return fields
	 */
	public String[] getFields() {
		return fields;
	}

	/**
	 * @param fields 要设置的 fields
	 */
	public void setFields(String[] fields) {
		this.fields = fields;
	}

	public String[] getPrimaryKeys() {
		return primaryKeys;
	}

	public void setPrimaryKeys(String[] primaryKeys) {
		this.primaryKeys = primaryKeys;
	}

}
