/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.parts;

/**
 * SPSetValueField 的 fieldName 域
 * @author JW
 * @since 2017年6月13日
 *
 */
public class SetValueFieldfieldNameDto {
	String fieldName;
	String replaceByFieldValue;
	/**
	 * @return fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}
	/**
	 * @param fieldName 要设置的 fieldName
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	/**
	 * @return replaceByFieldValue
	 */
	public String getReplaceByFieldValue() {
		return replaceByFieldValue;
	}
	/**
	 * @param replaceByFieldValue 要设置的 replaceByFieldValue
	 */
	public void setReplaceByFieldValue(String replaceByFieldValue) {
		this.replaceByFieldValue = replaceByFieldValue;
	}
	
}
