package com.ys.idatrix.quality.steps.report;

import java.util.List;

import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;


@Step( id = "AnalysisReport", image = "analysisReport.svg", name = "AnalysisReport", description = "AnalysisReport  Description", categoryDescription = "i18n:org.pentaho.di.trans.step:BaseStep.Category.Output",documentationUrl = "",i18nPackageName = "" )
public  class AnalysisReportMeta extends BaseStepMeta implements StepMetaInterface {

	public String nodeName ; //需要可适应当天日期
	
	private boolean ignoreError = false;
	private Long commitSize = 1000L ;
	
	public AnalysisReportMeta() {
		super();
		setDefault();
	}

	@Override
	public void setDefault() {
		
		nodeName= "yyyyMMdd分析报表" ;
		
		ignoreError= false ;
		commitSize = 1000L ;
		
	}

	@Override
	public Object clone() {
		
		AnalysisReportMeta retval = (AnalysisReportMeta) super.clone();
		return retval;
	}
	
	@Override
	public void getFields(RowMetaInterface row, String name, RowMetaInterface[] info, StepMeta nextStep,
			VariableSpace space, Repository repository, IMetaStore metaStore) throws KettleStepException {
		// Default: nothing changes to rowMeta
	}

	@Override
	public String getXML() throws KettleException {
		StringBuilder retval = new StringBuilder(1024);

		retval.append("    ").append(XMLHandler.addTagValue("nodeName", nodeName));
		retval.append("    ").append(XMLHandler.addTagValue("commitSize", commitSize));
		
		retval.append("    ").append(XMLHandler.addTagValue("ignoreError", ignoreError));

		return retval.toString();
	}

	@Override
	public void loadXML(Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore) throws KettleXMLException {

		nodeName = XMLHandler.getTagValue(stepnode, "nodeName");
		commitSize = Long.parseLong( XMLHandler.getTagValue(stepnode, "commitSize") );
		
		ignoreError = "Y".equalsIgnoreCase( XMLHandler.getTagValue(stepnode, "ignoreError"));

	}

	public void readRep(Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases)
			throws KettleException {

		nodeName = rep.getStepAttributeString(id_step, "nodeName");
		commitSize =  rep.getStepAttributeInteger(id_step, "commitSize");
		
		ignoreError = rep.getStepAttributeBoolean(id_step, "ignoreError");

	}

	@Override
	public void saveRep(Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step)
			throws KettleException {

		rep.saveStepAttribute(id_transformation, id_step, "nodeName", nodeName);
		rep.saveStepAttribute(id_transformation, id_step, "commitSize", commitSize);
		
		rep.saveStepAttribute(id_transformation, id_step, "ignoreError", ignoreError);

	}

	@Override
	public StepDataInterface getStepData() {
		return new AnalysisReportData();
	}
	
	@Override
	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		return new AnalysisReport(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
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
	
}
