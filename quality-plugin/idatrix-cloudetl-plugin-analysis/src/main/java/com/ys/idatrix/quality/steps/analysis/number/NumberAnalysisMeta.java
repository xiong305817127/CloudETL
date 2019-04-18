package com.ys.idatrix.quality.steps.analysis.number;

import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import com.ys.idatrix.quality.steps.analysis.base.AnalysisBaseMeta;
import com.ys.idatrix.quality.steps.common.NodeTypeEnum;

@Step( id = "NumberAnalysis", image = "numberAnalysis.svg", name = "NumberAnalysis", description = "NumberAnalysis  Description", categoryDescription = "i18n:org.pentaho.di.trans.step:BaseStep.Category.Transform",documentationUrl = "",i18nPackageName = "" )
public class NumberAnalysisMeta extends AnalysisBaseMeta implements StepMetaInterface {

	@Override
	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		return new NumberAnalysis(stepMeta, stepDataInterface, copyNr, transMeta, trans) ;
	}

	@Override
	public void setDefault() {
		super.setDefault();
		nodeName= "yyyyMMdd电话号码格式节点" ;
	}
	
	@Override
	public NodeTypeEnum getNodeType() {
		return NodeTypeEnum.NUMBER;
	}

	

}
