/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step.parts;

/**
 * SPSetValueConstant 的 fieldName 域,
 * @author JW
 * @since 2017年6月13日
 *
 */
public class SetValueConstantfieldNameDto {
	String fieldName;
	String replaceValue;
	String replaceMask;
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
	 * @return replaceValue
	 */
	public String getReplaceValue() {
		return replaceValue;
	}

	/**
	 * @param replaceValue
	 *            要设置的 replaceValue
	 */
	public void setReplaceValue(String replaceValue) {
		this.replaceValue = replaceValue;
	}

	/**
	 * @return replaceMask
	 */
	public String getReplaceMask() {
		return replaceMask;
	}

	/**
	 * @param replaceMask
	 *            要设置的 replaceMask
	 */
	public void setReplaceMask(String replaceMask) {
		this.replaceMask = replaceMask;
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
