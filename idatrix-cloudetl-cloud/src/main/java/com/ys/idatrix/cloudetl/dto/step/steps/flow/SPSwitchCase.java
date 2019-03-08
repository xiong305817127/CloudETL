package com.ys.idatrix.cloudetl.dto.step.steps.flow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.switchcase.SwitchCaseMeta;
import org.pentaho.di.trans.steps.switchcase.SwitchCaseTarget;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.cloudetl.dto.step.parts.SwitchCaseTargetDto;
import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;

import net.sf.json.JSONObject;

/**
 * Step - SwitchCase. 转换 org.pentaho.di.trans.steps.switchcase.SwitchCaseMeta
 * 
 * @author XH
 * @since 2017-09-05
 */
@Component("SPSwitchCase")
@Scope("prototype")
public class SPSwitchCase implements StepParameter, StepDataRelationshipParser ,ResumeStepDataParser {

	String fieldname;
	boolean isContains;
	int caseValueType;
	String caseValueFormat;
	String caseValueDecimal;
	String caseValueGroup;
	String defaultTargetStepname;
	List<SwitchCaseTargetDto> caseTargets;

	/**
	 * @return the fieldname
	 */
	public String getFieldname() {
		return fieldname;
	}

	/**
	 * @param 设置
	 *            fieldname
	 */
	public void setFieldname(String fieldname) {
		this.fieldname = fieldname;
	}

	/**
	 * @return the isContains
	 */
	public boolean isContains() {
		return isContains;
	}

	/**
	 * @param 设置
	 *            isContains
	 */
	public void setContains(boolean isContains) {
		this.isContains = isContains;
	}

	/**
	 * @return the caseValueType
	 */
	public int getCaseValueType() {
		return caseValueType;
	}

	/**
	 * @param 设置
	 *            caseValueType
	 */
	public void setCaseValueType(int caseValueType) {
		this.caseValueType = caseValueType;
	}

	/**
	 * @return the caseValueFormat
	 */
	public String getCaseValueFormat() {
		return caseValueFormat;
	}

	/**
	 * @param 设置
	 *            caseValueFormat
	 */
	public void setCaseValueFormat(String caseValueFormat) {
		this.caseValueFormat = caseValueFormat;
	}

	/**
	 * @return the caseValueDecimal
	 */
	public String getCaseValueDecimal() {
		return caseValueDecimal;
	}

	/**
	 * @param 设置
	 *            caseValueDecimal
	 */
	public void setCaseValueDecimal(String caseValueDecimal) {
		this.caseValueDecimal = caseValueDecimal;
	}

	/**
	 * @return the caseValueGroup
	 */
	public String getCaseValueGroup() {
		return caseValueGroup;
	}

	/**
	 * @param 设置
	 *            caseValueGroup
	 */
	public void setCaseValueGroup(String caseValueGroup) {
		this.caseValueGroup = caseValueGroup;
	}

	/**
	 * @return the defaultTargetStepname
	 */
	public String getDefaultTargetStepname() {
		return defaultTargetStepname;
	}

	/**
	 * @param 设置
	 *            defaultTargetStepname
	 */
	public void setDefaultTargetStepname(String defaultTargetStepname) {
		this.defaultTargetStepname = defaultTargetStepname;
	}

	/**
	 * @return the caseTargets
	 */
	public List<SwitchCaseTargetDto> getCaseTargets() {
		return caseTargets;
	}

	/**
	 * @param 设置
	 *            caseTargets
	 */
	public void setCaseTargets(List<SwitchCaseTargetDto> caseTargets) {
		this.caseTargets = caseTargets;
	}

	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("caseTargets", SwitchCaseTargetDto.class);
		return (SPSwitchCase) JSONObject.toBean(jsonObj, SPSwitchCase.class, classMap);
	}

	@Override
	public Object encodeParameterObject(StepMeta stepMeta) {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPSwitchCase spSwitchCase = new SPSwitchCase();
		SwitchCaseMeta switchcasemeta = (SwitchCaseMeta) stepMetaInterface;

		spSwitchCase.setFieldname(switchcasemeta.getFieldname());
		spSwitchCase.setCaseValueType(switchcasemeta.getCaseValueType());
		spSwitchCase.setCaseValueFormat(switchcasemeta.getCaseValueFormat());
		spSwitchCase.setCaseValueDecimal(switchcasemeta.getCaseValueDecimal());
		spSwitchCase.setCaseValueGroup(switchcasemeta.getCaseValueGroup());
		spSwitchCase.setDefaultTargetStepname(switchcasemeta.getDefaultTargetStepname());
		spSwitchCase.setContains(switchcasemeta.isContains());

		spSwitchCase.setCaseTargets(
				transListToList(switchcasemeta.getCaseTargets(), new DtoTransData<SwitchCaseTargetDto>() {
					@Override
					public SwitchCaseTargetDto dealData(Object obj, int index) {
						SwitchCaseTarget sct = (SwitchCaseTarget) obj;
						SwitchCaseTargetDto sctd = new SwitchCaseTargetDto();
						sctd.setCaseValue(sct.caseValue);
						sctd.setCaseTargetStep(
								sct.caseTargetStep != null ? sct.caseTargetStep.getName() : sct.caseTargetStepname);
						return sctd;
					}
				}));

		return spSwitchCase;
	}

	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPSwitchCase spSwitchCase = (SPSwitchCase) po;
		SwitchCaseMeta switchcasemeta = (SwitchCaseMeta) stepMetaInterface;

		switchcasemeta.setFieldname(spSwitchCase.getFieldname());
		switchcasemeta.setCaseValueType(spSwitchCase.getCaseValueType());
		switchcasemeta.setCaseValueFormat(spSwitchCase.getCaseValueFormat());
		switchcasemeta.setCaseValueDecimal(spSwitchCase.getCaseValueDecimal());
		switchcasemeta.setCaseValueGroup(spSwitchCase.getCaseValueGroup());
		switchcasemeta.setDefaultTargetStepname(spSwitchCase.getDefaultTargetStepname());
		switchcasemeta.setContains(spSwitchCase.isContains());

		switchcasemeta
				.setCaseTargets(transListToList(spSwitchCase.getCaseTargets(), new DtoTransData<SwitchCaseTarget>() {
					@Override
					public SwitchCaseTarget dealData(Object obj, int index) {
						SwitchCaseTargetDto sctd = (SwitchCaseTargetDto) obj;
						SwitchCaseTarget sct = new SwitchCaseTarget();
						sct.caseValue = sctd.getCaseValue();
						sct.caseTargetStepname = sctd.getCaseTargetStep();
						return sct;
					}
				}));

		switchcasemeta.searchInfoAndTargetSteps(transMeta.getSteps());
	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) {
		//没有改变

	}
	
	@Override
	public boolean preRunHandle(TransMeta transMeta ,StepMeta stepMeta ,StepMetaInterface stepMetaInterface,StepDataInterface stepDataInterface , StepInterface stepInterface)  throws Exception {
		
		//该组件只有分发,没有复制模式
		stepMeta.setDistributes(true);
		
		return true ;
	}

	@Override
	public int stepType() {
		return 12;
	}

}
