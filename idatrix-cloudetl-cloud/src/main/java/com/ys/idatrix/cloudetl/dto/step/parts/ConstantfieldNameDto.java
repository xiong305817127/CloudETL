/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.parts;

/**
 * SPConstant 的 fieldName
 * @author JW
 * @since 2017年6月12日
 *
 */
public class ConstantfieldNameDto {

	String fieldName;
	String fieldType;
	String fieldFormat;
	String currency;
	String decimal;
	String group;
	String value;
	int fieldLength =-1 ;
	int fieldPrecision =-1;
	boolean setEmptyString;

	/**
	 * @return fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @param fieldName
	 *            要设置的 fieldName
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * @return fieldType
	 */
	public String getFieldType() {
		return fieldType;
	}

	/**
	 * @param fieldType
	 *            要设置的 fieldType
	 */
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	/**
	 * @return fieldFormat
	 */
	public String getFieldFormat() {
		return fieldFormat;
	}

	/**
	 * @param fieldFormat
	 *            要设置的 fieldFormat
	 */
	public void setFieldFormat(String fieldFormat) {
		this.fieldFormat = fieldFormat;
	}

	/**
	 * @return currency
	 */
	public String getCurrency() {
		return currency;
	}

	/**
	 * @param currency
	 *            要设置的 currency
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}

	/**
	 * @return decimal
	 */
	public String getDecimal() {
		return decimal;
	}

	/**
	 * @param decimal
	 *            要设置的 decimal
	 */
	public void setDecimal(String decimal) {
		this.decimal = decimal;
	}

	/**
	 * @return group
	 */
	public String getGroup() {
		return group;
	}

	/**
	 * @param group
	 *            要设置的 group
	 */
	public void setGroup(String group) {
		this.group = group;
	}

	/**
	 * @return value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            要设置的 value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/**
	 * @return fieldLength
	 */
	public int getFieldLength() {
		return fieldLength;
	}

	/**
	 * @param fieldLength
	 *            要设置的 fieldLength
	 */
	public void setFieldLength(int fieldLength) {
		this.fieldLength = fieldLength;
	}

	/**
	 * @return fieldPrecision
	 */
	public int getFieldPrecision() {
		return fieldPrecision;
	}

	/**
	 * @param fieldPrecision
	 *            要设置的 fieldPrecision
	 */
	public void setFieldPrecision(int fieldPrecision) {
		this.fieldPrecision = fieldPrecision;
	}

	/**
	 * @return setEmptyString
	 */
	public boolean isSetEmptyString() {
		return setEmptyString;
	}

	/**
	 * @param setEmptyString
	 *            要设置的 setEmptyString
	 */
	public void setSetEmptyString(boolean setEmptyString) {
		this.setEmptyString = setEmptyString;
	}

}
