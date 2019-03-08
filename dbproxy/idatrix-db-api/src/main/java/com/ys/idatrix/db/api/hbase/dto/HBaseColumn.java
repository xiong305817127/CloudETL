package com.ys.idatrix.db.api.hbase.dto;

import java.io.Serializable;

/**
 * @ClassName: HBaseColumn
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
public class HBaseColumn implements Serializable {

	private static final long serialVersionUID = 3231736371614371102L;

	private String columnFamily;

	private String columnName;

	private DataType dataType;

	public HBaseColumn() {
		super();
	}

	public HBaseColumn(String columnFamily, String columnName, DataType dataType) {
		super();
		this.columnFamily = columnFamily;
		this.columnName = columnName;
		this.dataType = dataType;
	}

	public String getColumnFamily() {
		return columnFamily;
	}

	public String getColumnName() {
		return columnName;
	}

	public DataType getDataType() {
		return dataType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((columnFamily == null) ? 0 : columnFamily.hashCode());
		result = prime * result + ((columnName == null) ? 0 : columnName.hashCode());
		result = prime * result + ((dataType == null) ? 0 : dataType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HBaseColumn other = (HBaseColumn) obj;
		if (columnFamily == null) {
			if (other.columnFamily != null)
				return false;
		} else if (!columnFamily.equals(other.columnFamily))
			return false;
		if (columnName == null) {
			if (other.columnName != null)
				return false;
		} else if (!columnName.equals(other.columnName))
			return false;
		if (dataType != other.dataType)
			return false;
		return true;
	}

}
