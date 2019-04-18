package com.ys.idatrix.quality.dto.step.steps.input;

import java.util.List;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;

import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.quality.toolkit.domain.DataNode;
import com.ys.idatrix.quality.toolkit.domain.Relationship;
import com.ys.idatrix.quality.toolkit.utils.DataNodeUtil;
import com.ys.idatrix.quality.toolkit.utils.RelationshipUtil;
import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.recovery.trans.ResumeStepDataParser;
import net.sf.json.JSONObject;

/**
 * Step - XMLInputStream. 转换
 * org.pentaho.di.trans.steps.xmlinputstream.XMLInputStreamMeta
 * 
 * @author XH
 * @since 2018-11-09
 */
@Component("SPXMLInputStream")
@Scope("prototype")
public class SPXMLInputStream implements StepParameter, StepDataRelationshipParser, ResumeStepDataParser {

	private boolean sourceFromInput;
	private String sourceFieldName;
	private String filename;
	private boolean addResultFile;
	private String nrRowsToSkip;
	private String rowLimit;
	private String defaultStringLen;
	private String encoding;
	private boolean enableNamespaces;
	private boolean enableTrim;
	private boolean includeFilenameField;
	private String filenameField;
	private boolean includeRowNumberField;
	private String rowNumberField;
	private boolean includeXmlDataTypeNumericField;
	private String xmlDataTypeNumericField;
	private boolean includeXmlDataTypeDescriptionField;
	private String xmlDataTypeDescriptionField;
	private boolean includeXmlLocationLineField;
	private String xmlLocationLineField;
	private boolean includeXmlLocationColumnField;
	private String xmlLocationColumnField;
	private boolean includeXmlElementIDField;
	private String xmlElementIDField;
	private boolean includeXmlParentElementIDField;
	private String xmlParentElementIDField;
	private boolean includeXmlElementLevelField;
	private String xmlElementLevelField;
	private boolean includeXmlPathField;
	private String xmlPathField;
	private boolean includeXmlParentPathField;
	private String xmlParentPathField;
	private boolean includeXmlDataNameField;
	private String xmlDataNameField;
	private boolean includeXmlDataValueField;
	private String xmlDataValueField;

	public boolean isSourceFromInput() {
		return sourceFromInput;
	}

	public void setSourceFromInput(boolean sourceFromInput) {
		this.sourceFromInput = sourceFromInput;
	}

	public String getSourceFieldName() {
		return sourceFieldName;
	}

	public void setSourceFieldName(String sourceFieldName) {
		this.sourceFieldName = sourceFieldName;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public boolean isAddResultFile() {
		return addResultFile;
	}

	public void setAddResultFile(boolean addResultFile) {
		this.addResultFile = addResultFile;
	}

	public String getNrRowsToSkip() {
		return nrRowsToSkip;
	}

	public void setNrRowsToSkip(String nrRowsToSkip) {
		this.nrRowsToSkip = nrRowsToSkip;
	}

	public String getRowLimit() {
		return rowLimit;
	}

	public void setRowLimit(String rowLimit) {
		this.rowLimit = rowLimit;
	}

	public String getDefaultStringLen() {
		return defaultStringLen;
	}

	public void setDefaultStringLen(String defaultStringLen) {
		this.defaultStringLen = defaultStringLen;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public boolean isEnableNamespaces() {
		return enableNamespaces;
	}

	public void setEnableNamespaces(boolean enableNamespaces) {
		this.enableNamespaces = enableNamespaces;
	}

	public boolean isEnableTrim() {
		return enableTrim;
	}

	public void setEnableTrim(boolean enableTrim) {
		this.enableTrim = enableTrim;
	}

	public boolean isIncludeFilenameField() {
		return includeFilenameField;
	}

	public void setIncludeFilenameField(boolean includeFilenameField) {
		this.includeFilenameField = includeFilenameField;
	}

	public String getFilenameField() {
		return filenameField;
	}

	public void setFilenameField(String filenameField) {
		this.filenameField = filenameField;
	}

	public boolean isIncludeRowNumberField() {
		return includeRowNumberField;
	}

	public void setIncludeRowNumberField(boolean includeRowNumberField) {
		this.includeRowNumberField = includeRowNumberField;
	}

	public String getRowNumberField() {
		return rowNumberField;
	}

	public void setRowNumberField(String rowNumberField) {
		this.rowNumberField = rowNumberField;
	}

	public boolean isIncludeXmlDataTypeNumericField() {
		return includeXmlDataTypeNumericField;
	}

	public void setIncludeXmlDataTypeNumericField(boolean includeXmlDataTypeNumericField) {
		this.includeXmlDataTypeNumericField = includeXmlDataTypeNumericField;
	}

	public String getXmlDataTypeNumericField() {
		return xmlDataTypeNumericField;
	}

	public void setXmlDataTypeNumericField(String xmlDataTypeNumericField) {
		this.xmlDataTypeNumericField = xmlDataTypeNumericField;
	}

	public boolean isIncludeXmlDataTypeDescriptionField() {
		return includeXmlDataTypeDescriptionField;
	}

	public void setIncludeXmlDataTypeDescriptionField(boolean includeXmlDataTypeDescriptionField) {
		this.includeXmlDataTypeDescriptionField = includeXmlDataTypeDescriptionField;
	}

	public String getXmlDataTypeDescriptionField() {
		return xmlDataTypeDescriptionField;
	}

	public void setXmlDataTypeDescriptionField(String xmlDataTypeDescriptionField) {
		this.xmlDataTypeDescriptionField = xmlDataTypeDescriptionField;
	}

	public boolean isIncludeXmlLocationLineField() {
		return includeXmlLocationLineField;
	}

	public void setIncludeXmlLocationLineField(boolean includeXmlLocationLineField) {
		this.includeXmlLocationLineField = includeXmlLocationLineField;
	}

	public String getXmlLocationLineField() {
		return xmlLocationLineField;
	}

	public void setXmlLocationLineField(String xmlLocationLineField) {
		this.xmlLocationLineField = xmlLocationLineField;
	}

	public boolean isIncludeXmlLocationColumnField() {
		return includeXmlLocationColumnField;
	}

	public void setIncludeXmlLocationColumnField(boolean includeXmlLocationColumnField) {
		this.includeXmlLocationColumnField = includeXmlLocationColumnField;
	}

	public String getXmlLocationColumnField() {
		return xmlLocationColumnField;
	}

	public void setXmlLocationColumnField(String xmlLocationColumnField) {
		this.xmlLocationColumnField = xmlLocationColumnField;
	}

	public boolean isIncludeXmlElementIDField() {
		return includeXmlElementIDField;
	}

	public void setIncludeXmlElementIDField(boolean includeXmlElementIDField) {
		this.includeXmlElementIDField = includeXmlElementIDField;
	}

	public String getXmlElementIDField() {
		return xmlElementIDField;
	}

	public void setXmlElementIDField(String xmlElementIDField) {
		this.xmlElementIDField = xmlElementIDField;
	}

	public boolean isIncludeXmlParentElementIDField() {
		return includeXmlParentElementIDField;
	}

	public void setIncludeXmlParentElementIDField(boolean includeXmlParentElementIDField) {
		this.includeXmlParentElementIDField = includeXmlParentElementIDField;
	}

	public String getXmlParentElementIDField() {
		return xmlParentElementIDField;
	}

	public void setXmlParentElementIDField(String xmlParentElementIDField) {
		this.xmlParentElementIDField = xmlParentElementIDField;
	}

	public boolean isIncludeXmlElementLevelField() {
		return includeXmlElementLevelField;
	}

	public void setIncludeXmlElementLevelField(boolean includeXmlElementLevelField) {
		this.includeXmlElementLevelField = includeXmlElementLevelField;
	}

	public String getXmlElementLevelField() {
		return xmlElementLevelField;
	}

	public void setXmlElementLevelField(String xmlElementLevelField) {
		this.xmlElementLevelField = xmlElementLevelField;
	}

	public boolean isIncludeXmlPathField() {
		return includeXmlPathField;
	}

	public void setIncludeXmlPathField(boolean includeXmlPathField) {
		this.includeXmlPathField = includeXmlPathField;
	}

	public String getXmlPathField() {
		return xmlPathField;
	}

	public void setXmlPathField(String xmlPathField) {
		this.xmlPathField = xmlPathField;
	}

	public boolean isIncludeXmlParentPathField() {
		return includeXmlParentPathField;
	}

	public void setIncludeXmlParentPathField(boolean includeXmlParentPathField) {
		this.includeXmlParentPathField = includeXmlParentPathField;
	}

	public String getXmlParentPathField() {
		return xmlParentPathField;
	}

	public void setXmlParentPathField(String xmlParentPathField) {
		this.xmlParentPathField = xmlParentPathField;
	}

	public boolean isIncludeXmlDataNameField() {
		return includeXmlDataNameField;
	}

	public void setIncludeXmlDataNameField(boolean includeXmlDataNameField) {
		this.includeXmlDataNameField = includeXmlDataNameField;
	}

	public String getXmlDataNameField() {
		return xmlDataNameField;
	}

	public void setXmlDataNameField(String xmlDataNameField) {
		this.xmlDataNameField = xmlDataNameField;
	}

	public boolean isIncludeXmlDataValueField() {
		return includeXmlDataValueField;
	}

	public void setIncludeXmlDataValueField(boolean includeXmlDataValueField) {
		this.includeXmlDataValueField = includeXmlDataValueField;
	}

	public String getXmlDataValueField() {
		return xmlDataValueField;
	}

	public void setXmlDataValueField(String xmlDataValueField) {
		this.xmlDataValueField = xmlDataValueField;
	}

	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		return (SPXMLInputStream) JSONObject.toBean(jsonObj, SPXMLInputStream.class);
	}

	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPXMLInputStream spxmlinputstream = new SPXMLInputStream();
		//XMLInputStreamMeta xmlinputstreammeta = (XMLInputStreamMeta) stepMetaInterface;

		setToObject(stepMetaInterface, spxmlinputstream);
		
//		spxmlinputstream.setSourceFromInput(xmlinputstreammeta.sourceFromInput);
//		spxmlinputstream.setSourceFieldName(xmlinputstreammeta.sourceFieldName);
//		
//		spxmlinputstream.setFilename(xmlinputstreammeta.getFilename());
//		spxmlinputstream.setAddResultFile(xmlinputstreammeta.isAddResultFile());
//		spxmlinputstream.setNrRowsToSkip(xmlinputstreammeta.getNrRowsToSkip());
//		spxmlinputstream.setRowLimit(xmlinputstreammeta.getRowLimit());
//		spxmlinputstream.setDefaultStringLen(xmlinputstreammeta.getDefaultStringLen());
//		spxmlinputstream.setEncoding(xmlinputstreammeta.getEncoding());
//		spxmlinputstream.setEnableNamespaces(xmlinputstreammeta.isEnableNamespaces());
//		spxmlinputstream.setEnableTrim(xmlinputstreammeta.isEnableTrim());
//		spxmlinputstream.setIncludeFilenameField(xmlinputstreammeta.isIncludeFilenameField());
//		spxmlinputstream.setFilenameField(xmlinputstreammeta.getFilenameField());
//		spxmlinputstream.setIncludeRowNumberField(xmlinputstreammeta.isIncludeRowNumberField());
//		spxmlinputstream.setRowNumberField(xmlinputstreammeta.getRowNumberField());
//		spxmlinputstream.setIncludeXmlDataTypeNumericField(xmlinputstreammeta.isIncludeXmlDataTypeNumericField());
//		spxmlinputstream.setXmlDataTypeNumericField(xmlinputstreammeta.getXmlDataTypeNumericField());
//		spxmlinputstream.setIncludeXmlDataTypeDescriptionField(xmlinputstreammeta.isIncludeXmlDataTypeDescriptionField());
//		spxmlinputstream.setXmlDataTypeDescriptionField(xmlinputstreammeta.getXmlDataTypeDescriptionField());
//		spxmlinputstream.setIncludeXmlLocationLineField(xmlinputstreammeta.isIncludeXmlLocationLineField());
//		spxmlinputstream.setXmlLocationLineField(xmlinputstreammeta.getXmlLocationLineField());
//		spxmlinputstream.setIncludeXmlLocationColumnField(xmlinputstreammeta.isIncludeXmlLocationColumnField());
//		spxmlinputstream.setXmlLocationColumnField(xmlinputstreammeta.getXmlLocationColumnField());
//		spxmlinputstream.setIncludeXmlElementIDField(xmlinputstreammeta.isIncludeXmlElementIDField());
//		spxmlinputstream.setXmlElementIDField(xmlinputstreammeta.getXmlElementIDField());
//		spxmlinputstream.setIncludeXmlParentElementIDField(xmlinputstreammeta.isIncludeXmlParentElementIDField());
//		spxmlinputstream.setXmlParentElementIDField(xmlinputstreammeta.getXmlParentElementIDField());
//		spxmlinputstream.setIncludeXmlElementLevelField(xmlinputstreammeta.isIncludeXmlElementLevelField());
//		spxmlinputstream.setXmlElementLevelField(xmlinputstreammeta.getXmlElementLevelField());
//		spxmlinputstream.setIncludeXmlPathField(xmlinputstreammeta.isIncludeXmlPathField());
//		spxmlinputstream.setXmlPathField(xmlinputstreammeta.getXmlPathField());
//		spxmlinputstream.setIncludeXmlParentPathField(xmlinputstreammeta.isIncludeXmlParentPathField());
//		spxmlinputstream.setXmlParentPathField(xmlinputstreammeta.getXmlParentPathField());
//		spxmlinputstream.setIncludeXmlDataNameField(xmlinputstreammeta.isIncludeXmlDataNameField());
//		spxmlinputstream.setXmlDataNameField(xmlinputstreammeta.getXmlDataNameField());
//		spxmlinputstream.setIncludeXmlDataValueField(xmlinputstreammeta.isIncludeXmlDataValueField());
//		spxmlinputstream.setXmlDataValueField(xmlinputstreammeta.getXmlDataValueField());
		
		return spxmlinputstream;
	}

	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases, TransMeta transMeta)
			throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPXMLInputStream spxmlinputstream = (SPXMLInputStream) po;
		//XMLInputStreamMeta xmlinputstreammeta = (XMLInputStreamMeta) stepMetaInterface;

		setToObject(spxmlinputstream ,stepMetaInterface );
		
//		xmlinputstreammeta.sourceFromInput=spxmlinputstream.isSourceFromInput();
//		xmlinputstreammeta.sourceFieldName=spxmlinputstream.getSourceFieldName();
//		
//		xmlinputstreammeta.setFilename(spxmlinputstream.getFilename());
//		xmlinputstreammeta.setAddResultFile(spxmlinputstream.isAddResultFile());
//		xmlinputstreammeta.setNrRowsToSkip(spxmlinputstream.getNrRowsToSkip());
//		xmlinputstreammeta.setRowLimit(spxmlinputstream.getRowLimit());
//		xmlinputstreammeta.setDefaultStringLen(spxmlinputstream.getDefaultStringLen());
//		xmlinputstreammeta.setEncoding(spxmlinputstream.getEncoding());
//		xmlinputstreammeta.setEnableNamespaces(spxmlinputstream.isEnableNamespaces());
//		xmlinputstreammeta.setEnableTrim(spxmlinputstream.isEnableTrim());
//		xmlinputstreammeta.setIncludeFilenameField(spxmlinputstream.isIncludeFilenameField());
//		xmlinputstreammeta.setFilenameField(spxmlinputstream.getFilenameField());
//		xmlinputstreammeta.setIncludeRowNumberField(spxmlinputstream.isIncludeRowNumberField());
//		xmlinputstreammeta.setRowNumberField(spxmlinputstream.getRowNumberField());
//		xmlinputstreammeta.setIncludeXmlDataTypeNumericField(spxmlinputstream.isIncludeXmlDataTypeNumericField());
//		xmlinputstreammeta.setXmlDataTypeNumericField(spxmlinputstream.getXmlDataTypeNumericField());
//		xmlinputstreammeta.setIncludeXmlDataTypeDescriptionField(spxmlinputstream.isIncludeXmlDataTypeDescriptionField());
//		xmlinputstreammeta.setXmlDataTypeDescriptionField(spxmlinputstream.getXmlDataTypeDescriptionField());
//		xmlinputstreammeta.setIncludeXmlLocationLineField(spxmlinputstream.isIncludeXmlLocationLineField());
//		xmlinputstreammeta.setXmlLocationLineField(spxmlinputstream.getXmlLocationLineField());
//		xmlinputstreammeta.setIncludeXmlLocationColumnField(spxmlinputstream.isIncludeXmlLocationColumnField());
//		xmlinputstreammeta.setXmlLocationColumnField(spxmlinputstream.getXmlLocationColumnField());
//		xmlinputstreammeta.setIncludeXmlElementIDField(spxmlinputstream.isIncludeXmlElementIDField());
//		xmlinputstreammeta.setXmlElementIDField(spxmlinputstream.getXmlElementIDField());
//		xmlinputstreammeta.setIncludeXmlParentElementIDField(spxmlinputstream.isIncludeXmlParentElementIDField());
//		xmlinputstreammeta.setXmlParentElementIDField(spxmlinputstream.getXmlParentElementIDField());
//		xmlinputstreammeta.setIncludeXmlElementLevelField(spxmlinputstream.isIncludeXmlElementLevelField());
//		xmlinputstreammeta.setXmlElementLevelField(spxmlinputstream.getXmlElementLevelField());
//		xmlinputstreammeta.setIncludeXmlPathField(spxmlinputstream.isIncludeXmlPathField());
//		xmlinputstreammeta.setXmlPathField(spxmlinputstream.getXmlPathField());
//		xmlinputstreammeta.setIncludeXmlParentPathField(spxmlinputstream.isIncludeXmlParentPathField());
//		xmlinputstreammeta.setXmlParentPathField(spxmlinputstream.getXmlParentPathField());
//		xmlinputstreammeta.setIncludeXmlDataNameField(spxmlinputstream.isIncludeXmlDataNameField());
//		xmlinputstreammeta.setXmlDataNameField(spxmlinputstream.getXmlDataNameField());
//		xmlinputstreammeta.setIncludeXmlDataValueField(spxmlinputstream.isIncludeXmlDataValueField());
//		xmlinputstreammeta.setXmlDataValueField(spxmlinputstream.getXmlDataValueField());

	}

	@Override
	public int stepType() {
		return 1;
	}

	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr)
			throws Exception {

		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		
		String  fileName = (String) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getFilename");
		if( !Utils.isEmpty( fileName) ) {
			String encoding = (String) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getEncoding");
			
			DataNode fileDataNode = DataNodeUtil.fileNodeParse("Xml", fileName.trim(), encoding, "", "" ) ;
			sdr.addInputDataNode(fileDataNode);
			
			// 增加 系统节点 和 流节点的关系
			String from= "转换:"+transMeta.getName()+",步骤:"+stepMeta.getName();
			List<Relationship> relationships = RelationshipUtil.inputStepRelationship(null, fileDataNode, sdr.getOutputStream(), stepMeta.getName(), from);
			sdr.getDataRelationship().addAll(relationships);
			
		}

	}

}