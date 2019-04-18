/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step.parts;

/**
 *  SPHBaseOutput 的  mappedColumns 域DTO,
 * @author XH
 * @since 2017年6月21日
 *
 */
public class MappedColumnDto {
	
	String alias;
	String columnFamily;
	String columnName;
	String type;
	String index;
	/**
	 * @return alias
	 */
	public String getAlias() {
		return alias;
	}
	/**
	 * @param  设置 alias
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}
	/**
	 * @return columnFamily
	 */
	public String getColumnFamily() {
		return columnFamily;
	}
	/**
	 * @param  设置 columnFamily
	 */
	public void setColumnFamily(String columnFamily) {
		this.columnFamily = columnFamily;
	}
	/**
	 * @return columnName
	 */
	public String getColumnName() {
		return columnName;
	}
	/**
	 * @param  设置 columnName
	 */
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	/**
	 * @return type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param  设置 type
	 */
	public void setType(String type) {
		this.type = type;
	}
	/**
	 * @return indexedVals
	 */
	public String getIndex() {
		return index;
	}
	/**
	 * @param  设置 indexedVals
	 */
	public void setIndex(String indexedVals) {
		this.index = indexedVals;
	}
	
	
}
