package com.ys.idatrix.quality.dto.step.steps.analysis;

import java.util.List;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;

import net.sf.json.JSONObject;

/**
 * 转换  com.ys.idatrix.quality.analysis.steps.number.AnalysisBaseMeta
 * 
 * @author XH
 * @since 2017-09-05
 */
public class SPBaseAnalysis implements StepParameter, StepDataRelationshipParser ,ResumeStepDataParser{

	
	public String nodeName ; //需要可适应当天日期
	public String[] fieldNames ; //域名
	public String[] fieldTypes ; //域类型
	
	public String  standardValue ; //标准值
	public String[]  referenceValues ; //参考值
	
	private boolean nullable = false; //是否可为空,为空时判断为正常,
	private boolean ignoreError = false;
	

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String[] getFieldNames() {
		return fieldNames;
	}

	public void setFieldNames(String[] fieldNames) {
		this.fieldNames = fieldNames;
	}

	public String[] getFieldTypes() {
		return fieldTypes;
	}

	public void setFieldTypes(String[] fieldTypes) {
		this.fieldTypes = fieldTypes;
	}

	public String getStandardValue() {
		return standardValue;
	}

	public void setStandardValue(String standardValue) {
		this.standardValue = standardValue;
	}

	public String[] getReferenceValues() {
		return referenceValues;
	}

	public void setReferenceValues(String[] referenceValues) {
		this.referenceValues = referenceValues;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public boolean isIgnoreError() {
		return ignoreError;
	}

	public void setIgnoreError(boolean ignoreError) {
		this.ignoreError = ignoreError;
	}

	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		return (SPBaseAnalysis) JSONObject.toBean(jsonObj, SPBaseAnalysis.class);
	}

	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPBaseAnalysis spBaseAnalysis = new SPBaseAnalysis();
		
		setToObject(stepMetaInterface, spBaseAnalysis);
		
		return spBaseAnalysis;
	}

	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases, TransMeta transMeta)
			throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface() ;
		SPBaseAnalysis spBaseAnalysis= (SPBaseAnalysis)po;
		
		setToObject(spBaseAnalysis, stepMetaInterface);
	}

	@Override
	public int stepType() {
		return 4;
	}

	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr)
			throws Exception {

	}

}
