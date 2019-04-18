package com.ys.idatrix.quality.dto.step.parts;

public class SetVariablefieldNameDto {

	String fieldName;
	String variableName;
	int variableType;
	String defaultValue;
	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}
	/**
	 * @param  设置 fieldName
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	/**
	 * @return the variableName
	 */
	public String getVariableName() {
		return variableName;
	}
	/**
	 * @param  设置 variableName
	 */
	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}
	/**
	 * @return the variableType
	 */
	public int getVariableType() {
		return variableType;
	}
	/**
	 * @param  设置 variableType
	 */
	public void setVariableType(int variableType) {
		this.variableType = variableType;
	}
	/**
	 * @return the defaultValue
	 */
	public String getDefaultValue() {
		return defaultValue;
	}
	/**
	 * @param  设置 defaultValue
	 */
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	
}
