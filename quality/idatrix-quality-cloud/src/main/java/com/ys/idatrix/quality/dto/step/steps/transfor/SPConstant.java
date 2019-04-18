/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step.steps.transfor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.constant.ConstantMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.quality.dto.step.parts.ConstantfieldNameDto;
import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;

import com.ys.idatrix.quality.toolkit.domain.Relationship;
import com.ys.idatrix.quality.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - Constant(增加常量) org.pentaho.di.trans.steps.constant.ConstantMeta
 * 
 * @author XH
 * @since 2017年6月12日
 *
 */
@Component("SPConstant")
@Scope("prototype")
public class SPConstant implements StepParameter, StepDataRelationshipParser {

	List<ConstantfieldNameDto> fieldName;

	/**
	 * @return fieldName
	 */
	public List<ConstantfieldNameDto> getFieldName() {
		return fieldName;
	}

	/**
	 * @param fieldName
	 *            要设置的 fieldName
	 */
	public void setFieldName(List<ConstantfieldNameDto> fieldName) {
		this.fieldName = fieldName;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("fieldName", ConstantfieldNameDto.class);
		return (SPConstant) JSONObject.toBean(jsonObj, SPConstant.class, classMap);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPConstant spConstant = new SPConstant();
		ConstantMeta constantmeta = (ConstantMeta) stepMetaInterface;

		List<ConstantfieldNameDto> fieldNameList = Lists.newArrayList();
		String[] fieldNames = constantmeta.getFieldName();
		String[] fieldTypes = constantmeta.getFieldType();
		String[] fieldFormats = constantmeta.getFieldFormat();
		String[] currencys = constantmeta.getCurrency();
		String[] decimals = constantmeta.getDecimal();
		String[] groups = constantmeta.getGroup();
		String[] values = constantmeta.getValue();
		int[] fieldLengths = constantmeta.getFieldLength();
		int[] fieldPrecisions = constantmeta.getFieldPrecision();
		boolean[] setEmptyStrings = constantmeta.isEmptyString();
		for (int i = 0; i < fieldNames.length; i++) {
			ConstantfieldNameDto constantfieldnamedto = new ConstantfieldNameDto();
			constantfieldnamedto.setFieldName(fieldNames[i]);
			constantfieldnamedto.setFieldType(fieldTypes[i]);
			constantfieldnamedto.setFieldFormat(fieldFormats[i]);
			constantfieldnamedto.setCurrency(currencys[i]);
			constantfieldnamedto.setDecimal(decimals[i]);
			constantfieldnamedto.setGroup(groups[i]);
			constantfieldnamedto.setValue(values[i]);
			constantfieldnamedto.setFieldLength(fieldLengths[i]);
			constantfieldnamedto.setFieldPrecision(fieldPrecisions[i]);
			constantfieldnamedto.setSetEmptyString(setEmptyStrings[i]);
			fieldNameList.add(constantfieldnamedto);
		}
		spConstant.setFieldName(fieldNameList);
		return spConstant;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPConstant spConstant = (SPConstant) po;
		ConstantMeta constantmeta = (ConstantMeta) stepMetaInterface;

		String[] fieldNames = new String[spConstant.getFieldName().size()];
		String[] fieldTypes = new String[spConstant.getFieldName().size()];
		String[] fieldFormats = new String[spConstant.getFieldName().size()];
		String[] currencys = new String[spConstant.getFieldName().size()];
		String[] decimals = new String[spConstant.getFieldName().size()];
		String[] groups = new String[spConstant.getFieldName().size()];
		String[] values = new String[spConstant.getFieldName().size()];
		int[] fieldLengths = new int[spConstant.getFieldName().size()];
		int[] fieldPrecisions = new int[spConstant.getFieldName().size()];
		boolean[] setEmptyStrings = new boolean[spConstant.getFieldName().size()];
		for (int i = 0; i < spConstant.getFieldName().size(); i++) {
			ConstantfieldNameDto constantfieldnamedto = spConstant.getFieldName().get(i);
			fieldNames[i] = constantfieldnamedto.getFieldName();
			fieldTypes[i] = constantfieldnamedto.getFieldType();
			fieldFormats[i] = constantfieldnamedto.getFieldFormat();
			currencys[i] = constantfieldnamedto.getCurrency();
			decimals[i] = constantfieldnamedto.getDecimal();
			groups[i] = constantfieldnamedto.getGroup();
			values[i] = constantfieldnamedto.getValue();
			fieldLengths[i] = constantfieldnamedto.getFieldLength();
			fieldPrecisions[i] = constantfieldnamedto.getFieldPrecision();
			setEmptyStrings[i] = constantfieldnamedto.isSetEmptyString();
		}
		constantmeta.setFieldName(fieldNames);
		constantmeta.setFieldType(fieldTypes);
		constantmeta.setFieldFormat(fieldFormats);
		constantmeta.setCurrency(currencys);
		constantmeta.setDecimal(decimals);
		constantmeta.setGroup(groups);
		constantmeta.setValue(values);
		constantmeta.setFieldLength(fieldLengths);
		constantmeta.setFieldPrecision(fieldPrecisions);
		constantmeta.setEmptyString(setEmptyStrings);

	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {
		
		String from= "转换:"+transMeta.getName()+",步骤:"+stepMeta.getName();
		
		ConstantMeta constantmeta = (ConstantMeta)stepMeta.getStepMetaInterface();
		String[] fieldNames = constantmeta.getFieldName();
		if(fieldNames != null ) {
			for(String field : fieldNames) {
				String dummyId = transMeta.getName()+"-"+stepMeta.getName()+"-"+field ;
				Relationship relationship = RelationshipUtil.buildDummyRelationship(from, dummyId, field);
				sdr.getDataRelationship().add(relationship);
				sdr.addInputDataNode( relationship.getStartNode());
			}
		}
		
	}

}
