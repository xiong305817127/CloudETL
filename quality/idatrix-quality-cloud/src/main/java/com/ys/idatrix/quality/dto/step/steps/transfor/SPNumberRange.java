/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step.steps.transfor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.numberrange.NumberRangeMeta;
import org.pentaho.di.trans.steps.numberrange.NumberRangeRule;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.quality.dto.step.parts.NumberRangeRuleDto;
import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.quality.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - Number Range(数值范围)
 * org.pentaho.di.trans.steps.numberrange.NumberRangeMeta
 * 
 * @author XH
 * @since 2017年6月13日
 *
 */
@Component("SPNumberRange")
@Scope("prototype")
public class SPNumberRange implements StepParameter, StepDataRelationshipParser {

	String inputField;
	String outputField;
	String fallBackValue;
	List<NumberRangeRuleDto> rules;

	/**
	 * @return inputField
	 */
	public String getInputField() {
		return inputField;
	}

	/**
	 * @param inputField
	 *            要设置的 inputField
	 */
	public void setInputField(String inputField) {
		this.inputField = inputField;
	}

	/**
	 * @return outputField
	 */
	public String getOutputField() {
		return outputField;
	}

	/**
	 * @param outputField
	 *            要设置的 outputField
	 */
	public void setOutputField(String outputField) {
		this.outputField = outputField;
	}

	/**
	 * @return fallBackValue
	 */
	public String getFallBackValue() {
		return fallBackValue;
	}

	/**
	 * @param fallBackValue
	 *            要设置的 fallBackValue
	 */
	public void setFallBackValue(String fallBackValue) {
		this.fallBackValue = fallBackValue;
	}

	/**
	 * @return rules
	 */
	public List<NumberRangeRuleDto> getRules() {
		return rules;
	}

	/**
	 * @param rules
	 *            要设置的 rules
	 */
	public void setRules(List<NumberRangeRuleDto> rules) {
		this.rules = rules;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("rules", NumberRangeRuleDto.class);
		return (SPNumberRange) JSONObject.toBean(jsonObj, SPNumberRange.class, classMap);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPNumberRange spNumberRange = new SPNumberRange();
		NumberRangeMeta numberrangemeta = (NumberRangeMeta) stepMetaInterface;

		spNumberRange.setInputField(numberrangemeta.getInputField());
		spNumberRange.setOutputField(numberrangemeta.getOutputField());
		spNumberRange.setFallBackValue(numberrangemeta.getFallBackValue());
		if (numberrangemeta.getRules() != null) {
			spNumberRange.setRules(numberrangemeta.getRules().stream().map(numrang -> {
				NumberRangeRuleDto nrld = new NumberRangeRuleDto();
				nrld.setLowerbound(numrang.getLowerBound());
				nrld.setUpperbound(numrang.getUpperBound());
				nrld.setValue(numrang.getValue());
				return nrld;
			}).collect(Collectors.toList()));
		}
		return spNumberRange;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPNumberRange spNumberRange = (SPNumberRange) po;
		NumberRangeMeta numberrangemeta = (NumberRangeMeta) stepMetaInterface;

		numberrangemeta.setFallBackValue(spNumberRange.getFallBackValue());
		numberrangemeta.setOutputField(spNumberRange.getOutputField());
		numberrangemeta.setInputField(spNumberRange.getInputField());
		if (spNumberRange.getRules() != null) {
			numberrangemeta.setRules(spNumberRange.getRules().stream().map(spnumrang -> {
				NumberRangeRule nrl = new NumberRangeRule(spnumrang.getLowerbound(), spnumrang.getUpperbound(),
						spnumrang.getValue());
				return nrl;
			}).collect(Collectors.toList()));
		}

	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {
		
		String from= "转换:"+transMeta.getName()+",步骤:"+stepMeta.getName();
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		NumberRangeMeta  numberrangemeta= (NumberRangeMeta )stepMetaInterface;
		//输入
		String input = numberrangemeta.getInputField();
		//输出
		String output = numberrangemeta.getOutputField();
		
		sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, input, output) );
		
	}

}
