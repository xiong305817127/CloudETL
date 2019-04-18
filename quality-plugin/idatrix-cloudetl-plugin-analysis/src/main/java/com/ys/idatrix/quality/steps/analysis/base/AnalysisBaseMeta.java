package com.ys.idatrix.quality.steps.analysis.base;

import java.util.Arrays;
import java.util.List;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaBoolean;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

import com.ys.idatrix.quality.steps.common.NodeTypeEnum;
import com.ys.idatrix.quality.steps.common.NodeTypeEnum.AnalysisTypeFieldEnum;

public abstract class AnalysisBaseMeta extends BaseStepMeta implements StepMetaInterface {

	public static String EMPTY_VALUE_REFERENCE = "EMPTY_VALUE" ; //空值的参考值key
	public static String NON_MATCH_REFERENCE = "NON_MATCH" ; //不匹配的参考值key
	public static String ERROR_MATCH_REFERENCE = "ERROR_MATCH" ; //不匹配的参考值key

	public String nodeName ; //需要可适应当天日期
	public String[] fieldNames ; //域名
	public String[] fieldTypes ; //域类型
	
	public String  standardValue ; //标准值
	public String[]  referenceValues ; //参考值
	
	private boolean nullable = false; //是否可为空,为空时判断为正常,
	private boolean ignoreError = false;
	
	public AnalysisBaseMeta() {
		super();
		setDefault();
	}

	@Override
	public void setDefault() {
		
		nodeName= "yyyy-MM-dd" ;
		fieldNames = new String[0];
		fieldTypes = new String[0];
		standardValue= "" ;
		referenceValues = new String[0];
		ignoreError= false ;
		nullable = false ;
		
	}

	@Override
	public Object clone() {
		
		AnalysisBaseMeta retval = (AnalysisBaseMeta) super.clone();
		if (fieldNames != null) {
			retval.setFieldNames(Arrays.copyOf(fieldNames,fieldNames.length));
			retval.setFieldTypes(Arrays.copyOf(fieldTypes,fieldNames.length));
		}
		
		if (referenceValues != null) {
			retval.setReferenceValues(Arrays.copyOf(referenceValues,referenceValues.length));
		}
		return retval;
		

	}
	
	@Override
	public void getFields(RowMetaInterface row, String name, RowMetaInterface[] info, StepMeta nextStep,
			VariableSpace space, Repository repository, IMetaStore metaStore) throws KettleStepException {

			ValueMetaInterface nn = new ValueMetaString( space.getVariable( nodeName+".NodeName" ,  AnalysisTypeFieldEnum.nodeName.toString() ));
			nn.setOrigin(name);
			row.addValueMeta(nn);
			
			ValueMetaInterface nt = new ValueMetaString( space.getVariable( nodeName+".NodeType" ,  AnalysisTypeFieldEnum.nodeType.toString()));
			nt.setOrigin(name);
			row.addValueMeta(nt);
			
			ValueMetaInterface fn = new ValueMetaString(  space.getVariable( nodeName+".FieldName" ,  AnalysisTypeFieldEnum.fieldName.toString() ) );
			fn.setLength(100, -1);
			fn.setOrigin(name);
			row.addValueMeta(fn);

			ValueMetaInterface v = new ValueMetaString(  space.getVariable( nodeName+".Value" ,  AnalysisTypeFieldEnum.value.toString() ) );
			v.setLength(100, -1);
			v.setOrigin(name);
			row.addValueMeta(v);
			
			ValueMetaInterface rv = new ValueMetaString(  space.getVariable( nodeName+".ReferenceValue" ,  AnalysisTypeFieldEnum.referenceValue.toString() ) );
			rv.setLength(255, -1);
			rv.setOrigin(name);
			row.addValueMeta(rv);
			
			ValueMetaInterface r = new ValueMetaBoolean( space.getVariable( nodeName+".Result" ,  AnalysisTypeFieldEnum.result.toString() ) );
			r.setOrigin(name);
			row.addValueMeta(r);

	}

	@Override
	public String getXML() throws KettleException {
		StringBuilder retval = new StringBuilder(1024);

		retval.append("    ").append(XMLHandler.addTagValue("nodeName", nodeName));
		retval.append("    ").append(XMLHandler.addTagValue("ignoreError", ignoreError));
		retval.append("    ").append(XMLHandler.addTagValue("nullable", nullable));

		retval.append("    <fields>").append(Const.CR);
		for (int i = 0; i < fieldNames.length; i++) {
			retval.append("      ").append(XMLHandler.addTagValue("fieldName", fieldNames[i]));
			if(fieldTypes.length > i) {
				retval.append("      ").append(XMLHandler.addTagValue("fieldType", fieldTypes[i]));
			}else {
				retval.append("      ").append(XMLHandler.addTagValue("fieldType", "" ));
			}
		}
		retval.append("    </fields>").append(Const.CR);

		retval.append("    ").append(XMLHandler.addTagValue("standardValue", standardValue));
		
		retval.append("    <referenceValues>").append(Const.CR);
		for (int i = 0; i < referenceValues.length; i++) {
			retval.append("      ").append(XMLHandler.addTagValue("referenceValue", referenceValues[i]));
		}
		retval.append("    </referenceValues>").append(Const.CR);
		
		
		return retval.toString();
	}

	@Override
	public void loadXML(Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore) throws KettleXMLException {

		nodeName = XMLHandler.getTagValue(stepnode, "nodeName");
		ignoreError = "Y".equalsIgnoreCase( XMLHandler.getTagValue(stepnode, "ignoreError"));
		nullable = "Y".equalsIgnoreCase( XMLHandler.getTagValue(stepnode, "nullable"));

		Node fieldnode = XMLHandler.getSubNode(stepnode, "fields");
		int nrfiles = XMLHandler.countNodes(fieldnode, "fieldName");
		fieldNames = new String[nrfiles];
		fieldTypes = new String[nrfiles];
		for (int i = 0; i < nrfiles; i++) {
			Node fieldName = XMLHandler.getSubNodeByNr(fieldnode, "fieldName", i);
			Node fieldType = XMLHandler.getSubNodeByNr(fieldnode, "fieldType", i);
			fieldNames[i] =XMLHandler.getNodeValue(fieldName);
			fieldTypes[i] =XMLHandler.getNodeValue(fieldType);
		}
		
		standardValue = XMLHandler.getTagValue(stepnode, "standardValue");
		
		Node referencenode = XMLHandler.getSubNode(stepnode, "referenceValues");
		int nrReference = XMLHandler.countNodes(referencenode, "referenceValue");
		referenceValues = new String[nrReference];
		for (int i = 0; i < nrReference; i++) {
			Node referenceValue = XMLHandler.getSubNodeByNr(referencenode, "referenceValue", i);
			referenceValues[i] =XMLHandler.getNodeValue(referenceValue);
		}

	}

	public void readRep(Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases)
			throws KettleException {

		nodeName = rep.getStepAttributeString(id_step, "nodeName");
		ignoreError = rep.getStepAttributeBoolean(id_step, "ignoreError");
		nullable = rep.getStepAttributeBoolean(id_step, "nullable");

		int nrfield = rep.countNrStepAttributes(id_step, "fieldName");
		fieldNames =  new String[nrfield];
		fieldTypes =  new String[nrfield];
		for (int i = 0; i < nrfield; i++) {
			fieldNames[i] =rep.getStepAttributeString(id_step, i, "fieldName");
			fieldTypes[i] =rep.getStepAttributeString(id_step, i, "fieldType");
		}
		
		standardValue = rep.getStepAttributeString(id_step, "standardValue");
		
		int nrreference = rep.countNrStepAttributes(id_step, "referenceValue");
		referenceValues =  new String[nrreference];
		for (int i = 0; i < nrreference; i++) {
			referenceValues[i] =rep.getStepAttributeString(id_step, i, "referenceValue");
		}
		
	}

	@Override
	public void saveRep(Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step)
			throws KettleException {

		rep.saveStepAttribute(id_transformation, id_step, "nodeName", nodeName);
		rep.saveStepAttribute(id_transformation, id_step, "ignoreError", ignoreError);
		rep.saveStepAttribute(id_transformation, id_step, "nullable", nullable);

		for (int i = 0; i < fieldNames.length; i++) {
			rep.saveStepAttribute(id_transformation, id_step, i, "fieldName", Const.NVL(fieldNames[i], ""));
			if(fieldTypes.length > i) {
				rep.saveStepAttribute(id_transformation, id_step, i, "fieldType", Const.NVL(fieldTypes[i], ""));
			}else {
				rep.saveStepAttribute(id_transformation, id_step, i, "fieldType", "" );
			}
			
		}
		
		rep.saveStepAttribute(id_transformation, id_step, "standardValue", standardValue);
		
		for (int i = 0; i < referenceValues.length; i++) {
			rep.saveStepAttribute(id_transformation, id_step, i, "referenceValue", Const.NVL(referenceValues[i], ""));
		}
		
	}

	@Override
	public StepDataInterface getStepData() {
		return new AnalysisBaseData();
	}
	
	public abstract NodeTypeEnum getNodeType() ;

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public String[] getFieldNames() {
		return fieldNames;
	}

	public String[] getFieldTypes() {
		return fieldTypes;
	}

	public void setFieldTypes(String[] fieldTypes) {
		this.fieldTypes = fieldTypes;
	}

	public void setFieldNames(String[] fieldNames) {
		this.fieldNames = fieldNames;
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

	public boolean isIgnoreError() {
		return ignoreError;
	}

	public void setIgnoreError(boolean ignoreError) {
		this.ignoreError = ignoreError;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

}
