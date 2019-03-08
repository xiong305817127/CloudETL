package com.ys.idatrix.cloudetl.dto.step.parts;

public class GetVariableFieldDefinitionDto {

	String fieldname;
	String variablestring;
	int fieldtype;
	String fieldformat;
	String currency;
	String decimal;
	String group;
	int length = -1;
	int precision = -1;
	String trimType = "none";;
	/**
	 * @return the fieldname
	 */
	public String getFieldname() {
		return fieldname;
	}
	/**
	 * @param  设置 fieldname
	 */
	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}
	/**
	 * @return the variablestring
	 */
	public String getVariablestring() {
		return variablestring;
	}
	/**
	 * @param  设置 variablestring
	 */
	public void setVariablestring(String variablestring) {
		this.variablestring = variablestring;
	}
	/**
	 * @return the fieldtype
	 */
	public int getFieldtype() {
		return fieldtype;
	}
	/**
	 * @param  设置 fieldtype
	 */
	public void setFieldtype(int fieldtype) {
		this.fieldtype = fieldtype;
	}
	/**
	 * @return the fieldformat
	 */
	public String getFieldformat() {
		return fieldformat;
	}
	/**
	 * @param  设置 fieldformat
	 */
	public void setFieldformat(String fieldformat) {
		this.fieldformat = fieldformat;
	}
	/**
	 * @return the currency
	 */
	public String getCurrency() {
		return currency;
	}
	/**
	 * @param  设置 currency
	 */
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	/**
	 * @return the decimal
	 */
	public String getDecimal() {
		return decimal;
	}
	/**
	 * @param  设置 decimal
	 */
	public void setDecimal(String decimal) {
		this.decimal = decimal;
	}
	/**
	 * @return the group
	 */
	public String getGroup() {
		return group;
	}
	/**
	 * @param  设置 group
	 */
	public void setGroup(String group) {
		this.group = group;
	}
	/**
	 * @return the length
	 */
	public int getLength() {
		return length;
	}
	/**
	 * @param  设置 length
	 */
	public void setLength(int length) {
		this.length = length;
	}
	/**
	 * @return the precision
	 */
	public int getPrecision() {
		return precision;
	}
	/**
	 * @param  设置 precision
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
