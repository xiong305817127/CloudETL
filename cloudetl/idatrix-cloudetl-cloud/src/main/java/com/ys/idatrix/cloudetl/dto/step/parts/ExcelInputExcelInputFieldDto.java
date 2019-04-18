/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.parts;

/**
 * SPExcelInput 的 ExcelInputField 域,等效  org.pentaho.di.trans.steps.excelinput.ExcelInputField
 * @author JW
 * @since 2017年6月7日
 *
 */
public class ExcelInputExcelInputFieldDto {

	String name;
	String typedesc;
	int length =-1;
	int precision=-1;
	String trimtypecode;
	boolean repeated =false;
	String format;
	String currencysymbol;
	String decimalsymbol;
	String groupsymbol;
	/**
	 * @return name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name 要设置的 name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return typedesc
	 */
	public String getTypedesc() {
		return typedesc;
	}
	/**
	 * @param typedesc 要设置的 typedesc
	 */
	public void setTypedesc(String typedesc) {
		this.typedesc = typedesc;
	}

	/**
	 * @return length
	 */
	public int getLength() {
		return length;
	}
	/**
	 * @param length 要设置的 length
	 */
	public void setLength(int length) {
		this.length = length;
	}
	/**
	 * @return precision
	 */
	public int getPrecision() {
		return precision;
	}
	/**
	 * @param precision 要设置的 precision
	 */
	public void setPrecision(int precision) {
		this.precision = precision;
	}
	/**
	 * @return trimtypecode
	 */
	public String getTrimtypecode() {
		return trimtypecode;
	}
	/**
	 * @param trimtypecode 要设置的 trimtypecode
	 */
	public void setTrimtypecode(String trimtypecode) {
		this.trimtypecode = trimtypecode;
	}
	/**
	 * @return repeated
	 */
	public boolean isRepeated() {
		return repeated;
	}
	/**
	 * @param repeated 要设置的 repeated
	 */
	public void setRepeated(boolean repeated) {
		this.repeated = repeated;
	}
	/**
	 * @return format
	 */
	public String getFormat() {
		return format;
	}
	/**
	 * @param format 要设置的 format
	 */
	public void setFormat(String format) {
		this.format = format;
	}
	/**
	 * @return currencysymbol
	 */
	public String getCurrencysymbol() {
		return currencysymbol;
	}
	/**
	 * @param currencysymbol 要设置的 currencysymbol
	 */
	public void setCurrencysymbol(String currencysymbol) {
		this.currencysymbol = currencysymbol;
	}
	/**
	 * @return decimalsymbol
	 */
	public String getDecimalsymbol() {
		return decimalsymbol;
	}
	/**
	 * @param decimalsymbol 要设置的 decimalsymbol
	 */
	public void setDecimalsymbol(String decimalsymbol) {
		this.decimalsymbol = decimalsymbol;
	}
	/**
	 * @return groupsymbol
	 */
	public String getGroupsymbol() {
		return groupsymbol;
	}
	/**
	 * @param groupsymbol 要设置的 groupsymbol
	 */
	public void setGroupsymbol(String groupsymbol) {
		this.groupsymbol = groupsymbol;
	}
	
}
