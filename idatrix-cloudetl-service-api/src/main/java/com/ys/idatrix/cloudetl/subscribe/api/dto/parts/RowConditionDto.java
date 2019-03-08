package com.ys.idatrix.cloudetl.subscribe.api.dto.parts;

import java.io.Serializable;
import java.util.List;

/**
 * SPFilterRows 的 condition域
 *
 * @author XH
 * @since 2017年9月4日
 *
 */
public class RowConditionDto  implements Serializable{

	private static final long serialVersionUID = 678306874286849046L;
	
	boolean negate;
	//"", "OR", "AND", "NOT", "OR NOT", "AND NOT", "XOR"
	int operators = 0;
	
	String leftvalue;
	//"=", "<>", "<", "<=", ">", ">=", "REGEXP", "IS NULL", "IS NOT NULL", "IN LIST", "CONTAINS", "STARTS WITH", "ENDS WITH", "LIKE", "TRUE" 
	String function="=";
	String rightvalue;

	//"Number", "String", "Date", "Boolean", "Integer", "BigNumber", "Serializable", "Binary", "Timestamp",   "Internet Address"
	String rightExactType = "String";
	String rightExactText;
	int rightExactLength = -1;
	int rightExactPrecision = -1;
	boolean rightExactIsnull;
	//yyyy/MM/dd HH:mm:ss.SSS,#.# 等...
	String rightExactMask;

	List<RowConditionDto> conditions;

	public RowConditionDto() {
		super();
	}

	public RowConditionDto(String leftvalue, String function, String rightExactType, String rightExactText) {
		super();
		this.leftvalue = leftvalue;
		this.function = function;
		this.rightExactType = rightExactType;
		this.rightExactText = rightExactText;
	}

	public RowConditionDto(String leftvalue, String function, String rightExactType, String rightExactText, String rightExactMask) {
		super();
		this.leftvalue = leftvalue;
		this.function = function;
		this.rightExactType = rightExactType;
		this.rightExactText = rightExactText;
		this.rightExactMask = rightExactMask;
	}

	public RowConditionDto(String leftvalue, String function, String rightvalue) {
		super();
		this.leftvalue = leftvalue;
		this.function = function;
		this.rightvalue = rightvalue;
	}

	public boolean isNegate() {
		return negate;
	}

	public void setNegate(boolean negate) {
		this.negate = negate;
	}

	public int getOperators() {
		return operators;
	}

	public void setOperators(int operators) {
		this.operators = operators;
	}

	public String getLeftvalue() {
		return leftvalue;
	}

	public void setLeftvalue(String leftvalue) {
		this.leftvalue = leftvalue;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getRightvalue() {
		return rightvalue;
	}

	public void setRightvalue(String rightvalue) {
		this.rightvalue = rightvalue;
	}

	public String getRightExactType() {
		return rightExactType;
	}

	public void setRightExactType(String rightExactType) {
		this.rightExactType = rightExactType;
	}

	public String getRightExactText() {
		return rightExactText;
	}

	public void setRightExactText(String rightExactText) {
		this.rightExactText = rightExactText;
	}

	public int getRightExactLength() {
		return rightExactLength;
	}

	public void setRightExactLength(int rightExactLength) {
		this.rightExactLength = rightExactLength;
	}

	public int getRightExactPrecision() {
		return rightExactPrecision;
	}

	public void setRightExactPrecision(int rightExactPrecision) {
		this.rightExactPrecision = rightExactPrecision;
	}

	public boolean isRightExactIsnull() {
		return rightExactIsnull;
	}

	public void setRightExactIsnull(boolean rightExactIsnull) {
		this.rightExactIsnull = rightExactIsnull;
	}

	public String getRightExactMask() {
		return rightExactMask;
	}

	public void setRightExactMask(String rightExactMask) {
		this.rightExactMask = rightExactMask;
	}

	public List<RowConditionDto> getConditions() {
		return conditions;
	}

	public void setConditions(List<RowConditionDto> conditions) {
		this.conditions = conditions;
	}

	/**
	 * 多条件快捷增加，会覆盖子条件
	 * @param negate 是否整体取非
	 * @param conditions 条件列表
	 * @param operators conditions条件间的操作符 ，operators.length = conditions.size()-1,长度不够，and(2)来凑
	 */
	public void addConditions(boolean negate,List<RowConditionDto> conditions,int... operators) {
		if(conditions != null && conditions.size() >1 ) {
			//多条件
			this.negate = negate;
			for(int i=1;i<conditions.size();i++) {
				int op = 2;
				if(operators.length>=i) {
					op=operators[i-1];
				}
				conditions.get(i).setOperators(op);
			}
			this.conditions = conditions;
		}
	}
	
	/**
	 * 多条件快捷增加，会覆盖子条件
	 * @param conditions 条件列表
	 * @param operators conditions条件间的操作符 ，operators.length = conditions.size()-1,长度不够，and(2)来凑
	 */
	public void addConditions(List<RowConditionDto> conditions,int... operators) {
		addConditions(false, conditions, operators);
	}

	@Override
	public String toString() {
		return "RowConditionDto [negate=" + negate + ", operators=" + operators + ", leftvalue=" + leftvalue
				+ ", function=" + function + ", rightvalue=" + rightvalue + ", rightExactType=" + rightExactType
				+ ", rightExactText=" + rightExactText + ", rightExactLength=" + rightExactLength
				+ ", rightExactPrecision=" + rightExactPrecision + ", rightExactIsnull=" + rightExactIsnull
				+ ", rightExactMask=" + rightExactMask + ", conditions=" + conditions + "]";
	}

}
