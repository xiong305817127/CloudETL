/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.parts;

import org.pentaho.di.core.row.ValueMetaInterface;

/**
 *  SPCsvInput 的  TextFileInputField域,等效  org.pentaho.di.trans.steps.textfileinput.TextFileInputField
 * @author JW
 * @since 2017年6月7日
 *
 */
public class CsvInputTextFileInputFieldDto {

	String name;
	int type =ValueMetaInterface.TYPE_STRING;
	String format;
	String currencysymbol;
	String decimalsymbol;
	String groupsymbol;
	int length =-1;
	int  precision =-1;
	String trimType = "none";
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
	 * @return type
	 */
	public int getType() {
		return type;
	}
	/**
	 * @param type 要设置的 type
	 */
	public void setType(int type) {
		this.type = type;
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
	public String getTrimType() {
		return trimType;
	}
	public void setTrimType(String trimType) {
		this.trimType = trimType;
	}

}
