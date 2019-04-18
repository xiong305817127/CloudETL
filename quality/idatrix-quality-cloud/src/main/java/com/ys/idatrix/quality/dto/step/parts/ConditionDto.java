package com.ys.idatrix.quality.dto.step.parts;

import java.util.List;

import org.pentaho.di.core.Condition;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.row.ValueMetaAndData;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaBase;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.core.row.value.ValueMetaString;

import com.google.common.collect.Lists;

/**
 * SPFilterRows 的 condition域,等效 org.pentaho.di.core.Condition
 *
 * @author XH
 * @since 2017年9月4日
 *
 */
public class ConditionDto {

	boolean negate;
	int operators = Condition.OPERATOR_NONE;
	String leftvalue;
	String function;
	String rightvalue;

	// v = new ValueMetaAndData(ValueMetaFactory.createValueMeta( "constant",
	// leftval.getType() ), null );
	String rightExactName = "constant";
	String rightExactType = "String";
	String rightExactText;
	int rightExactLength = -1;
	int rightExactPrecision = -1;
	boolean rightExactIsnull;
	String rightExactMask;

	List<ConditionDto> conditions;

	
	
	/**
	 * 
	 */
	public ConditionDto() {
		super();
	}
	
	public ConditionDto(Condition condition) {
		super();
		
		this.negate = condition.isNegated();
		this.operators = condition.getOperator();
		this.leftvalue = condition.getLeftValuename();
		this.function = condition.getFunctionDesc();
		this.rightvalue = condition.getRightValuename();

		ValueMetaAndData vmd = condition.getRightExact();
		if(vmd != null && vmd.getValueData() != null){
			rightExactText = vmd.getValueData().toString();
			ValueMetaInterface vmi = vmd.getValueMeta();
			if(vmi!=null){
				rightExactName = vmi.getName();
				rightExactType = vmi.getTypeDesc();
				rightExactLength = vmi.getLength();
				rightExactPrecision = vmi.getPrecision();
				rightExactIsnull = rightExactText == null ? true : false;
				rightExactMask = vmi.getConversionMask();
			}
		}

		if(!condition.isAtomic()){
			conditions =Lists.newArrayList() ;
			for (int i = 0; i < condition.nrConditions(); i++) {
				Condition c = condition.getCondition(i);
				ConditionDto frcd = new ConditionDto(c);
				conditions.add(frcd);
			}
		}
		
	}

	/**
	 * @return the negate
	 */
	public boolean isNegate() {
		return negate;
	}

	/**
	 * @param 设置
	 *            negate
	 */
	public void setNegate(boolean negate) {
		this.negate = negate;
	}

	/**
	 * @return the operators
	 */
	public int getOperators() {
		return operators;
	}

	/**
	 * @param 设置
	 *            operators
	 */
	public void setOperators(int operators) {
		this.operators = operators;
	}

	/**
	 * @return the leftvalue
	 */
	public String getLeftvalue() {
		return leftvalue;
	}

	/**
	 * @param 设置
	 *            leftvalue
	 */
	public void setLeftvalue(String leftvalue) {
		this.leftvalue = leftvalue;
	}

	/**
	 * @return the function
	 */
	public String getFunction() {
		return function;
	}

	/**
	 * @param 设置
	 *            function
	 */
	public void setFunction(String function) {
		this.function = function;
	}

	/**
	 * @return the rightvalue
	 */
	public String getRightvalue() {
		return rightvalue;
	}

	/**
	 * @param 设置
	 *            rightvalue
	 */
	public void setRightvalue(String rightvalue) {
		this.rightvalue = rightvalue;
	}

	/**
	 * @return the rightExactName
	 */
	public String getRightExactName() {
		return rightExactName;
	}

	/**
	 * @param 设置
	 *            rightExactName
	 */
	public void setRightExactName(String rightExactName) {
		this.rightExactName = rightExactName;
	}

	/**
	 * @return the rightExactType
	 */
	public String getRightExactType() {
		if(rightExactType == null ||rightExactType.length() ==0) {
			rightExactType = "String";
		}
		return rightExactType;
	}

	/**
	 * @param 设置
	 *            rightExactType
	 */
	public void setRightExactType(String rightExactType) {
		this.rightExactType = rightExactType;
	}

	/**
	 * @return the rightExactText
	 */
	public String getRightExactText() {
		return rightExactText;
	}

	/**
	 * @param 设置
	 *            rightExactText
	 */
	public void setRightExactText(String rightExactText) {
		this.rightExactText = rightExactText;
	}

	/**
	 * @return the rightExactLength
	 */
	public int getRightExactLength() {
		return rightExactLength;
	}

	/**
	 * @param 设置
	 *            rightExactLength
	 */
	public void setRightExactLength(int rightExactLength) {
		this.rightExactLength = rightExactLength;
	}

	/**
	 * @return the rightExactPrecision
	 */
	public int getRightExactPrecision() {
		return rightExactPrecision;
	}

	/**
	 * @param 设置
	 *            rightExactPrecision
	 */
	public void setRightExactPrecision(int rightExactPrecision) {
		this.rightExactPrecision = rightExactPrecision;
	}

	/**
	 * @return the rightExactIsnull
	 */
	public boolean isRightExactIsnull() {
		return rightExactIsnull;
	}

	/**
	 * @param 设置
	 *            rightExactIsnull
	 */
	public void setRightExactIsnull(boolean rightExactIsnull) {
		this.rightExactIsnull = rightExactIsnull;
	}

	/**
	 * @return the rightExactMask
	 */
	public String getRightExactMask() {
		return rightExactMask;
	}

	/**
	 * @param 设置
	 *            rightExactMask
	 */
	public void setRightExactMask(String rightExactMask) {
		this.rightExactMask = rightExactMask;
	}

	/**
	 * @return the conditions
	 */
	public List<ConditionDto> getConditions() {
		return conditions;
	}

	/**
	 * @param 设置
	 *            conditions
	 */
	public void setConditions(List<ConditionDto> conditions) {
		this.conditions = conditions;
	}

	public Condition transToCodition() throws Exception {

		Condition condition = new Condition();

		condition.setNegated(negate);
		condition.setOperator(this.operators);
		condition.setLeftValuename(this.leftvalue);
		condition.setFunction(Condition.getFunction(this.function));
		condition.setRightValuename(this.rightvalue);

		ValueMetaInterface vmi = ValueMetaFactory.createValueMeta(rightExactName,
				ValueMetaBase.getType(rightExactType));
		vmi.setLength(rightExactLength);
		vmi.setPrecision(rightExactPrecision);
		if (rightExactMask != null) {
			vmi.setConversionMask(rightExactMask);
		}
		Object valueData = rightExactText;
		if (ValueMetaBase.getType(rightExactType) != ValueMetaInterface.TYPE_STRING) {
			ValueMetaInterface originMeta = new ValueMetaString(rightExactName);
			if (vmi.isNumeric()) {
				originMeta.setDecimalSymbol(".");
				originMeta.setGroupingSymbol(null);
				originMeta.setCurrencySymbol(null);
			}
			if (ValueMetaBase.getType(rightExactType) == ValueMetaInterface.TYPE_DATE) {
				originMeta.setConversionMask(ValueMetaBase.COMPATIBLE_DATE_FORMAT_PATTERN);
			}
			valueData = Const.trim(rightExactText);
			valueData = vmi.convertData(originMeta, valueData);
		}
		if (rightExactIsnull) {
			valueData = null;
		}
		ValueMetaAndData vmd = new ValueMetaAndData(vmi, valueData);
		condition.setRightExact(vmd);
		if (conditions != null && conditions.size() > 0) {
			for (int i = 0; i < conditions.size(); i++) {
				ConditionDto frCondition = conditions.get(i);
				condition.addCondition(frCondition.transToCodition());
			}
		}

		return condition;
	}

}
