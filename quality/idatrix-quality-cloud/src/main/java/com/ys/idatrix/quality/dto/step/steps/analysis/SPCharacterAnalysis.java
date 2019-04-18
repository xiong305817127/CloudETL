package com.ys.idatrix.quality.dto.step.steps.analysis;

import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;

import net.sf.json.JSONObject;

/**
 * Step - NumberAnalysis(电话号码校验). 转换  com.ys.idatrix.quality.analysis.steps.number.NumberAnalysisMeta
 * 
 * @author XH
 * @since 2017-09-05
 */
@Component("SPCharacterAnalysis")
@Scope("prototype")
public class SPCharacterAnalysis extends SPBaseAnalysis implements StepParameter, StepDataRelationshipParser ,ResumeStepDataParser{
	
	private String standardKey ;
	private boolean ignoreCase = false;
	
	public String getStandardKey() {
		return standardKey;
	}
	public void setStandardKey(String standardKey) {
		this.standardKey = standardKey;
	}
	public boolean isIgnoreCase() {
		return ignoreCase;
	}
	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}
	
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		return (SPCharacterAnalysis) JSONObject.toBean(jsonObj, SPCharacterAnalysis.class);
	}
	
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPCharacterAnalysis spCharacterAnalysis = new SPCharacterAnalysis();
		
		setToObject(stepMetaInterface, spCharacterAnalysis);
		
		return spCharacterAnalysis;
	}
	
	
}
