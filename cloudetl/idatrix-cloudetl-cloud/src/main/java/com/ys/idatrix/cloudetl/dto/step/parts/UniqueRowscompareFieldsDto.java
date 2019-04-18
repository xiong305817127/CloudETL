/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.parts;

/**
 * SPUniqueRows 的 compareFields域
 * @author JW
 * @since 2017年6月12日
 *
 */
public class UniqueRowscompareFieldsDto {

	String compareField;
	boolean caseInsensitive;

	/**
	 * @return compareFields
	 */
	public String getCompareField() {
		return compareField;
	}

	/**
	 * @param compareFields
	 *            要设置的 compareFields
	 */
	public void setCompareField(String compareFields) {
		this.compareField = compareFields;
	}

	/**
	 * @return caseInsensitive
	 */
	public boolean isCaseInsensitive() {
		return caseInsensitive;
	}

	/**
	 * @param caseInsensitive
	 *            要设置的 caseInsensitive
	 */
	public void setCaseInsensitive(boolean caseInsensitive) {
		this.caseInsensitive = caseInsensitive;
	}

}
