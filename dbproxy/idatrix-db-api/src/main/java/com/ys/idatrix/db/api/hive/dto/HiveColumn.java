package com.ys.idatrix.db.api.hive.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @ClassName: HiveColumn
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Getter
@Setter
@Accessors(chain = true)
public class HiveColumn implements Comparable<HiveColumn>,Serializable {

	private String columnName;

	private HiveDataType dataType;

	private String comment;

	/**
	 * 分区字段顺序，默认为0，表示不是分区字段
	 */
	private int partitionOrder;

	public HiveColumn() {
		super();
	}

	public HiveColumn(String columnName, HiveDataType dataType, String comment) {
		this.columnName = columnName;
		this.dataType = dataType;
		this.comment = comment;
		this.partitionOrder = 0;
	}

	@Override
	public int compareTo(HiveColumn other) {
		if (this.partitionOrder == other.partitionOrder) {
			return this.columnName.compareToIgnoreCase(other.columnName);
		} else {
			return this.partitionOrder - other.partitionOrder;
		}

	}

}
