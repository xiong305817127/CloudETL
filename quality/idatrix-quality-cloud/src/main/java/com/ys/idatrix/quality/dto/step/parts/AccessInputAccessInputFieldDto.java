/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step.parts;

import org.pentaho.di.core.row.ValueMetaInterface;

/**
 * step access input 组件的 InputField Dto 等效 org.pentaho.di.trans.steps.accessinput.AccessInputField
 * @author JW
 * @since 2017年6月7日
 *
 */
public class AccessInputAccessInputFieldDto {

	String name;
	String column;
	int typedesc = ValueMetaInterface.TYPE_STRING;
	String format;
	int length =-1;
	int precision =-1;
	String currencysymbol;
	String decimalsymbol;
	String groupsymbol;
	String trimType = "none";
	boolean repeated =false;
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
	 * @return column
	 */
	public String getColumn() {
		return column;
	}
	/**
	 * @param column 要设置的 column
	 */
	public void setColumn(String column) {
		this.column = column;
	}

	/**
	 * @return typedesc
	 */
	public int getTypedesc() {
		return typedesc;
	}
	/**
	 * @param typedesc 要设置的 typedesc
	 */
	public void setTypedesc(int typedesc) {
		this.typedesc = typedesc;
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

	public String getTrimType() {
		return trimType;
	}
	public void setTrimType(String trimType) {
		this.trimType = trimType;
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
	
	
	
}
