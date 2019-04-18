/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step.parts;

/**
 * SpCalculator 等  calculation域,等效 org.pentaho.di.trans.steps.calculator.CalculatorMetaFunction
 * @author JW
 * @since 2017年6月13日
 *
 */
public class CalculatorCalculatorMetaFunctionDto {
	String fieldname;
	int calctype;
	String fielda;
	String fieldb;
	String fieldc;
	int valuetype;
	int valuelength =-1;
	int valueprecision =-1;
	boolean removedfromresult;
	String conversionmask;
	String decimalsymbol;
	String groupingsymbol;
	String currencysymbol;
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
	 * @return calctype
	 */
	public int getCalctype() {
		return calctype;
	}
	/**
	 * @param calctype 要设置的 calctype
	 */
	public void setCalctype(int calctype) {
		this.calctype = calctype;
	}
	/**
	 * @return fielda
	 */
	public String getFielda() {
		return fielda;
	}
	/**
	 * @param fielda 要设置的 fielda
	 */
	public void setFielda(String fielda) {
		this.fielda = fielda;
	}
	/**
	 * @return fieldb
	 */
	public String getFieldb() {
		return fieldb;
	}
	/**
	 * @param fieldb 要设置的 fieldb
	 */
	public void setFieldb(String fieldb) {
		this.fieldb = fieldb;
	}
	/**
	 * @return fieldc
	 */
	public String getFieldc() {
		return fieldc;
	}
	/**
	 * @param fieldc 要设置的 fieldc
	 */
	public void setFieldc(String fieldc) {
		this.fieldc = fieldc;
	}
	/**
	 * @return valuetype
	 */
	public int getValuetype() {
		return valuetype;
	}
	/**
	 * @param valuetype 要设置的 valuetype
	 */
	public void setValuetype(int valuetype) {
		this.valuetype = valuetype;
	}
	/**
	 * @return valuelength
	 */
	public int getValuelength() {
		return valuelength;
	}
	/**
	 * @param valuelength 要设置的 valuelength
	 */
	public void setValuelength(int valuelength) {
		this.valuelength = valuelength;
	}
	/**
	 * @return valueprecision
	 */
	public int getValueprecision() {
		return valueprecision;
	}
	/**
	 * @param valueprecision 要设置的 valueprecision
	 */
	public void setValueprecision(int valueprecision) {
		this.valueprecision = valueprecision;
	}
	/**
	 * @return removedfromresult
	 */
	public boolean isRemovedfromresult() {
		return removedfromresult;
	}
	/**
	 * @param removedfromresult 要设置的 removedfromresult
	 */
	public void setRemovedfromresult(boolean removedfromresult) {
		this.removedfromresult = removedfromresult;
	}
	/**
	 * @return conversionmask
	 */
	public String getConversionmask() {
		return conversionmask;
	}
	/**
	 * @param conversionmask 要设置的 conversionmask
	 */
	public void setConversionmask(String conversionmask) {
		this.conversionmask = conversionmask;
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
	
}
