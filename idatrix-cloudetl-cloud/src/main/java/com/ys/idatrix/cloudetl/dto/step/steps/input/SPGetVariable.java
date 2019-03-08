package com.ys.idatrix.cloudetl.dto.step.steps.input;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.row.value.ValueMetaBase;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.getvariable.GetVariableMeta;
import org.pentaho.di.trans.steps.getvariable.GetVariableMeta.FieldDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.cloudetl.dto.step.parts.GetVariableFieldDefinitionDto;
import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.cloudetl.toolkit.domain.DataNode;
import com.ys.idatrix.cloudetl.toolkit.domain.Relationship;
import com.ys.idatrix.cloudetl.toolkit.utils.DataNodeUtil;
import com.ys.idatrix.cloudetl.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - GetVariable(获取变量). 转换 org.pentaho.di.trans.steps.getvariable.GetVariableMeta
 * 
 * @author XH
 * @since 2018-04-10
 */
@Component("SPGetVariable")
@Scope("prototype")
public class SPGetVariable implements StepParameter, StepDataRelationshipParser {

	List<GetVariableFieldDefinitionDto> fieldDefinitions;

	/**
	 * @return the fieldDefinitions
	 */
	public List<GetVariableFieldDefinitionDto> getFieldDefinitions() {
		return fieldDefinitions;
	}

	/**
	 * @param 设置
	 *            fieldDefinitions
	 */
	public void setFieldDefinitions(List<GetVariableFieldDefinitionDto> fieldDefinitions) {
		this.fieldDefinitions = fieldDefinitions;
	}

	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("fieldDefinitions", GetVariableFieldDefinitionDto.class);
		return (SPGetVariable) JSONObject.toBean(jsonObj, SPGetVariable.class, classMap);
	}

	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPGetVariable spGetVariable = new SPGetVariable();
		GetVariableMeta getvariablemeta = (GetVariableMeta) stepMetaInterface;

		FieldDefinition[] fieldDefinitionsArray = getvariablemeta.getFieldDefinitions();
		if (fieldDefinitionsArray != null) {
			List<GetVariableFieldDefinitionDto> fieldDefinitionsList = Arrays.asList(fieldDefinitionsArray).stream()
					.map(temp1 -> {
						GetVariableFieldDefinitionDto temp2 = new GetVariableFieldDefinitionDto();
						temp2.setFieldname(temp1.getFieldName());
						temp2.setVariablestring(temp1.getVariableString());
						temp2.setFieldtype(temp1.getFieldType());
						temp2.setFieldformat(temp1.getFieldFormat());
						temp2.setCurrency(temp1.getCurrency());
						temp2.setDecimal(temp1.getDecimal());
						temp2.setGroup(temp1.getGroup());
						temp2.setLength(temp1.getFieldLength());
						temp2.setPrecision(temp1.getFieldPrecision());
						temp2.setTrimType(ValueMetaBase.getTrimTypeCode(temp1.getTrimType()));
						return temp2;
					}).collect(Collectors.toList());
			spGetVariable.setFieldDefinitions(fieldDefinitionsList);
		}
		return spGetVariable;
	}

	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPGetVariable spGetVariable = (SPGetVariable) po;
		GetVariableMeta getvariablemeta = (GetVariableMeta) stepMetaInterface;

		if (spGetVariable.getFieldDefinitions() != null) {
			List<FieldDefinition> fieldDefinitionsList = spGetVariable.getFieldDefinitions().stream().map(temp1 -> {
				FieldDefinition temp2 = new FieldDefinition();
				temp2.setFieldName(temp1.getFieldname());
				temp2.setVariableString(temp1.getVariablestring());
				temp2.setFieldType(temp1.getFieldtype());
				temp2.setFieldFormat(temp1.getFieldformat());
				temp2.setCurrency(temp1.getCurrency());
				temp2.setDecimal(temp1.getDecimal());
				temp2.setGroup(temp1.getGroup());
				temp2.setFieldLength(temp1.getLength());
				temp2.setFieldPrecision(temp1.getPrecision());
				temp2.setTrimType(ValueMetaBase.getTrimTypeByCode(temp1.getTrimType()));
				return temp2;
			}).collect(Collectors.toList());
			getvariablemeta.setFieldDefinitions(
					fieldDefinitionsList.toArray(new FieldDefinition[spGetVariable.getFieldDefinitions().size()]));
		}
	}

	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr)
			throws Exception {

		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		GetVariableMeta getvariablemeta = (GetVariableMeta) stepMetaInterface;

		if (getvariablemeta.getFieldDefinitions() == null || getvariablemeta.getFieldDefinitions().length ==0) {
			return;
		}
		
		Map<String, DataNode> itemDataNodes = DataNodeUtil.interfaceNodeParse("Http", "dataInterface-getVariable", "json", stepMeta.getName(), sdr.getOutputStream().values());
		sdr.getInputDataNodes().addAll(itemDataNodes.values());

		// 增加 系统节点 和 流节点的关系
		String from = "转换:" + transMeta.getName() + ",步骤:" + stepMeta.getName();
		List<Relationship> relationships = RelationshipUtil.inputStepRelationship(itemDataNodes, null, sdr.getOutputStream(), stepMeta.getName(), from);
		sdr.getDataRelationship().addAll(relationships);
	}

}
