/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.parts;

/**
 * SPValueMapper 的 sourceValue 域
 * @author JW
 * @since 2017年6月9日
 *
 */
public class ValueMappersourceValueDto {
	String sourceValue;
	String targetValue;
	/**
	 * @return sourceValue
	 */
	public String getSourceValue() {
		return sourceValue;
	}
	/**
	 * @param sourceValue 要设置的 sourceValue
	 */
	public void setSourceValue(String sourceValue) {
		this.sourceValue = sourceValue;
	}
	/**
	 * @return targetValue
	 */
	public String getTargetValue() {
		return targetValue;
	}
	/**
	 * @param targetValue 要设置的 targetValue
	 */
	public void setTargetValue(String targetValue) {
		this.targetValue = targetValue;
	}
	
}
