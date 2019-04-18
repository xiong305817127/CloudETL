/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.parts;

/**
 * SPFieldSplitter 的 fieldName 域
 * @author JW
 * @since 2017年6月13日
 *
 */
public class FieldSplitterfieldNameDto {
	String fieldName;
	String fieldID;
	boolean fieldRemoveID;
	int fieldType;
	String fieldFormat;
	String fieldGroup;
	String fieldDecimal;
	String fieldCurrency;
	int fieldLength =-1;
	int fieldPrecision =-1;
	String fieldNullIf;
	String fieldIfNull;
	String trimType = "none";

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
	 * @return fieldID
	 */
	public String getFieldID() {
		return fieldID;
	}

	/**
	 * @param fieldID
	 *            要设置的 fieldID
	 */
	public void setFieldID(String fieldID) {
		this.fieldID = fieldID;
	}

	/**
	 * @return fieldRemoveID
	 */
	public boolean isFieldRemoveID() {
		return fieldRemoveID;
	}

	/**
	 * @param fieldRemoveID
	 *            要设置的 fieldRemoveID
	 */
	public void setFieldRemoveID(boolean fieldRemoveID) {
		this.fieldRemoveID = fieldRemoveID;
	}

	/**
	 * @return fieldType
	 */
	public int getFieldType() {
		return fieldType;
	}

	/**
	 * @param fieldType
	 *            要设置的 fieldType
	 */
	public void setFieldType(int fieldType) {
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
	 * @return fieldGroup
	 */
	public String getFieldGroup() {
		return fieldGroup;
	}

	/**
	 * @param fieldGroup
	 *            要设置的 fieldGroup
	 */
	public void setFieldGroup(String fieldGroup) {
		this.fieldGroup = fieldGroup;
	}

	/**
	 * @return fieldDecimal
	 */
	public String getFieldDecimal() {
		return fieldDecimal;
	}

	/**
	 * @param fieldDecimal
	 *            要设置的 fieldDecimal
	 */
	public void setFieldDecimal(String fieldDecimal) {
		this.fieldDecimal = fieldDecimal;
	}

	/**
	 * @return fieldCurrency
	 */
	public String getFieldCurrency() {
		return fieldCurrency;
	}

	/**
	 * @param fieldCurrency
	 *            要设置的 fieldCurrency
	 */
	public void setFieldCurrency(String fieldCurrency) {
		this.fieldCurrency = fieldCurrency;
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
	 * @return fieldNullIf
	 */
	public String getFieldNullIf() {
		return fieldNullIf;
	}

	/**
	 * @param fieldNullIf
	 *            要设置的 fieldNullIf
	 */
	public void setFieldNullIf(String fieldNullIf) {
		this.fieldNullIf = fieldNullIf;
	}

	/**
	 * @return fieldIfNull
	 */
	public String getFieldIfNull() {
		return fieldIfNull;
	}

	/**
	 * @param fieldIfNull
	 *            要设置的 fieldIfNull
	 */
	public void setFieldIfNull(String fieldIfNull) {
		this.fieldIfNull = fieldIfNull;
	}

	public String getTrimType() {
		return trimType;
	}

	public void setTrimType(String trimType) {
		this.trimType = trimType;
	}

	

}
