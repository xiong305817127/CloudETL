package com.ys.idatrix.cloudetl.dto.step.parts;

public class FormulaMetaFunctionDto {
	String fieldName;
	String formula;
	int valueType;
	int valueLength;
	int valuePrecision;
	String replaceField;

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public int getValueType() {
		return valueType;
	}

	public void setValueType(int valueType) {
		this.valueType = valueType;
	}

	public int getValueLength() {
		return valueLength;
	}

	public void setValueLength(int valueLength) {
		this.valueLength = valueLength;
	}

	public int getValuePrecision() {
		return valuePrecision;
	}

	public void setValuePrecision(int valuePrecision) {
		this.valuePrecision = valuePrecision;
	}

	public String getReplaceField() {
		return replaceField;
	}

	public void setReplaceField(String replaceField) {
		this.replaceField = replaceField;
	}

}
