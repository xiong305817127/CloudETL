package com.ys.idatrix.quality.dto.step.steps.report;

import java.util.List;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;

import net.sf.json.JSONObject;

/**
 * Step - AnalysisReport( 检验报告器 ). 转换  com.ys.idatrix.quality.analysis.steps.report.AnalysisReportMeta
 * 
 * @author XH
 * @since 2017-09-05
 */
@Component("SPAnalysisReport")
@Scope("prototype")
public class SPAnalysisReport implements StepParameter, StepDataRelationshipParser ,ResumeStepDataParser{


	public String nodeName ; //需要可适应当天日期
	
	//输入域名称
	public String inputNodeName ; 
	public String inputNodeType ; 
	public String inputFieldName ; 
	public String inputFieldValue ; 
	public String inputReferenceValue ; 
	public String inputResult ; 
	
	private boolean ignoreError = false;
	
	private Long commitSize = 1000L ;
	
	
	
	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String getInputNodeName() {
		return inputNodeName;
	}

	public void setInputNodeName(String inputNodeName) {
		this.inputNodeName = inputNodeName;
	}

	public String getInputNodeType() {
		return inputNodeType;
	}

	public void setInputNodeType(String inputNodeType) {
		this.inputNodeType = inputNodeType;
	}

	public String getInputFieldName() {
		return inputFieldName;
	}

	public void setInputFieldName(String inputFieldName) {
		this.inputFieldName = inputFieldName;
	}

	public String getInputFieldValue() {
		return inputFieldValue;
	}

	public void setInputFieldValue(String inputFieldValue) {
		this.inputFieldValue = inputFieldValue;
	}

	public String getInputReferenceValue() {
		return inputReferenceValue;
	}

	public void setInputReferenceValue(String inputReferenceValue) {
		this.inputReferenceValue = inputReferenceValue;
	}

	public String getInputResult() {
		return inputResult;
	}

	public void setInputResult(String inputResult) {
		this.inputResult = inputResult;
	}

	public boolean isIgnoreError() {
		return ignoreError;
	}

	public void setIgnoreError(boolean ignoreError) {
		this.ignoreError = ignoreError;
	}

	public Long getCommitSize() {
		return commitSize;
	}

	public void setCommitSize(Long commitSize) {
		this.commitSize = commitSize;
	}



	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		return (SPAnalysisReport) JSONObject.toBean(jsonObj, SPAnalysisReport.class);
	}

	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPAnalysisReport spAnalysisReport = new SPAnalysisReport();
		
		setToObject(stepMetaInterface, spAnalysisReport);
		
		return spAnalysisReport;
	}

	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases, TransMeta transMeta)
			throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface() ;
		SPAnalysisReport spAnalysisReport= (SPAnalysisReport)po;
		
		setToObject(spAnalysisReport, stepMetaInterface);
		
	}
	
	@Override
	public int stepType() {
		return 6;
	}

	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr)
			throws Exception {
		// TODO Auto-generated method stub
		
	}
	
}
