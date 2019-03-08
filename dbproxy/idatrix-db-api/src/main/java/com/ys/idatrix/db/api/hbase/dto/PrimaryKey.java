package com.ys.idatrix.db.api.hbase.dto;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.HashSet;

/**
 * @ClassName: PrimaryKey
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
public class PrimaryKey implements Serializable {

	private static final long serialVersionUID = -4871597335191683270L;

	private String keyName;

	private HashSet<HBaseColumn> columnSet;

	public PrimaryKey() {
		super();
	}

	public PrimaryKey(String keyName, HBaseColumn... columns) {
		super();
		this.keyName = keyName;
		if (StringUtils.isEmpty(keyName)) {
			this.keyName = "pk";
		}
		if (columns == null || columns.length == 0) {
			throw new IllegalArgumentException("PrimaryKey 需要指定至少一个列名");
		}
		this.columnSet = new HashSet<HBaseColumn>();
		for (int i = 0; i < columns.length; i++) {
			this.columnSet.add(columns[i]);
		}

	}

	public String getKeyName() {
		return keyName;
	}

	public HashSet<HBaseColumn> getColumns() {
		return columnSet;
	}

}
