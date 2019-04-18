package com.ys.idatrix.quality.dto.step.steps.script;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.formula.FormulaMeta;
import org.pentaho.di.trans.steps.formula.FormulaMetaFunction;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.quality.dto.step.parts.FormulaMetaFunctionDto;
import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.quality.toolkit.domain.Relationship;
import com.ys.idatrix.quality.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - Formula. 转换 org.pentaho.di.trans.steps.formula.FormulaMeta
 * 
 * @author XH
 * @since 2018-08-22
 */
@Component("SPFormula")
@Scope("prototype")
public class SPFormula implements StepParameter, StepDataRelationshipParser, ResumeStepDataParser {

	List<FormulaMetaFunctionDto> formulas;

	public List<FormulaMetaFunctionDto> getFormulas() {
		return formulas;
	}

	public void setFormulas(List<FormulaMetaFunctionDto> formulas) {
		this.formulas = formulas;
	}

	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("formulas", FormulaMetaFunctionDto.class);
		return (SPFormula) JSONObject.toBean(jsonObj, SPFormula.class, classMap);
	}

	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPFormula spFormula = new SPFormula();
		FormulaMeta formulameta = (FormulaMeta) stepMetaInterface;

		FormulaMetaFunction[] flms = formulameta.getFormula();
		if (flms != null && flms.length > 0) {
			List<FormulaMetaFunctionDto> formulasList = Lists.newArrayList();
			for (FormulaMetaFunction flm : flms) {
				FormulaMetaFunctionDto fld = new FormulaMetaFunctionDto();
				fld.setFieldName(flm.getFieldName());
				fld.setFormula(flm.getFormula());
				fld.setReplaceField(flm.getReplaceField());
				fld.setValueLength(flm.getValueLength());
				fld.setValuePrecision(flm.getValuePrecision());
				fld.setValueType(flm.getValueType());
				formulasList.add(fld);
			}
			spFormula.setFormulas(formulasList);
		}
		return spFormula;
	}

	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases, TransMeta transMeta)
			throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPFormula spFormula = (SPFormula) po;
		FormulaMeta formulameta = (FormulaMeta) stepMetaInterface;

		List<FormulaMetaFunctionDto> flds = spFormula.getFormulas();
		if (flds != null && flds.size() > 0) {
			FormulaMetaFunction[] flms = new FormulaMetaFunction[flds.size()];
			for (int i = 0; i < flds.size(); i++) {
				FormulaMetaFunctionDto fld = flds.get(i);
				String fieldName = fld.getFieldName();
				String formula = fld.getFormula();
				String replaceField = fld.getReplaceField();
				int valueLength = fld.getValueLength();
				int valuePrecision = fld.getValuePrecision();
				int valueType = fld.getValueType();

				FormulaMetaFunction flm = new FormulaMetaFunction(fieldName, formula, valueType, valueLength,
						valuePrecision, replaceField);
				flms[i] = flm;
			}
			formulameta.setFormula(flms);
		}
	}

	@Override
	public int stepType() {
		return 4;
	}

	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr)
			throws Exception {

		String from = "转换:" + transMeta.getName() + ",步骤:" + stepMeta.getName();
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();

		FormulaMeta formulameta = (FormulaMeta) stepMetaInterface;
		FormulaMetaFunction[] flms = formulameta.getFormula();
		if (flms != null) {
			Arrays.asList(flms).stream().forEach(temp1 -> {
				try {
					if(Utils.isEmpty(temp1.getReplaceField())) {
						//新增域
						String fieldName = temp1.getFieldName();
						//TODO 解析脚本 暂时忽略
						//String scripe = temp1.getFormula() ;
						
						String dummyId = transMeta.getName()+"-"+stepMeta.getName()+"-"+fieldName ;
						Relationship relationship = RelationshipUtil.buildDummyRelationship(from, dummyId, fieldName);
						sdr.addRelationship(relationship);
						sdr.addInputDataNode(relationship.getStartNode());
						
					}
					
				} catch (Exception e) {
					relationshiplogger.error("",e);
				}

			});
		}

	}

}
