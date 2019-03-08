package com.ys.idatrix.cloudetl.dto.step.parts;

public class RegexEvalfieldNameDto {

	String fieldName;
	String fieldType;
	String fieldFormat;
	String fieldGroup;
	String fieldDecimal;
	int fieldLength = -1;
	int fieldPrecision = -1;
	String fieldNullIf;
	String fieldIfNull;
	int fieldTrimType;
	String fieldCurrency;
	
	
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getFieldType() {
		return fieldType;
	}
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}
	public String getFieldFormat() {
		return fieldFormat;
	}
	public void setFieldFormat(String fieldFormat) {
		this.fieldFormat = fieldFormat;
	}
	public String getFieldGroup() {
		return fieldGroup;
	}
	public void setFieldGroup(String fieldGroup) {
		this.fieldGroup = fieldGroup;
	}
	public String getFieldDecimal() {
		return fieldDecimal;
	}
	public void setFieldDecimal(String fieldDecimal) {
		this.fieldDecimal = fieldDecimal;
	}
	public int getFieldLength() {
		return fieldLength;
	}
	public void setFieldLength(int fieldLength) {
		this.fieldLength = fieldLength;
	}
	public int getFieldPrecision() {
		return fieldPrecision;
	}
	public void setFieldPrecision(int fieldPrecision) {
		this.fieldPrecision = fieldPrecision;
	}
	public String getFieldNullIf() {
		return fieldNullIf;
	}
	public void setFieldNullIf(String fieldNullIf) {
		this.fieldNullIf = fieldNullIf;
	}
	public String getFieldIfNull() {
		return fieldIfNull;
	}
	public void setFieldIfNull(String fieldIfNull) {
		this.fieldIfNull = fieldIfNull;
	}
	public int getFieldTrimType() {
		return fieldTrimType;
	}
	public void setFieldTrimType(int fieldTrimType) {
		this.fieldTrimType = fieldTrimType;
	}
	public String getFieldCurrency() {
		return fieldCurrency;
	}
	public void setFieldCurrency(String fieldCurrency) {
		this.fieldCurrency = fieldCurrency;
	}
	
	
	
}
