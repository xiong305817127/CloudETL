/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.parts;

/**
 * SPDenormaliser 的  denormaliserTargetField域 ,等效 org.pentaho.di.trans.steps.denormaliser.DenormaliserTargetField
 * @author JW
 * @since 2017年6月9日
 *
 */
public class DenormaliserDenormaliserTargetFieldDto {
	String fieldname;
	String keyvalue;
	String name;
	String typedesc;
	String format;
	int  length =-1;
	int  precision =-1;
	String decimalsymbol;
	String groupingsymbol;
	String currencysymbol;
	String nullstring;
	String aggregationtypedesc;
	/**
	 * @return fieldname
	 */
	public String getFieldname() {
		return fieldname;
	}
	/**
	 * @param fieldname 要设置的 fieldname
	 */
	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}
	/**
	 * @return keyvalue
	 */
	public String getKeyvalue() {
		return keyvalue;
	}
	/**
	 * @param keyvalue 要设置的 keyvalue
	 */
	public void setKeyvalue(String keyvalue) {
		this.keyvalue = keyvalue;
	}
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
	 * @return groupingsymbol
	 */
	public String getGroupingsymbol() {
		return groupingsymbol;
	}
	/**
	 * @param groupingsymbol 要设置的 groupingsymbol
	 */
	public void setGroupingsymbol(String groupingsymbol) {
		this.groupingsymbol = groupingsymbol;
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
	 * @return nullstring
	 */
	public String getNullstring() {
		return nullstring;
	}
	/**
	 * @param nullstring 要设置的 nullstring
	 */
	public void setNullstring(String nullstring) {
		this.nullstring = nullstring;
	}
	/**
	 * @return aggregationtypedesc
	 */
	public String getAggregationtypedesc() {
		return aggregationtypedesc;
	}
	/**
	 * @param aggregationtypedesc 要设置的 aggregationtypedesc
	 */
	public void setAggregationtypedesc(String aggregationtypedesc) {
		this.aggregationtypedesc = aggregationtypedesc;
	}
	
}
