package com.ys.idatrix.db.api.hbase.dto;

/**
 * 
 * 对应Phoenix的table属性COLUMN_ENCODED_BYTES，可以优化Phoenix查询效率</br>
 * 默认值是NONE，表示不优化，可兼容HBase原生API访问
 * @author libin
 *
 */
public enum ColumnEncodedBytes {
	
	BYTE1("1"), BYTE2("2"), BYTE3("3"), BYTE4("4"), NONE("'NONE'");

	private String value;

	private ColumnEncodedBytes(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
