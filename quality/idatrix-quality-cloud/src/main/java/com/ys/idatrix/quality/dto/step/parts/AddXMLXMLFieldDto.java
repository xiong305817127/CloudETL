/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step.parts;

/**
 *  AddXML 的 XMLField 域DTO,等效 repalce org.pentaho.di.trans.steps.addxml.XMLField
 * @author XH
 * @since 2017年6月21日
 *
 */
public class AddXMLXMLFieldDto {
	String fieldname;
	String elementname;
	int type;
	String format;
	String currencysymbol;
	String decimalsymbol;
	String groupingsymbol;
	String nullstring;
	int length =-1;
	int precision =-1;
	boolean attribute;
	String attributeparentname;
	/**
	 * @return fieldname
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
	 * @return elementname
	 */
	public String getElementname() {
		return elementname;
	}
	/**
	 * @param  设置 elementname
	 */
	public void setElementname(String elementname) {
		this.elementname = elementname;
	}

	/**
	 * @return type
	 */
	public int getType() {
		return type;
	}
	/**
	 * @param  设置 type
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
	 * @param  设置 format
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
	 * @param  设置 currencysymbol
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
	 * @param  设置 decimalsymbol
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
	 * @param  设置 groupingsymbol
	 */
	public void setGroupingsymbol(String groupingsymbol) {
		this.groupingsymbol = groupingsymbol;
	}
	/**
	 * @return nullstring
	 */
	public String getNullstring() {
		return nullstring;
	}
	/**
	 * @param  设置 nullstring
	 */
	public void setNullstring(String nullstring) {
		this.nullstring = nullstring;
	}
	/**
	 * @return length
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
	 * @return precision
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
	/**
	 * @return attribute
	 */
	public boolean isAttribute() {
		return attribute;
	}
	/**
	 * @param  设置 attribute
	 */
	public void setAttribute(boolean attribute) {
		this.attribute = attribute;
	}
	/**
	 * @return attributeparentname
	 */
	public String getAttributeparentname() {
		return attributeparentname;
	}
	/**
	 * @param  设置 attributeparentname
	 */
	public void setAttributeparentname(String attributeparentname) {
		this.attributeparentname = attributeparentname;
	}
	
	
	
}
