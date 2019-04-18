package com.ys.idatrix.quality.dto.step.steps.analysis;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;

/**
 * Step - NumberAnalysis(电话号码校验). 转换  com.ys.idatrix.quality.analysis.steps.number.NumberAnalysisMeta
 * 
 * @author XH
 * @since 2017-09-05
 */
@Component("SPNumberAnalysis")
@Scope("prototype")
public class SPNumberAnalysis extends SPBaseAnalysis implements StepParameter, StepDataRelationshipParser ,ResumeStepDataParser{
	
}
