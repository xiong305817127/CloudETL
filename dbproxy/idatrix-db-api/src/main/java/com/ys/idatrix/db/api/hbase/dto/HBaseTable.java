package com.ys.idatrix.db.api.hbase.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.LinkedHashSet;

/**
 * @ClassName: HBaseTable
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Getter
@Setter
@Accessors(chain = true)
public class HBaseTable implements Serializable {

	private static final long serialVersionUID = -1997467290352233667L;

	private String tableName;

	private String namespace;

	private int version = 1;

	private boolean immutableRows = false;

	private ColumnEncodedBytes columnEncodedBytes = ColumnEncodedBytes.NONE;

	private LinkedHashSet<HBaseColumn> columns = new LinkedHashSet<HBaseColumn>();

	private PrimaryKey primaryKey;

	public HBaseTable() {
		super();
	}

	public HBaseColumn[] getColumns() {
		HBaseColumn[] arr = new HBaseColumn[columns.size()];
		return columns.toArray(arr);
	}

	public void addColumn(HBaseColumn column) {
		this.columns.add(column);
	}

	public void removeColumn(HBaseColumn column) {
		columns.remove(column);
	}

}
