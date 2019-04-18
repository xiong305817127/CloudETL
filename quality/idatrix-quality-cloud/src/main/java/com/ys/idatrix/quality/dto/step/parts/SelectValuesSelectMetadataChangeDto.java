/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step.parts;

/**
 * SPSelectValues 的 selectMetadataChange 域,等效  org.pentaho.di.trans.steps.selectvalues.SelectMetadataChange
 * @author JW
 * @since 2017年6月13日
 *
 */
public class SelectValuesSelectMetadataChangeDto {

      String  name;
      String  rename;
      int    type;
      int  length =-1;
      int  precision =-1;
      String  conversionMask;
      boolean  dateFormatLenient ;
      String  dateFormatLocale;
      String  dateFormatTimeZone;
      boolean lenientStringToNumber;
      String  encoding;
      String  decimalSymbol;
      String  groupingSymbol;
      String  currencySymbol;
      int  storageType=-1 ;
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
	 * @return rename
	 */
	public String getRename() {
		return rename;
	}
	/**
	 * @param rename 要设置的 rename
	 */
	public void setRename(String rename) {
		this.rename = rename;
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
	 * @return conversionMask
	 */
	public String getConversionMask() {
		return conversionMask;
	}
	/**
	 * @param conversionMask 要设置的 conversionMask
	 */
	public void setConversionMask(String conversionMask) {
		this.conversionMask = conversionMask;
	}
	/**
	 * @return dateFormatLenient
	 */
	public boolean isDateFormatLenient() {
		return dateFormatLenient;
	}
	/**
	 * @param dateFormatLenient 要设置的 dateFormatLenient
	 */
	public void setDateFormatLenient(boolean dateFormatLenient) {
		this.dateFormatLenient = dateFormatLenient;
	}
	/**
	 * @return dateFormatLocale
	 */
	public String getDateFormatLocale() {
		return dateFormatLocale;
	}
	/**
	 * @param dateFormatLocale 要设置的 dateFormatLocale
	 */
	public void setDateFormatLocale(String dateFormatLocale) {
		this.dateFormatLocale = dateFormatLocale;
	}
	/**
	 * @return dateFormatTimeZone
	 */
	public String getDateFormatTimeZone() {
		return dateFormatTimeZone;
	}
	/**
	 * @param dateFormatTimeZone 要设置的 dateFormatTimeZone
	 */
	public void setDateFormatTimeZone(String dateFormatTimeZone) {
		this.dateFormatTimeZone = dateFormatTimeZone;
	}
	/**
	 * @return lenientStringToNumber
	 */
	public boolean isLenientStringToNumber() {
		return lenientStringToNumber;
	}
	/**
	 * @param lenientStringToNumber 要设置的 lenientStringToNumber
	 */
	public void setLenientStringToNumber(boolean lenientStringToNumber) {
		this.lenientStringToNumber = lenientStringToNumber;
	}
	/**
	 * @return encoding
	 */
	public String getEncoding() {
		return encoding;
	}
	/**
	 * @param encoding 要设置的 encoding
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}
	/**
	 * @return decimalSymbol
	 */
	public String getDecimalSymbol() {
		return decimalSymbol;
	}
	/**
	 * @param decimalSymbol 要设置的 decimalSymbol
	 */
	public void setDecimalSymbol(String decimalSymbol) {
		this.decimalSymbol = decimalSymbol;
	}
	/**
	 * @return groupingSymbol
	 */
	public String getGroupingSymbol() {
		return groupingSymbol;
	}
	/**
	 * @param groupingSymbol 要设置的 groupingSymbol
	 */
	public void setGroupingSymbol(String groupingSymbol) {
		this.groupingSymbol = groupingSymbol;
	}
	/**
	 * @return currencySymbol
	 */
	public String getCurrencySymbol() {
		return currencySymbol;
	}
	/**
	 * @param currencySymbol 要设置的 currencySymbol
	 */
	public void setCurrencySymbol(String currencySymbol) {
		this.currencySymbol = currencySymbol;
	}
	/**
	 * @return storageType
	 */
	public int getStorageType() {
		return storageType;
	}
	/**
	 * @param storageType 要设置的 storageType
	 */
	public void setStorageType(int storageType) {
		this.storageType = storageType;
	}
	
}
