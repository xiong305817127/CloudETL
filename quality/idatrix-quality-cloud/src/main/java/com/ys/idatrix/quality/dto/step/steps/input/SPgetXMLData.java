package com.ys.idatrix.quality.dto.step.steps.input;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.row.value.ValueMetaBase;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.quality.toolkit.domain.DataNode;
import com.ys.idatrix.quality.toolkit.domain.Relationship;
import com.ys.idatrix.quality.toolkit.utils.DataNodeUtil;
import com.ys.idatrix.quality.toolkit.utils.RelationshipUtil;
import com.google.common.collect.Lists;
import com.ys.idatrix.quality.dto.step.parts.TextFileInputFileDto;
import com.ys.idatrix.quality.dto.step.parts.XMLDataFieldDto;
import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.recovery.trans.ResumeStepDataParser;
import net.sf.json.JSONObject;

/**
 * Step - getXMLData. 转换 org.pentaho.di.trans.steps.getxmldata.GetXMLDataMeta
 * 
 * @author XH
 * @since 2018-11-08
 */
@Component("SPgetXMLData")
@Scope("prototype")
public class SPgetXMLData implements StepParameter, StepDataRelationshipParser, ResumeStepDataParser {

	private boolean includeFilename;
	private String filenameField;
	private boolean includeRowNumber;
	private boolean addResultFile;
	private boolean nameSpaceAware;
	private boolean ignorecomments;
	private boolean readurl;
	private boolean validating;
	private boolean usetoken;
	private boolean IsIgnoreEmptyFile;
	private boolean doNotFailIfNoFile;
	private String rowNumberField;
	private String encoding;

	List<TextFileInputFileDto> fileNames;
	private List<XMLDataFieldDto> inputFields;

	private long rowLimit;
	private String loopxpath;
	private boolean inFields;
	private boolean IsAFile;
	private String xmlField;
	private String prunePath;
	private String shortFileFieldName;
	private String pathFieldName;
	private String hiddenFieldName;
	private String lastModificationTimeFieldName;
	private String uriNameFieldName;
	private String rootUriNameFieldName;
	private String extensionFieldName;
	private String sizeFieldName;

	public boolean isIncludeFilename() {
		return includeFilename;
	}

	public void setIncludeFilename(boolean includeFilename) {
		this.includeFilename = includeFilename;
	}

	public String getFilenameField() {
		return filenameField;
	}

	public void setFilenameField(String filenameField) {
		this.filenameField = filenameField;
	}

	public boolean isIncludeRowNumber() {
		return includeRowNumber;
	}

	public void setIncludeRowNumber(boolean includeRowNumber) {
		this.includeRowNumber = includeRowNumber;
	}

	public boolean isAddResultFile() {
		return addResultFile;
	}

	public void setAddResultFile(boolean addResultFile) {
		this.addResultFile = addResultFile;
	}

	public boolean isNameSpaceAware() {
		return nameSpaceAware;
	}

	public void setNameSpaceAware(boolean nameSpaceAware) {
		this.nameSpaceAware = nameSpaceAware;
	}

	public boolean isIgnorecomments() {
		return ignorecomments;
	}

	public void setIgnorecomments(boolean ignorecomments) {
		this.ignorecomments = ignorecomments;
	}

	public boolean isReadurl() {
		return readurl;
	}

	public void setReadurl(boolean readurl) {
		this.readurl = readurl;
	}

	public boolean isValidating() {
		return validating;
	}

	public void setValidating(boolean validating) {
		this.validating = validating;
	}

	public boolean isUsetoken() {
		return usetoken;
	}

	public void setUsetoken(boolean usetoken) {
		this.usetoken = usetoken;
	}

	public boolean isIsIgnoreEmptyFile() {
		return IsIgnoreEmptyFile;
	}

	public void setIsIgnoreEmptyFile(boolean isIgnoreEmptyFile) {
		IsIgnoreEmptyFile = isIgnoreEmptyFile;
	}

	public boolean isDoNotFailIfNoFile() {
		return doNotFailIfNoFile;
	}

	public void setDoNotFailIfNoFile(boolean doNotFailIfNoFile) {
		this.doNotFailIfNoFile = doNotFailIfNoFile;
	}

	public String getRowNumberField() {
		return rowNumberField;
	}

	public void setRowNumberField(String rowNumberField) {
		this.rowNumberField = rowNumberField;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public List<TextFileInputFileDto> getFileNames() {
		return fileNames;
	}

	public void setFileNames(List<TextFileInputFileDto> fileNames) {
		this.fileNames = fileNames;
	}

	public List<XMLDataFieldDto> getInputFields() {
		return inputFields;
	}

	public void setInputFields(List<XMLDataFieldDto> inputFields) {
		this.inputFields = inputFields;
	}

	public long getRowLimit() {
		return rowLimit;
	}

	public void setRowLimit(long rowLimit) {
		this.rowLimit = rowLimit;
	}

	public String getLoopxpath() {
		return loopxpath;
	}

	public void setLoopxpath(String loopxpath) {
		this.loopxpath = loopxpath;
	}

	public boolean isInFields() {
		return inFields;
	}

	public void setInFields(boolean inFields) {
		this.inFields = inFields;
	}

	public boolean isIsAFile() {
		return IsAFile;
	}

	public void setIsAFile(boolean isAFile) {
		IsAFile = isAFile;
	}

	public String getXmlField() {
		return xmlField;
	}

	public void setXmlField(String xmlField) {
		this.xmlField = xmlField;
	}

	public String getPrunePath() {
		return prunePath;
	}

	public void setPrunePath(String prunePath) {
		this.prunePath = prunePath;
	}

	public String getShortFileFieldName() {
		return shortFileFieldName;
	}

	public void setShortFileFieldName(String shortFileFieldName) {
		this.shortFileFieldName = shortFileFieldName;
	}

	public String getPathFieldName() {
		return pathFieldName;
	}

	public void setPathFieldName(String pathFieldName) {
		this.pathFieldName = pathFieldName;
	}

	public String getHiddenFieldName() {
		return hiddenFieldName;
	}

	public void setHiddenFieldName(String hiddenFieldName) {
		this.hiddenFieldName = hiddenFieldName;
	}

	public String getLastModificationTimeFieldName() {
		return lastModificationTimeFieldName;
	}

	public void setLastModificationTimeFieldName(String lastModificationTimeFieldName) {
		this.lastModificationTimeFieldName = lastModificationTimeFieldName;
	}

	public String getUriNameFieldName() {
		return uriNameFieldName;
	}

	public void setUriNameFieldName(String uriNameFieldName) {
		this.uriNameFieldName = uriNameFieldName;
	}

	public String getRootUriNameFieldName() {
		return rootUriNameFieldName;
	}

	public void setRootUriNameFieldName(String rootUriNameFieldName) {
		this.rootUriNameFieldName = rootUriNameFieldName;
	}

	public String getExtensionFieldName() {
		return extensionFieldName;
	}

	public void setExtensionFieldName(String extensionFieldName) {
		this.extensionFieldName = extensionFieldName;
	}

	public String getSizeFieldName() {
		return sizeFieldName;
	}

	public void setSizeFieldName(String sizeFieldName) {
		this.sizeFieldName = sizeFieldName;
	}

	@Override
	public Object getParameterObject(Object json) {

		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("fileNames", TextFileInputFileDto.class);
		classMap.put("inputFields", XMLDataFieldDto.class);
		return (SPgetXMLData) JSONObject.toBean(jsonObj, SPgetXMLData.class, classMap);
	}

	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {

		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPgetXMLData spgetxmldata = new SPgetXMLData();
		//GetXMLDataMeta getxmldatameta = (GetXMLDataMeta) stepMetaInterface;

		setToObject(stepMetaInterface, spgetxmldata);
		
//		spgetxmldata.setIncludeFilename(getxmldatameta.includeFilename());
//		spgetxmldata.setFilenameField(getxmldatameta.getFilenameField());
//		spgetxmldata.setIncludeRowNumber(getxmldatameta.includeRowNumber());
//		spgetxmldata.setAddResultFile(getxmldatameta.addResultFile());
//		spgetxmldata.setNameSpaceAware(getxmldatameta.isNamespaceAware());
//		spgetxmldata.setIgnorecomments(getxmldatameta.isIgnoreComments());
//		spgetxmldata.setReadurl(getxmldatameta.isReadUrl());
//		spgetxmldata.setValidating(getxmldatameta.isValidating());
//		spgetxmldata.setUsetoken(getxmldatameta.isuseToken());
//		spgetxmldata.setIsIgnoreEmptyFile(getxmldatameta.isIgnoreEmptyFile());
//		spgetxmldata.setDoNotFailIfNoFile(getxmldatameta.isdoNotFailIfNoFile());
//		spgetxmldata.setRowNumberField(getxmldatameta.getRowNumberField());
//		spgetxmldata.setEncoding(getxmldatameta.getEncoding());
//		
//		spgetxmldata.setRowLimit(getxmldatameta.getRowLimit());
//		spgetxmldata.setLoopxpath(getxmldatameta.getLoopXPath());
//		spgetxmldata.setInFields(getxmldatameta.isInFields());
//		spgetxmldata.setIsAFile(getxmldatameta.getIsAFile());
//		spgetxmldata.setXmlField(getxmldatameta.getXMLField());
//		spgetxmldata.setPrunePath(getxmldatameta.getPrunePath());
//		spgetxmldata.setShortFileFieldName(getxmldatameta.getShortFileNameField());
//		spgetxmldata.setPathFieldName(getxmldatameta.getPathField());
//		spgetxmldata.setHiddenFieldName(getxmldatameta.isHiddenField());
//		spgetxmldata.setLastModificationTimeFieldName(getxmldatameta.getLastModificationDateField());
//		spgetxmldata.setUriNameFieldName(getxmldatameta.getUriField());
//		spgetxmldata.setRootUriNameFieldName(getxmldatameta.getRootUriField());
//		spgetxmldata.setExtensionFieldName(getxmldatameta.getExtensionField());
//		spgetxmldata.setSizeFieldName(getxmldatameta.getSizeField());
		

		List<TextFileInputFileDto> textfileinputfiledtoList = Lists.newArrayList();
		//String[] fileNameMetaArray = getxmldatameta.getFileName();
		String[] fileNameMetaArray = (String[]) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getFileName");
		String[] fileMaskMetaArray = (String[]) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getFileMask");
		String[] exludeFileMaskMetaArray = (String[]) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getExludeFileMask");
		String[] fileRequiredMetaArray = (String[]) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getFileRequired");
		String[] includeSubFoldersMetaArray = (String[]) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getIncludeSubFolders");
		for (int i = 0; fileNameMetaArray != null && i < fileNameMetaArray.length; i++) {
			TextFileInputFileDto tempobj = new TextFileInputFileDto();
			tempobj.setFileName(fileNameMetaArray[i]);//getxmldatameta.getFileName()[i]);
			tempobj.setFileMask(fileMaskMetaArray[i]);// getxmldatameta.getFileMask()[i]);
			tempobj.setExcludeFileMask(exludeFileMaskMetaArray[i]);//getxmldatameta.getExludeFileMask()[i]);
			tempobj.setFileRequired(fileRequiredMetaArray[i]);//getxmldatameta.getFileRequired()[i]);
			tempobj.setIncludeSubFolders(includeSubFoldersMetaArray[i]);//getxmldatameta.getIncludeSubFolders()[i]);
			textfileinputfiledtoList.add(tempobj);
		}
		spgetxmldata.setFileNames(textfileinputfiledtoList);

		List<XMLDataFieldDto> xmldatafielddtoList = Lists.newArrayList();
		//GetXMLDataField[] inputFieldsMetaArray = getxmldatameta.getInputFields();
		Object[] inputFieldsMetaArray = (Object[]) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getInputFields");
		for (int i = 0; inputFieldsMetaArray != null && i < inputFieldsMetaArray.length; i++) {
			XMLDataFieldDto tempobj = new XMLDataFieldDto();
			
			setToObject(inputFieldsMetaArray[i], tempobj);
			
//			tempobj.setName(inputFieldsMetaArray[i].getName());
//			tempobj.setXpath(inputFieldsMetaArray[i].getXPath());
//			tempobj.setFormat(inputFieldsMetaArray[i].getFormat());
//			tempobj.setCurrencySymbol(inputFieldsMetaArray[i].getCurrencySymbol());
//			tempobj.setDecimalSymbol(inputFieldsMetaArray[i].getDecimalSymbol());
//			tempobj.setGroupSymbol(inputFieldsMetaArray[i].getGroupSymbol());
//			tempobj.setLength(inputFieldsMetaArray[i].getLength());
//			tempobj.setPrecision(inputFieldsMetaArray[i].getPrecision());
//			tempobj.setRepeat(inputFieldsMetaArray[i].isRepeated());
			
			//tempobj.setType(inputFieldsMetaArray[i].getTypeDesc());
			tempobj.setType( (String)OsgiBundleUtils.invokeOsgiMethod(inputFieldsMetaArray[i], "getTypeDesc"));
			//tempobj.setElementtype(inputFieldsMetaArray[i].getElementTypeCode());
			tempobj.setElementtype((String)OsgiBundleUtils.invokeOsgiMethod(inputFieldsMetaArray[i], "getElementTypeCode"));
			//tempobj.setResulttype(inputFieldsMetaArray[i].getResultTypeCode());
			tempobj.setResulttype((String)OsgiBundleUtils.invokeOsgiMethod(inputFieldsMetaArray[i], "getResultTypeCode"));
			//tempobj.setTrimtype(inputFieldsMetaArray[i].getTrimTypeCode());
			tempobj.setTrimtype((String)OsgiBundleUtils.invokeOsgiMethod(inputFieldsMetaArray[i], "getTrimTypeCode"));
			
			xmldatafielddtoList.add(tempobj);
		}
		spgetxmldata.setInputFields(xmldatafielddtoList);

		
		return spgetxmldata;
	}

	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases, TransMeta transMeta)
			throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPgetXMLData spgetxmldata = (SPgetXMLData) po;
		//GetXMLDataMeta getxmldatameta = (GetXMLDataMeta) stepMetaInterface;

		setToObject(spgetxmldata, stepMetaInterface);
		
//		getxmldatameta.setIncludeFilename(spgetxmldata.isIncludeFilename());
//		getxmldatameta.setFilenameField(spgetxmldata.getFilenameField());
//		getxmldatameta.setIncludeRowNumber(spgetxmldata.isIncludeRowNumber());
//		getxmldatameta.setAddResultFile(spgetxmldata.isAddResultFile());
//		getxmldatameta.setNamespaceAware(spgetxmldata.isNameSpaceAware());
//		getxmldatameta.setIgnoreComments(spgetxmldata.isIgnorecomments());
//		getxmldatameta.setReadUrl(spgetxmldata.isReadurl());
//		getxmldatameta.setValidating(spgetxmldata.isValidating());
//		getxmldatameta.setuseToken(spgetxmldata.isUsetoken());
//		getxmldatameta.setIgnoreEmptyFile(spgetxmldata.isIsIgnoreEmptyFile());
//		getxmldatameta.setdoNotFailIfNoFile(spgetxmldata.isDoNotFailIfNoFile());
//		getxmldatameta.setRowNumberField(spgetxmldata.getRowNumberField());
//		getxmldatameta.setEncoding(spgetxmldata.getEncoding());
//		
//		getxmldatameta.setRowLimit(spgetxmldata.getRowLimit());
//		getxmldatameta.setLoopXPath(spgetxmldata.getLoopxpath());
//		getxmldatameta.setInFields(spgetxmldata.isInFields());
//		getxmldatameta.setIsAFile(spgetxmldata.isIsAFile());
//		getxmldatameta.setXMLField(spgetxmldata.getXmlField());
//		getxmldatameta.setPrunePath(spgetxmldata.getPrunePath());
//		getxmldatameta.setShortFileNameField(spgetxmldata.getShortFileFieldName());
//		getxmldatameta.setPathField(spgetxmldata.getPathFieldName());
//		getxmldatameta.setIsHiddenField(spgetxmldata.getHiddenFieldName());
//		getxmldatameta.setLastModificationDateField(spgetxmldata.getLastModificationTimeFieldName());
//		getxmldatameta.setUriField(spgetxmldata.getUriNameFieldName());
//		getxmldatameta.setRootUriField(spgetxmldata.getRootUriNameFieldName());
//		getxmldatameta.setExtensionField(spgetxmldata.getExtensionFieldName());
//		getxmldatameta.setSizeField(spgetxmldata.getSizeFieldName());

		List<TextFileInputFileDto> textfileinputfiledtoList = spgetxmldata.getFileNames();
		if (textfileinputfiledtoList != null && textfileinputfiledtoList.size() > 0) {
			String[] fileNamemetaArray = new String[textfileinputfiledtoList.size()];
			String[] fileMaskmetaArray = new String[textfileinputfiledtoList.size()];
			String[] excludeFileMaskmetaArray = new String[textfileinputfiledtoList.size()];
			String[] fileRequiredmetaArray = new String[textfileinputfiledtoList.size()];
			String[] includeSubFoldersmetaArray = new String[textfileinputfiledtoList.size()];
			for (int i = 0; textfileinputfiledtoList != null && i < textfileinputfiledtoList.size(); i++) {
				fileNamemetaArray[i] = textfileinputfiledtoList.get(i).getFileName();
				fileMaskmetaArray[i] = textfileinputfiledtoList.get(i).getFileMask();
				excludeFileMaskmetaArray[i] = textfileinputfiledtoList.get(i).getExcludeFileMask();
				fileRequiredmetaArray[i] = textfileinputfiledtoList.get(i).getFileRequired();
				includeSubFoldersmetaArray[i] = textfileinputfiledtoList.get(i).getIncludeSubFolders();
			}
			//getxmldatameta.setFileName(fileNamemetaArray);
			OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setFileName", new Object[] {fileNamemetaArray});
			//getxmldatameta.setFileMask(fileMaskmetaArray);
			OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setFileMask", new Object[] {fileMaskmetaArray});
			//getxmldatameta.setExcludeFileMask(excludeFileMaskmetaArray);
			OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setExcludeFileMask", new Object[] {excludeFileMaskmetaArray});
			//getxmldatameta.setFileRequired(fileRequiredmetaArray);
			OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setFileRequired", new Object[] {fileRequiredmetaArray});
			//getxmldatameta.setIncludeSubFolders(includeSubFoldersmetaArray);
			OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setIncludeSubFolders", new Object[] {includeSubFoldersmetaArray});

		}

		List<XMLDataFieldDto> xmldatafielddtoList = spgetxmldata.getInputFields();
		if (xmldatafielddtoList != null && xmldatafielddtoList.size() > 0) {
			//GetXMLDataField[] inputFieldsMetaArray = new GetXMLDataField[xmldatafielddtoList.size()];
			Object[] inputFieldsMetaArray = (Object[]) Array.newInstance(stepMetaInterface.getClass().getClassLoader().loadClass("org.pentaho.di.trans.steps.getxmldata.GetXMLDataField"), xmldatafielddtoList.size());
			for (int i = 0; xmldatafielddtoList != null && i < xmldatafielddtoList.size(); i++) {
				//GetXMLDataField tempobj = new GetXMLDataField();
				Object tempobj = OsgiBundleUtils.newOsgiInstance(stepMetaInterface, "org.pentaho.di.trans.steps.getxmldata.GetXMLDataField");
				
				setToObject(xmldatafielddtoList.get(i), tempobj);
				
//				tempobj.setName(xmldatafielddtoList.get(i).getName());
//				tempobj.setXPath(xmldatafielddtoList.get(i).getXpath());
//				tempobj.setFormat(xmldatafielddtoList.get(i).getFormat());
//				tempobj.setCurrencySymbol(xmldatafielddtoList.get(i).getCurrencySymbol());
//				tempobj.setDecimalSymbol(xmldatafielddtoList.get(i).getDecimalSymbol());
//				tempobj.setGroupSymbol(xmldatafielddtoList.get(i).getGroupSymbol());
//				tempobj.setLength(xmldatafielddtoList.get(i).getLength());
//				tempobj.setPrecision(xmldatafielddtoList.get(i).getPrecision());
//				tempobj.setRepeated(xmldatafielddtoList.get(i).isRepeat());
				
				//tempobj.setType(ValueMetaBase.getType(xmldatafielddtoList.get(i).getType()));
				OsgiBundleUtils.invokeOsgiMethod(tempobj, "setType", ValueMetaBase.getType(xmldatafielddtoList.get(i).getType()) ) ;
				//tempobj.setElementType( GetXMLDataField.getElementTypeByCode(xmldatafielddtoList.get(i).getElementtype()));
				OsgiBundleUtils.invokeOsgiMethod(tempobj, "setElementType", OsgiBundleUtils.invokeOsgiMethod(tempobj, "getElementTypeByCode", xmldatafielddtoList.get(i).getElementtype()) ) ;
				//tempobj.setResultType(GetXMLDataField.getResultTypeByCode(xmldatafielddtoList.get(i).getResulttype()));
				OsgiBundleUtils.invokeOsgiMethod(tempobj, "setResultType", OsgiBundleUtils.invokeOsgiMethod(tempobj, "getResultTypeByCode", xmldatafielddtoList.get(i).getResulttype()) ) ;
				//tempobj.setTrimType(GetXMLDataField.getTrimTypeByCode(xmldatafielddtoList.get(i).getTrimtype()));
				OsgiBundleUtils.invokeOsgiMethod(tempobj, "setTrimType", OsgiBundleUtils.invokeOsgiMethod(tempobj, "getTrimTypeByCode", xmldatafielddtoList.get(i).getTrimtype()) ) ;
				
				inputFieldsMetaArray[i] = tempobj;
			}
			//getxmldatameta.setInputFields(inputFieldsMetaArray);
			OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setInputFields", inputFieldsMetaArray) ;
		}

	}

	@Override
	public int stepType() {
		return 1;
	}

	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr)
			throws Exception {

		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		
		String[] fileNames = (String[]) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getFileName");
		if(fileNames != null && fileNames.length > 0 ) {
			String encoding = (String) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getEncoding");
			for (String name : fileNames) {
				if (!Utils.isEmpty(name)) {
					
					DataNode fileDataNode = DataNodeUtil.fileNodeParse("Xml", name.trim(), encoding, "", "" ) ;
					sdr.addInputDataNode(fileDataNode);
					
					// 增加 系统节点 和 流节点的关系
					String from = "转换:" + transMeta.getName() + ",步骤:" + stepMeta.getName();
					List<Relationship> relationships = RelationshipUtil.inputStepRelationship(null, fileDataNode, sdr.getOutputStream(), stepMeta.getName(), from);
					sdr.getDataRelationship().addAll(relationships);
					
				}
			}
		}

	}

}