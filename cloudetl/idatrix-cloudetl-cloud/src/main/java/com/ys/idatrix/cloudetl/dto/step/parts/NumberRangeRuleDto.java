/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.parts;

/**
 * SPNumberRange 的 rules 域,等效  org.pentaho.di.trans.steps.numberrange.NumberRangeRule
 * @author JW
 * @since 2017年6月13日
 *
 */
public class NumberRangeRuleDto {

	double lowerbound;
	double upperbound;
	String value;


	/**
	 * @return lowerbound
	 */
	public double getLowerbound() {
		return lowerbound;
	}

	/**
	 * @param lowerbound 要设置的 lowerbound
	 */
	public void setLowerbound(double lowerbound) {
		this.lowerbound = lowerbound;
	}

	/**
	 * @return upperbound
	 */
	public double getUpperbound() {
		return upperbound;
	}

	/**
	 * @param upperbound 要设置的 upperbound
	 */
	public void setUpperbound(double upperbound) {
		this.upperbound = upperbound;
	}

	/**
	 * @return value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            要设置的 value
	 */
	public void setValue(String value) {
		this.value = value;
	}

}
