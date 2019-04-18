package com.ys.idatrix.quality.steps.analysis.character;

import java.util.List;

import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

import com.ys.idatrix.quality.steps.analysis.base.AnalysisBaseMeta;
import com.ys.idatrix.quality.steps.common.NodeTypeEnum;

@Step( id = "CharacterAnalysis", image = "characterAnalysis.svg", name = "CharacterAnalysis", description = "CharacterAnalysis  Description", categoryDescription = "i18n:org.pentaho.di.trans.step:BaseStep.Category.Transform",documentationUrl = "",i18nPackageName = "" )
public class CharacterAnalysisMeta extends AnalysisBaseMeta implements StepMetaInterface {

	
	private String standardKey ;
	private boolean ignoreCase = false;
	
	@Override
	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		return new CharacterAnalysis(stepMeta, stepDataInterface, copyNr, transMeta, trans) ;
	}
	
	@Override
	public StepDataInterface getStepData() {
		return new CharacterAnalysisData();
	}
	
	@Override
	public void setDefault() {
		super.setDefault();
		nodeName= "yyyyMMdd标准值格式节点" ;
	}
	

	@Override
	public NodeTypeEnum getNodeType() {
		return NodeTypeEnum.CHARACTER;
	}

	@Override
	public String getXML() throws KettleException {
		
		StringBuilder retval = new StringBuilder(1024);
		retval.append(super.getXML());
		
		retval.append("    ").append(XMLHandler.addTagValue("standardKey", standardKey));
		retval.append("    ").append(XMLHandler.addTagValue("ignoreCase", ignoreCase));
		
		return retval.toString() ;
	}
	@Override
	public void loadXML(Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore) throws KettleXMLException {
		
		super.loadXML(stepnode, databases, metaStore);
		standardKey = XMLHandler.getTagValue(stepnode, "standardKey");
		ignoreCase = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "ignoreCase"));
	}
	@Override
	public void readRep(Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases)
			throws KettleException {
		
		super.readRep(rep, metaStore, id_step, databases);
		standardKey = rep.getStepAttributeString(id_step, "standardKey");
		ignoreCase = rep.getStepAttributeBoolean(id_step, "ignoreCase");
	}
	
	@Override
	public void saveRep(Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step)
			throws KettleException {

		super.saveRep(rep, metaStore, id_transformation, id_step);
		rep.saveStepAttribute(id_transformation, id_step, "standardKey", standardKey);
		rep.saveStepAttribute(id_transformation, id_step, "ignoreCase", ignoreCase);
	}

	@Override
	public void getFields(RowMetaInterface row, String name, RowMetaInterface[] info, StepMeta nextStep,
			VariableSpace space, Repository repository, IMetaStore metaStore) throws KettleStepException {
		super.getFields(row, name, info, nextStep, space, repository, metaStore);
		
		ValueMetaInterface v = new ValueMetaString( "dictName" );
		v.setLength(100, -1);
		v.setOrigin(name);
		row.addValueMeta(v);
		
	}
	
	
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

}
