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
import org.pentaho.di.trans.steps.setvalueconstant.SetValueConstantMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.quality.dto.step.parts.SetValueConstantfieldNameDto;
import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;

import net.sf.json.JSONObject;

/**
 * Step -Set Value Constant(将字段值设置为常量)
 * org.pentaho.di.trans.steps.setvalueconstant.SetValueConstantMeta
 * 
 * @author XH
 * @since 2017年6月13日
 *
 */
@Component("SPSetValueConstant")
@Scope("prototype")
public class SPSetValueConstant implements StepParameter, StepDataRelationshipParser {

	boolean usevar;
	List<SetValueConstantfieldNameDto> fieldName;

	/**
	 * @return usevar
	 */
	public boolean isUsevar() {
		return usevar;
	}

	/**
	 * @param usevar
	 *            要设置的 usevar
	 */
	public void setUsevar(boolean usevar) {
		this.usevar = usevar;
	}

	/**
	 * @return fieldName
	 */
	public List<SetValueConstantfieldNameDto> getFieldName() {
		return fieldName;
	}

	/**
	 * @param fieldName
	 *            要设置的 fieldName
	 */
	public void setFieldName(List<SetValueConstantfieldNameDto> fieldName) {
		this.fieldName = fieldName;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("fieldName", SetValueConstantfieldNameDto.class);
		return (SPSetValueConstant) JSONObject.toBean(jsonObj, SPSetValueConstant.class, classMap);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPSetValueConstant spSetValueConstant = new SPSetValueConstant();
		SetValueConstantMeta setvalueconstantmeta = (SetValueConstantMeta) stepMetaInterface;

		List<SetValueConstantfieldNameDto> fieldNameList = Lists.newArrayList();
		String[] fieldNames = setvalueconstantmeta.getFieldName();
		String[] replaceValues = setvalueconstantmeta.getReplaceValue();
		String[] replaceMasks = setvalueconstantmeta.getReplaceMask();
		boolean[] setEmptyStrings = setvalueconstantmeta.isEmptyString();
		for (int i = 0; fieldNames != null && i < fieldNames.length; i++) {
			SetValueConstantfieldNameDto setvalueconstantfieldnamedto = new SetValueConstantfieldNameDto();
			setvalueconstantfieldnamedto.setFieldName(fieldNames[i]);
			setvalueconstantfieldnamedto.setReplaceValue(replaceValues[i]);
			setvalueconstantfieldnamedto.setReplaceMask(replaceMasks[i]);
			setvalueconstantfieldnamedto.setSetEmptyString(setEmptyStrings[i]);
			fieldNameList.add(setvalueconstantfieldnamedto);
		}
		spSetValueConstant.setFieldName(fieldNameList);

		spSetValueConstant.setUsevar(setvalueconstantmeta.isUseVars());
		return spSetValueConstant;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPSetValueConstant spSetValueConstant = (SPSetValueConstant) po;
		SetValueConstantMeta setvalueconstantmeta = (SetValueConstantMeta) stepMetaInterface;

		if (spSetValueConstant.getFieldName() != null) {
			String[] fieldNames = new String[spSetValueConstant.getFieldName().size()];
			String[] replaceValues = new String[spSetValueConstant.getFieldName().size()];
			String[] replaceMasks = new String[spSetValueConstant.getFieldName().size()];
			boolean[] setEmptyStrings = new boolean[spSetValueConstant.getFieldName().size()];
			for (int i = 0; i < spSetValueConstant.getFieldName().size(); i++) {
				SetValueConstantfieldNameDto setvalueconstantfieldnamedto = spSetValueConstant.getFieldName().get(i);
				fieldNames[i] = setvalueconstantfieldnamedto.getFieldName();
				replaceValues[i] = setvalueconstantfieldnamedto.getReplaceValue();
				replaceMasks[i] = setvalueconstantfieldnamedto.getReplaceMask();
				setEmptyStrings[i] = setvalueconstantfieldnamedto.isSetEmptyString();
			}
			setvalueconstantmeta.setFieldName(fieldNames);
			setvalueconstantmeta.setReplaceValue(replaceValues);
			setvalueconstantmeta.setReplaceMask(replaceMasks);
			setvalueconstantmeta.setEmptyString(setEmptyStrings);
		}
		setvalueconstantmeta.setUseVars(spSetValueConstant.isUsevar());

	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) {
		//字段没有变化
		
	}

}
