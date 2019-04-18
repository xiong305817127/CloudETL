/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.entry.parts;

/**
 *
 * @author XH
 * @since 2017年6月29日
 *
 */
public class SetVariablesvariableNameDto {

	String variableName;
	String variableValue;
	int variableType;

	/**
	 * @return variableName
	 */
	public String getVariableName() {
		return variableName;
	}

	/**
	 * @param 设置
	 *            variableName
	 */
	public void setVariableName(String variableName) {
		this.variableName = variableName;
	}

	/**
	 * @return variableValue
	 */
	public String getVariableValue() {
		return variableValue;
	}

	/**
	 * @param 设置
	 *            variableValue
	 */
	public void setVariableValue(String variableValue) {
		this.variableValue = variableValue;
	}

	/**
	 * @return variableType
	 */
	public int getVariableType() {
		return variableType;
	}

	/**
	 * @param 设置
	 *            variableType
	 */
	public void setVariableType(int variableType) {
		this.variableType = variableType;
	}

}
