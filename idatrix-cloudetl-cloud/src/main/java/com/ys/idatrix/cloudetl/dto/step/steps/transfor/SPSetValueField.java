/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.steps.transfor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.setvaluefield.SetValueFieldMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.cloudetl.dto.step.parts.SetValueFieldfieldNameDto;
import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;

import net.sf.json.JSONObject;

/**
 * Step - Set Value Field(设置字段值)
 * org.pentaho.di.trans.steps.setvaluefield.SetValueFieldMeta
 * 
 * @author XH
 * @since 2017年6月13日
 *
 */
@Component("SPSetValueField")
@Scope("prototype")
public class SPSetValueField implements StepParameter, StepDataRelationshipParser {

	List<SetValueFieldfieldNameDto> fieldName;

	/**
	 * @return fieldName
	 */
	public List<SetValueFieldfieldNameDto> getFieldName() {
		return fieldName;
	}

	/**
	 * @param fieldName
	 *            要设置的 fieldName
	 */
	public void setFieldName(List<SetValueFieldfieldNameDto> fieldName) {
		this.fieldName = fieldName;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("fieldName", SetValueFieldfieldNameDto.class);
		return (SPSetValueField) JSONObject.toBean(jsonObj, SPSetValueField.class, classMap);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPSetValueField spSetValueField = new SPSetValueField();
		SetValueFieldMeta setvaluefieldmeta = (SetValueFieldMeta) stepMetaInterface;

		List<SetValueFieldfieldNameDto> fieldNameList = Lists.newArrayList();
		String[] fieldNames = setvaluefieldmeta.getFieldName();
		String[] replaceByFieldValues = setvaluefieldmeta.getReplaceByFieldValue();
		for (int i = 0; fieldNames != null && i < fieldNames.length; i++) {
			SetValueFieldfieldNameDto setvaluefieldfieldnamedto = new SetValueFieldfieldNameDto();
			setvaluefieldfieldnamedto.setFieldName(fieldNames[i]);
			setvaluefieldfieldnamedto.setReplaceByFieldValue(replaceByFieldValues[i]);
			fieldNameList.add(setvaluefieldfieldnamedto);
		}
		spSetValueField.setFieldName(fieldNameList);
		return spSetValueField;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPSetValueField spSetValueField = (SPSetValueField) po;
		SetValueFieldMeta setvaluefieldmeta = (SetValueFieldMeta) stepMetaInterface;

		if (spSetValueField.getFieldName() != null) {
			String[] fieldNames = new String[spSetValueField.getFieldName().size()];
			String[] replaceByFieldValues = new String[spSetValueField.getFieldName().size()];
			for (int i = 0; i < spSetValueField.getFieldName().size(); i++) {
				SetValueFieldfieldNameDto setvaluefieldfieldnamedto = spSetValueField.getFieldName().get(i);
				fieldNames[i] = setvaluefieldfieldnamedto.getFieldName();
				replaceByFieldValues[i] = setvaluefieldfieldnamedto.getReplaceByFieldValue();
			}
			setvaluefieldmeta.setFieldName(fieldNames);
			setvaluefieldmeta.setReplaceByFieldValue(replaceByFieldValues);
		}
	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) {
		//字段没有变化
	}

}
