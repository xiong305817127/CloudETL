package com.ys.idatrix.quality.steps.analysis.certificates;

import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import com.ys.idatrix.quality.steps.analysis.base.AnalysisBaseMeta;
import com.ys.idatrix.quality.steps.common.NodeTypeEnum;

@Step( id = "CertificatesAnalysis", image = "certificatesAnalysis.svg", name = "CertificatesAnalysis", description = "CertificatesAnalysis  Description", categoryDescription = "i18n:org.pentaho.di.trans.step:BaseStep.Category.Transform",documentationUrl = "",i18nPackageName = "" )
public class CertificatesAnalysisMeta extends AnalysisBaseMeta implements StepMetaInterface {

	public enum  STANDARD_CODE  { Card18 , Card15 ,Passport,HKmakao,Taiwan};
	
	/** 检查护照是否合法 */
	public static final String PASSPORT1 = "/^[a-zA-Z]{5,17}$/";
	public static final String PASSPORT2 = "/^[a-zA-Z0-9]{5,17}$/";
	/** 港澳通行证验证     */
	public static final String HKMAKAO = "/^[HMhm]{1}([0-9]{10}|[0-9]{8})$/";
	/** 台湾通行证验证     */
	public static final String TAIWAN1 = " /^[0-9]{8}$/";
	public static final String TAIWAN2 = "/^[0-9]{10}$/";

	@Override
	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		return new CertificatesAnalysis(stepMeta, stepDataInterface, copyNr, transMeta, trans) ;
	}

	@Override
	public void setDefault() {
		super.setDefault();
		nodeName= "yyyyMMdd身份证格式节点" ;
	}
	
	
	@Override
	public NodeTypeEnum getNodeType() {
		return NodeTypeEnum.CERTIFICATES;
	}

	

}
