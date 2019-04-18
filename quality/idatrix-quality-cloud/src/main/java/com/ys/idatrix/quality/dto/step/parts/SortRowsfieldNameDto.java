/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step.parts;

/**
 * SPSortRows 的 fieldName域
 * @author JW
 * @since 2017年6月13日
 *
 */
public class SortRowsfieldNameDto {

	String fieldName;
	boolean ascending;
	boolean caseSensitive;
	boolean collatorEnabled;
	int collatorStrength;
	boolean preSortedField;

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
	 * @return ascending
	 */
	public boolean isAscending() {
		return ascending;
	}

	/**
	 * @param ascending
	 *            要设置的 ascending
	 */
	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	/**
	 * @return caseSensitive
	 */
	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	/**
	 * @param caseSensitive
	 *            要设置的 caseSensitive
	 */
	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	/**
	 * @return collatorEnabled
	 */
	public boolean isCollatorEnabled() {
		return collatorEnabled;
	}

	/**
	 * @param collatorEnabled
	 *            要设置的 collatorEnabled
	 */
	public void setCollatorEnabled(boolean collatorEnabled) {
		this.collatorEnabled = collatorEnabled;
	}

	/**
	 * @return collatorStrength
	 */
	public int getCollatorStrength() {
		return collatorStrength;
	}

	/**
	 * @param collatorStrength
	 *            要设置的 collatorStrength
	 */
	public void setCollatorStrength(int collatorStrength) {
		this.collatorStrength = collatorStrength;
	}

	/**
	 * @return preSortedField
	 */
	public boolean isPreSortedField() {
		return preSortedField;
	}

	/**
	 * @param preSortedField
	 *            要设置的 preSortedField
	 */
	public void setPreSortedField(boolean preSortedField) {
		this.preSortedField = preSortedField;
	}

}
