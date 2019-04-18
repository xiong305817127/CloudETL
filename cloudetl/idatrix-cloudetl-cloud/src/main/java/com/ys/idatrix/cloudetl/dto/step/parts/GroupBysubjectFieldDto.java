package com.ys.idatrix.cloudetl.dto.step.parts;

/**
 * SPGroupBy 的 subjectField 域
 *
 * @author XH
 * @since 2017年9月5日
 *
 */
public class GroupBysubjectFieldDto {
	
	String aggregateField;
	String subjectField;
	int aggregateType;
	String valueField;
	/**
	 * @return the aggregateField
	 */
	public String getAggregateField() {
		return aggregateField;
	}
	/**
	 * @param  设置 aggregateField
	 */
	public void setAggregateField(String aggregateField) {
		this.aggregateField = aggregateField;
	}
	/**
	 * @return the subjectField
	 */
	public String getSubjectField() {
		return subjectField;
	}
	/**
	 * @param  设置 subjectField
	 */
	public void setSubjectField(String subjectField) {
		this.subjectField = subjectField;
	}
	/**
	 * @return the aggregateType
	 */
	public int getAggregateType() {
		return aggregateType;
	}
	/**
	 * @param  设置 aggregateType
	 */
	public void setAggregateType(int aggregateType) {
		this.aggregateType = aggregateType;
	}
	/**
	 * @return the valueField
	 */
	public String getValueField() {
		return valueField;
	}
	/**
	 * @param  设置 valueField
	 */
	public void setValueField(String valueField) {
		this.valueField = valueField;
	}
	
	
}
