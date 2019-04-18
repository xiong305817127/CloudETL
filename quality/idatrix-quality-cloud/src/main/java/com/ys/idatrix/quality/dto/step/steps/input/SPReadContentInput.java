package com.ys.idatrix.quality.dto.step.steps.input;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.ys.idatrix.quality.dto.step.parts.TextFileInputFileDto;
import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.quality.toolkit.domain.DataNode;
import com.ys.idatrix.quality.toolkit.domain.Relationship;
import com.ys.idatrix.quality.toolkit.utils.DataNodeUtil;
import com.ys.idatrix.quality.toolkit.utils.RelationshipUtil;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import net.sf.json.JSONObject;

/**
 * Step - ReadContentInput.
 * 转换  com.ys.idatrix.cloudetl.readcontent.ReadContentInputMeta
 * 
 * @author XH
 * @since 2018-09-14
 */
@Component("SPReadContentInput")
@Scope("prototype")
public class SPReadContentInput implements StepParameter, StepDataRelationshipParser {

	private String type = "word";

	private String contentFieldName = "content";

	// 是否一页/一段/一行作为一行数据
	private boolean isPageRow = false;

	private List<TextFileInputFileDto> files;
	
	private String encoding = "UTF-8";

	private boolean acceptingFilenames = false;
	private String acceptingField;
	private String acceptingStepName;

	private boolean includeFileName = false;
	private boolean includeOnlyFileName = false;
	private String fileNameFieldName = "filename";
	
	private boolean addResultFile = false;
	private boolean ignoreError = false;
	private boolean isOnlyVisible = true;
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getContentFieldName() {
		return contentFieldName;
	}

	public void setContentFieldName(String contentFieldName) {
		this.contentFieldName = contentFieldName;
	}

	public boolean isPageRow() {
		return isPageRow;
	}

	public void setPageRow(boolean isPageRow) {
		this.isPageRow = isPageRow;
	}


	public List<TextFileInputFileDto> getFiles() {
		return files;
	}

	public void setFiles(List<TextFileInputFileDto> files) {
		this.files = files;
	}
	
	public void addFiles(TextFileInputFileDto file) {
		if (this.files == null) {
			this.files= Lists.newArrayList() ;
		}
		this.files.add(file);
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public boolean isAcceptingFilenames() {
		return acceptingFilenames;
	}

	public void setAcceptingFilenames(boolean acceptingFilenames) {
		this.acceptingFilenames = acceptingFilenames;
	}

	public String getAcceptingField() {
		return acceptingField;
	}

	public void setAcceptingField(String acceptingField) {
		this.acceptingField = acceptingField;
	}

	public String getAcceptingStepName() {
		return acceptingStepName;
	}

	public void setAcceptingStepName(String acceptingStepName) {
		this.acceptingStepName = acceptingStepName;
	}

	public boolean isIncludeFileName() {
		return includeFileName;
	}

	public void setIncludeFileName(boolean includeFileName) {
		this.includeFileName = includeFileName;
	}

	public boolean isIncludeOnlyFileName() {
		return includeOnlyFileName;
	}

	public void setIncludeOnlyFileName(boolean includeOnlyFileName) {
		this.includeOnlyFileName = includeOnlyFileName;
	}

	public String getFileNameFieldName() {
		return fileNameFieldName;
	}

	public void setFileNameFieldName(String fileNameFieldName) {
		this.fileNameFieldName = fileNameFieldName;
	}

	public boolean isAddResultFile() {
		return addResultFile;
	}

	public void setAddResultFile(boolean addResultFile) {
		this.addResultFile = addResultFile;
	}

	public boolean isIgnoreError() {
		return ignoreError;
	}

	public void setIgnoreError(boolean ignoreError) {
		this.ignoreError = ignoreError;
	}

	public boolean isOnlyVisible() {
		return isOnlyVisible;
	}

	public void setOnlyVisible(boolean isOnlyVisible) {
		this.isOnlyVisible = isOnlyVisible;
	}

	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("files", TextFileInputFileDto.class);
		return (SPReadContentInput) JSONObject.toBean(jsonObj, SPReadContentInput.class,classMap);
	}

	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface() ;
		SPReadContentInput spReadContentInput= new SPReadContentInput();
		//com.ys.idatrix.cloudetl.readcontent.ReadContentInputMeta readcontentinputmeta= (com.ys.idatrix.cloudetl.readcontent.ReadContentInputMeta )stepMetaInterface;
		
		setToObject(stepMetaInterface, spReadContentInput);
		
//		spReadContentInput.setType(readcontentinputmeta.getType());
//		spReadContentInput.setEncoding(readcontentinputmeta.getEncoding());
//		spReadContentInput.setContentFieldName(readcontentinputmeta.getContentFieldName());
//		spReadContentInput.setAcceptingField(readcontentinputmeta.getAcceptingField());
//		spReadContentInput.setAcceptingStepName(readcontentinputmeta.getAcceptingStepName());
//		spReadContentInput.setFileNameFieldName(readcontentinputmeta.getFileNameFieldName());
//		spReadContentInput.setAddResultFile(readcontentinputmeta.isAddResultFile());
//		spReadContentInput.setAcceptingFilenames(readcontentinputmeta.isAcceptingFilenames());
//		spReadContentInput.setIncludeFileName(readcontentinputmeta.isIncludeFileName());
//		spReadContentInput.setPageRow( readcontentinputmeta.isPageRow()) ;
		
		//String[] fileNames = readcontentinputmeta.getFileNames();
		String[] fileNames = (String[]) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getFileNames");
		String[] fileMasks = (String[]) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getFileMasks");
		String[] fileRequireds = (String[]) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getFileRequireds");
		String[] excludeFileMasks = (String[]) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getExcludeFileMasks");
		String[] includeSubFolders = (String[]) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getIncludeSubFolders");
		if(fileNames != null && fileNames.length >0) {
			for ( int i =0 ; i < fileNames.length ; i++ ) {
				
				TextFileInputFileDto tfifd = new TextFileInputFileDto();
				tfifd.setFileName(fileNames[i]);
				tfifd.setFileMask(fileMasks[i]);
				tfifd.setFileRequired(fileRequireds[i]);
				tfifd.setExcludeFileMask(excludeFileMasks[i]);
				tfifd.setIncludeSubFolders(includeSubFolders[i]);
				
				spReadContentInput.addFiles(tfifd);
			}
		}

		return spReadContentInput;
		
	}

	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases, TransMeta transMeta)
			throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface() ;
		SPReadContentInput spReadContentInput= (SPReadContentInput)po;
		
		//com.ys.idatrix.cloudetl.readcontent.ReadContentInputMeta   readcontentinputmeta = (com.ys.idatrix.cloudetl.readcontent.ReadContentInputMeta) stepMetaInterface;
		
		setToObject(spReadContentInput, stepMetaInterface);
		
//		readcontentinputmeta.setType(spReadContentInput.getType());
//		readcontentinputmeta.setAddResultFile(spReadContentInput.isAddResultFile());
//		readcontentinputmeta.setEncoding(spReadContentInput.getEncoding());
//		readcontentinputmeta.setContentFieldName(spReadContentInput.getContentFieldName());
//		readcontentinputmeta.setAcceptingFilenames(spReadContentInput.isAcceptingFilenames());
//		readcontentinputmeta.setAcceptingField(spReadContentInput.getAcceptingField());
//		readcontentinputmeta.setAcceptingStepName(spReadContentInput.getAcceptingStepName());
//		readcontentinputmeta.setIncludeFileName(spReadContentInput.isIncludeFileName());
//		readcontentinputmeta.setFileNameFieldName(spReadContentInput.getFileNameFieldName());
//		readcontentinputmeta.setPageRow(spReadContentInput.isPageRow() ) ;
		
		List<TextFileInputFileDto> tfifds = spReadContentInput.getFiles();
		String[] fileNames = new String[tfifds.size()] ;
		String[] fileMasks = new String[tfifds.size()];
		String[] fileRequireds = new String[tfifds.size()];
		String[] excludeFileMasks = new String[tfifds.size()];
		String[] includeSubFolders = new String[tfifds.size()];
		if(tfifds != null && tfifds.size() >0) {
			for ( int i =0 ; i < tfifds.size() ; i++ ) {
				TextFileInputFileDto tfifd = tfifds.get(i);

				fileNames[i] = tfifd.getFileName();
				fileMasks[i] = tfifd.getFileMask();
				fileRequireds[i] = tfifd.getFileRequired();
				excludeFileMasks[i] = tfifd.getExcludeFileMask();
				includeSubFolders[i] = tfifd.getIncludeSubFolders();
				
			}
		}
		//readcontentinputmeta.setFileNames(new String[tfifds.size()]);
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setFileNames",new Object[] { fileNames }) ;
		//readcontentinputmeta.setFileMasks(new String[tfifds.size()]);
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setFileMasks", new Object[] {fileMasks}) ;
		//readcontentinputmeta.setFileRequireds(new String[tfifds.size()]);
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setFileRequireds", new Object[] {fileRequireds}) ;
		//readcontentinputmeta.setExcludeFileMasks(new String[tfifds.size()]);
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setExcludeFileMasks", new Object[] {excludeFileMasks} ) ;
		//readcontentinputmeta.setIncludeSubFolders(new String[tfifds.size()]);
		OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "setIncludeSubFolders", new Object[] {includeSubFolders}) ;
		
	}

	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {
		
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		
		String encoding = (String) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getEncoding");
		
		String[] fileNames = (String[]) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getFileNames");
		for (String name : fileNames) {
			if (StringUtils.isNotEmpty(name)) {
				
				DataNode fileDataNode = DataNodeUtil.fileNodeParse("ReadContent", name.trim(), encoding , "", "" ) ;
				sdr.addInputDataNode(fileDataNode);
				
				// 增加 系统节点 和 流节点的关系
				String from= "转换:"+transMeta.getName()+",步骤:"+stepMeta.getName();
				List<Relationship> relationships = RelationshipUtil.inputStepRelationship(null, fileDataNode, sdr.getOutputStream(), stepMeta.getName(), from);
				sdr.getDataRelationship().addAll(relationships);
			}
		}
		
	}

}
