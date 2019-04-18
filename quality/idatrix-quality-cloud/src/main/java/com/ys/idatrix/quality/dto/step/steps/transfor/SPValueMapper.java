/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step.steps.transfor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.valuemapper.ValueMapperMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.quality.dto.step.parts.ValueMappersourceValueDto;
import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.quality.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step -Value Mapper (值映射)
 * org.pentaho.di.trans.steps.valuemapper.ValueMapperMeta
 * 
 * @author XH
 * @since 2017年6月9日
 *
 */
@Component("SPValueMapper")
@Scope("prototype")
public class SPValueMapper implements StepParameter, StepDataRelationshipParser {

	String fieldToUse;
	String targetField;
	String nonMatchDefault;
	List<ValueMappersourceValueDto> sourceValue;

	/**
	 * @return fieldToUse
	 */
	public String getFieldToUse() {
		return fieldToUse;
	}

	/**
	 * @param fieldToUse
	 *            要设置的 fieldToUse
	 */
	public void setFieldToUse(String fieldToUse) {
		this.fieldToUse = fieldToUse;
	}

	/**
	 * @return targetField
	 */
	public String getTargetField() {
		return targetField;
	}

	/**
	 * @param targetField
	 *            要设置的 targetField
	 */
	public void setTargetField(String targetField) {
		this.targetField = targetField;
	}

	/**
	 * @return nonMatchDefault
	 */
	public String getNonMatchDefault() {
		return nonMatchDefault;
	}

	/**
	 * @param nonMatchDefault
	 *            要设置的 nonMatchDefault
	 */
	public void setNonMatchDefault(String nonMatchDefault) {
		this.nonMatchDefault = nonMatchDefault;
	}

	/**
	 * @return sourceValue
	 */
	public List<ValueMappersourceValueDto> getSourceValue() {
		return sourceValue;
	}

	/**
	 * @param sourceValue
	 *            要设置的 sourceValue
	 */
	public void setSourceValue(List<ValueMappersourceValueDto> sourceValue) {
		this.sourceValue = sourceValue;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("sourceValue", ValueMappersourceValueDto.class);
		return (SPValueMapper) JSONObject.toBean(jsonObj, SPValueMapper.class, classMap);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPValueMapper spValueMapper = new SPValueMapper();
		ValueMapperMeta valuemappermeta = (ValueMapperMeta) stepMetaInterface;

		spValueMapper.setNonMatchDefault(valuemappermeta.getNonMatchDefault());

		List<ValueMappersourceValueDto> sourceValueList = Lists.newArrayList();
		String[] sourceValues = valuemappermeta.getSourceValue();
		String[] targetValues = valuemappermeta.getTargetValue();
		for (int i = 0; i < sourceValues.length; i++) {
			ValueMappersourceValueDto valuemappersourcevaluedto = new ValueMappersourceValueDto();
			valuemappersourcevaluedto.setSourceValue(sourceValues[i]);
			valuemappersourcevaluedto.setTargetValue(targetValues[i]);
			sourceValueList.add(valuemappersourcevaluedto);
		}
		spValueMapper.setSourceValue(sourceValueList);
		spValueMapper.setTargetField(valuemappermeta.getTargetField());
		spValueMapper.setFieldToUse(valuemappermeta.getFieldToUse());
		return spValueMapper;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();

		SPValueMapper spValueMapper = (SPValueMapper) po;
		ValueMapperMeta valuemappermeta = (ValueMapperMeta) stepMetaInterface;

		valuemappermeta.setNonMatchDefault(spValueMapper.getNonMatchDefault());
		String[] sourceValues = new String[spValueMapper.getSourceValue().size()];
		String[] targetValues = new String[spValueMapper.getSourceValue().size()];
		for (int i = 0; i < spValueMapper.getSourceValue().size(); i++) {
			ValueMappersourceValueDto valuemappersourcevaluedto = spValueMapper.getSourceValue().get(i);
			sourceValues[i] = valuemappersourcevaluedto.getSourceValue();
			targetValues[i] = valuemappersourcevaluedto.getTargetValue();
		}
		valuemappermeta.setSourceValue(sourceValues);
		valuemappermeta.setTargetValue(targetValues);
		valuemappermeta.setFieldToUse(spValueMapper.getFieldToUse());
		valuemappermeta.setTargetField(spValueMapper.getTargetField());

	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {
		
		String from= "转换:"+transMeta.getName()+",步骤:"+stepMeta.getName();
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		ValueMapperMeta valuemappermeta = (ValueMapperMeta) stepMetaInterface;
		//输入
		String in= valuemappermeta.getFieldToUse() ;
		//输出
		String out= valuemappermeta.getTargetField();
		if(!Utils.isEmpty(out)) {
			sdr.addRelationship( RelationshipUtil.buildFieldRelationship(from, in , out) );
		}
		
	}

}
