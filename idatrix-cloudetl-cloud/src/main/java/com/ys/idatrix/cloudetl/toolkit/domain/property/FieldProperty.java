/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.cloudetl.toolkit.domain.property;

import java.io.Serializable;

/**
 * FieldProperty - 字段属性 <br/>
 * @author JW
 * @since 2017年11月16日
 * 
 */
public class FieldProperty   extends BaseProperty  implements Serializable{

	private static final long serialVersionUID = 7681538327159828474L;

	// 数据库域名别名 - 别名 
	private String aliasField;
	
	// Type - 类型
	private String fieldType;

	// Length - 长度
	private long fieldLength;

	// Pricision - 精度
	private long fieldPricision;

	// Format - 格式
	private String fieldFormat;

	// 是否主键
	private boolean isPrimaryKey;

	// 是否允许为空
	private boolean isNull;

	// Description - 描述说明
	private String desc;


	public FieldProperty(String name) {
		super(name);
	}

	public String getAliasField() {
		return aliasField;
	}

	public void setAliasField(String aliasField) {
		this.aliasField = aliasField;
	}

	/**
	 * @return fieldType
	 */
	public String getFieldType() {
		return fieldType;
	}

	/**
	 * @param fieldType 要设置的 fieldType
	 */
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	/**
	 * @return fieldLength
	 */
	public long getFieldLength() {
		return fieldLength;
	}

	/**
	 * @param fieldLength 要设置的 fieldLength
	 */
	public void setFieldLength(long fieldLength) {
		this.fieldLength = fieldLength;
	}

	/**
	 * @return fieldPricision
	 */
	public long getFieldPricision() {
		return fieldPricision;
	}

	/**
	 * @param fieldPricision 要设置的 fieldPricision
	 */
	public void setFieldPricision(long fieldPricision) {
		this.fieldPricision = fieldPricision;
	}

	/**
	 * @return fieldFormat
	 */
	public String getFieldFormat() {
		return fieldFormat;
	}

	/**
	 * @param fieldFormat 要设置的 fieldFormat
	 */
	public void setFieldFormat(String fieldFormat) {
		this.fieldFormat = fieldFormat;
	}

	/**
	 * @return isPrimaryKey
	 */
	public boolean isPrimaryKey() {
		return isPrimaryKey;
	}

	/**
	 * @param isPrimaryKey 要设置的 isPrimaryKey
	 */
	public void setPrimaryKey(boolean isPrimaryKey) {
		this.isPrimaryKey = isPrimaryKey;
	}

	/**
	 * @return isNull
	 */
	public boolean isNull() {
		return isNull;
	}

	/**
	 * @param isNull 要设置的 isNull
	 */
	public void setNull(boolean isNull) {
		this.isNull = isNull;
	}

	/**
	 * @return desc
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * @param desc 要设置的 desc
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

}
