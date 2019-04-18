package com.ys.idatrix.quality.dto.step.steps.input;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.setvariable.SetVariableMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.quality.dto.step.parts.SetVariablefieldNameDto;
import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.quality.toolkit.domain.Relationship;
import com.ys.idatrix.quality.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - SetVariable. 转换 org.pentaho.di.trans.steps.setvariable.SetVariableMeta
 * 
 * @author XH
 * @since 2018-04-25
 */
@Component("SPSetVariable")
@Scope("prototype")
public class SPSetVariable implements StepParameter , StepDataRelationshipParser{

	List<SetVariablefieldNameDto> fieldName;
	boolean usingFormatting;

	/**
	 * @return the fieldName
	 */
	public List<SetVariablefieldNameDto> getFieldName() {
		return fieldName;
	}

	/**
	 * @param 设置
	 *            fieldName
	 */
	public void setFieldName(List<SetVariablefieldNameDto> fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * @return the usingFormatting
	 */
	public boolean isUsingFormatting() {
		return usingFormatting;
	}

	/**
	 * @param 设置
	 *            usingFormatting
	 */
	public void setUsingFormatting(boolean usingFormatting) {
		this.usingFormatting = usingFormatting;
	}

	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("fieldName", SetVariablefieldNameDto.class);
		return (SPSetVariable) JSONObject.toBean(jsonObj, SPSetVariable.class, classMap);
	}

	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPSetVariable spSetVariable = new SPSetVariable();
		SetVariableMeta setvariablemeta = (SetVariableMeta) stepMetaInterface;

		List<SetVariablefieldNameDto> fieldNameList = Lists.newArrayList();
		String[] fieldNames = setvariablemeta.getFieldName();
		String[] variableNames = setvariablemeta.getVariableName();
		int[] variableTypes = setvariablemeta.getVariableType();
		String[] defaultValues = setvariablemeta.getDefaultValue();
		for (int i = 0; fieldNames != null && i < fieldNames.length; i++) {
			SetVariablefieldNameDto setvariablefieldnamedto = new SetVariablefieldNameDto();
			setvariablefieldnamedto.setFieldName(fieldNames[i]);
			setvariablefieldnamedto.setVariableName(variableNames[i]);
			setvariablefieldnamedto.setVariableType(variableTypes[i]);
			setvariablefieldnamedto.setDefaultValue(defaultValues[i]);
			fieldNameList.add(setvariablefieldnamedto);
		}
		spSetVariable.setFieldName(fieldNameList);
		spSetVariable.setUsingFormatting(setvariablemeta.isUsingFormatting());
		return spSetVariable;
	}

	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPSetVariable spSetVariable = (SPSetVariable) po;
		SetVariableMeta setvariablemeta = (SetVariableMeta) stepMetaInterface;

		setvariablemeta.setUsingFormatting(spSetVariable.isUsingFormatting());
		if (spSetVariable.getFieldName() != null) {
			String[] fieldNames = new String[spSetVariable.getFieldName().size()];
			String[] variableNames = new String[spSetVariable.getFieldName().size()];
			int[] variableTypes = new int[spSetVariable.getFieldName().size()];
			String[] defaultValues = new String[spSetVariable.getFieldName().size()];
			for (int i = 0; i < spSetVariable.getFieldName().size(); i++) {
				SetVariablefieldNameDto setvariablefieldnamedto = spSetVariable.getFieldName().get(i);
				fieldNames[i] = setvariablefieldnamedto.getFieldName();
				variableNames[i] = setvariablefieldnamedto.getVariableName();
				variableTypes[i] = setvariablefieldnamedto.getVariableType();
				defaultValues[i] = setvariablefieldnamedto.getDefaultValue();
			}
			setvariablemeta.setFieldName(fieldNames);
			setvariablemeta.setVariableName(variableNames);
			setvariablemeta.setVariableType(variableTypes);
			setvariablemeta.setDefaultValue(defaultValues);
		}

	}

	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr)
			throws Exception {

		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SetVariableMeta setvariablemeta = (SetVariableMeta) stepMetaInterface;
		
		String from = "转换:" + transMeta.getName() + ",步骤:" + stepMeta.getName();

		String[] fieldNames = setvariablemeta.getFieldName();
		for (int i = 0; fieldNames != null && i < fieldNames.length; i++) {
			String fieldName = fieldNames[i];
			String dummyId = transMeta.getName() + "-" + stepMeta.getName() ;
			
			Relationship relationship = RelationshipUtil.buildDummyRelationship(from, dummyId, fieldName);
			sdr.addRelationship(relationship);
			sdr.addInputDataNode(relationship.getStartNode());
		}
		
		
	}

}
