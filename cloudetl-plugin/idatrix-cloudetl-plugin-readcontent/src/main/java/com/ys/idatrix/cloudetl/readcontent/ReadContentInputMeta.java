package com.ys.idatrix.cloudetl.readcontent;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.fileinput.FileInputList;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.resource.ResourceDefinition;
import org.pentaho.di.resource.ResourceNamingInterface;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

@Step( id = "ReadContentInput", image = "RCI.svg", name = "ReadContent", description = "ReadContent  Description", categoryDescription = "i18n:org.pentaho.di.trans.step:BaseStep.Category.Input",documentationUrl = "",i18nPackageName = "" )
public class ReadContentInputMeta extends BaseStepMeta implements StepMetaInterface {

	public  enum  TYPE_CODE  { word, pdf, text, excel , ppt};

	private String type = "word";

	private String contentFieldName = "content";

	// 是否一页/一段/一行作为一行数据
	private boolean isPageRow = false;

	private String[] fileNames;
	private String[] fileMasks;
	private String[] excludeFileMasks;
	private String[] fileRequireds;
	private String[] includeSubFolders;
	
	private String encoding = "UTF-8";

	private boolean acceptingFilenames = false;
	private String acceptingField;
	private String acceptingStepName;

	private boolean includeFileName = false;
	private boolean includeOnlyFileName = false;
	private String fileNameFieldName = "filename";
	
	private boolean addResultFile = false;
	
	private boolean ignoreError = false;
	
	//是否只保存可见字符(会除掉乱码,换行等信息)
	private boolean isOnlyVisible = true;

	/**
	 * 
	 */
	public ReadContentInputMeta() {
		super();
		setDefault();
	}

	@Override
	public void setDefault() {

		type = "word";
		contentFieldName = "content";

		fileNames = new String[0];
		fileMasks =  new String[0];
		excludeFileMasks =  new String[0];
		fileRequireds =  new String[0];
		includeSubFolders =  new String[0];

		isPageRow = false;
		encoding = "UTF-8" ;

		includeFileName = false;
		includeOnlyFileName = false ;
		fileNameFieldName = "filename";

		acceptingFilenames = false;
		acceptingField = null;
		acceptingStepName = null;
		
		addResultFile = false ;
		ignoreError = false ;
		isOnlyVisible=true;
	}

	@Override
	public Object clone() {

		ReadContentInputMeta retval = (ReadContentInputMeta) super.clone();
		if (fileNames != null) {
			retval.setFileNames(Arrays.copyOf(fileNames,fileNames.length));
			retval.setFileMasks(Arrays.copyOf(fileMasks,fileMasks.length));
			retval.setExcludeFileMasks(Arrays.copyOf(excludeFileMasks,excludeFileMasks.length));
			retval.setFileRequireds(Arrays.copyOf(fileRequireds,fileRequireds.length));
			retval.setIncludeSubFolders(Arrays.copyOf(includeSubFolders,includeSubFolders.length));
		}
		return retval;

	}

	@Override
	public void getFields(RowMetaInterface row, String name, RowMetaInterface[] info, StepMeta nextStep,
			VariableSpace space, Repository repository, IMetaStore metaStore) throws KettleStepException {

		if (!Utils.isEmpty(contentFieldName)) {
			ValueMetaInterface v = new ValueMetaString(space.environmentSubstitute(contentFieldName));
			v.setOrigin(name);
			row.addValueMeta(v);
		}

		if (includeFileName && !Utils.isEmpty(fileNameFieldName)) {
			ValueMetaInterface v = new ValueMetaString(space.environmentSubstitute(fileNameFieldName));
			v.setLength(100, -1);
			v.setOrigin(name);
			row.addValueMeta(v);
		}

	}

	@Override
	public String getXML() throws KettleException {
		StringBuilder retval = new StringBuilder(1024);

		retval.append("    ").append(XMLHandler.addTagValue("file_type", type));
		retval.append("    ").append(XMLHandler.addTagValue("contentFieldName", contentFieldName));
		retval.append("    ").append(XMLHandler.addTagValue("isPageRow", isPageRow));
		retval.append("    ").append(XMLHandler.addTagValue("includeFileName", includeFileName));
		retval.append("    ").append(XMLHandler.addTagValue("includeOnlyFileName", includeOnlyFileName));
		retval.append("    ").append(XMLHandler.addTagValue("fileNameFieldName", fileNameFieldName));
		retval.append("    ").append(XMLHandler.addTagValue("acceptingFilenames", acceptingFilenames));
		retval.append("    ").append(XMLHandler.addTagValue("acceptingField", acceptingField));
		retval.append("    ").append(XMLHandler.addTagValue("acceptingStepName", acceptingStepName));
		retval.append("    ").append(XMLHandler.addTagValue("addResultFile", addResultFile));
		retval.append("    ").append(XMLHandler.addTagValue("encoding", encoding));
		retval.append("    ").append(XMLHandler.addTagValue("ignoreError", ignoreError));
		retval.append("    ").append(XMLHandler.addTagValue("isOnlyVisible", isOnlyVisible));

		retval.append("    <file>").append(Const.CR);
		for (int i = 0; i < fileNames.length; i++) {
			retval.append("      ").append(XMLHandler.addTagValue("name", fileNames[i]));
			retval.append("      ").append(XMLHandler.addTagValue("filemask", fileMasks[i]));
			retval.append("      ").append(XMLHandler.addTagValue("exclude_filemask", excludeFileMasks[i]));
			retval.append("      ").append(XMLHandler.addTagValue("file_required", fileRequireds[i]));
			retval.append("      ").append(XMLHandler.addTagValue("include_subfolders", includeSubFolders[i]));
		}
		retval.append("    </file>").append(Const.CR);

		return retval.toString();
	}

	@Override
	public void loadXML(Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore) throws KettleXMLException {

		type = XMLHandler.getTagValue(stepnode, "file_type");
		contentFieldName = XMLHandler.getTagValue(stepnode, "contentFieldName");
		isPageRow = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "isPageRow"));
		includeFileName = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "includeFileName"));
		includeOnlyFileName = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "includeOnlyFileName"));
		fileNameFieldName = XMLHandler.getTagValue(stepnode, "fileNameFieldName");
		acceptingFilenames = "Y".equalsIgnoreCase(XMLHandler.getTagValue(stepnode, "acceptingFilenames"));
		acceptingField = XMLHandler.getTagValue(stepnode, "acceptingField");
		acceptingStepName = XMLHandler.getTagValue(stepnode, "acceptingStepName");
		addResultFile ="Y".equalsIgnoreCase( XMLHandler.getTagValue(stepnode, "addResultFile") );
		encoding = XMLHandler.getTagValue(stepnode, "encoding");
		ignoreError = "Y".equalsIgnoreCase( XMLHandler.getTagValue(stepnode, "ignoreError") );
		isOnlyVisible = "Y".equalsIgnoreCase( XMLHandler.getTagValue(stepnode, "isOnlyVisible") );

		Node filenode = XMLHandler.getSubNode(stepnode, "file");
		int nrfiles = XMLHandler.countNodes(filenode, "name");
		fileNames = new String[nrfiles];
		fileMasks =  new String[nrfiles];
		excludeFileMasks =  new String[nrfiles];
		fileRequireds =  new String[nrfiles];
		includeSubFolders =  new String[nrfiles];
		for (int i = 0; i < nrfiles; i++) {
			Node filenamenode = XMLHandler.getSubNodeByNr(filenode, "name", i);
			Node filemasknode = XMLHandler.getSubNodeByNr(filenode, "filemask", i);
			Node excludefilemasknode = XMLHandler.getSubNodeByNr(filenode, "exclude_filemask", i);
			Node fileRequirednode = XMLHandler.getSubNodeByNr(filenode, "file_required", i);
			Node includeSubFoldersnode = XMLHandler.getSubNodeByNr(filenode, "include_subfolders", i);
			fileNames[i] =XMLHandler.getNodeValue(filenamenode);
			fileMasks[i] =XMLHandler.getNodeValue(filemasknode);
			excludeFileMasks[i] =XMLHandler.getNodeValue(excludefilemasknode);
			fileRequireds[i] =XMLHandler.getNodeValue(fileRequirednode);
			includeSubFolders[i] =XMLHandler.getNodeValue(includeSubFoldersnode);
		}

	}

	public void readRep(Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases)
			throws KettleException {

		type = rep.getStepAttributeString(id_step, "file_type");
		contentFieldName = rep.getStepAttributeString(id_step, "contentFieldName");
		isPageRow = rep.getStepAttributeBoolean(id_step, "isPageRow");
		includeFileName = rep.getStepAttributeBoolean(id_step, "includeFileName");
		includeOnlyFileName = rep.getStepAttributeBoolean(id_step, "includeOnlyFileName");
		fileNameFieldName = rep.getStepAttributeString(id_step, "fileNameFieldName");
		acceptingFilenames = rep.getStepAttributeBoolean(id_step, "acceptingFilenames");
		acceptingField = rep.getStepAttributeString(id_step, "acceptingField");
		acceptingStepName = rep.getStepAttributeString(id_step, "acceptingStepName");
		addResultFile = rep.getStepAttributeBoolean(id_step, "addResultFile");
		encoding = rep.getStepAttributeString(id_step, "encoding");
		ignoreError = rep.getStepAttributeBoolean(id_step, "ignoreError");
		isOnlyVisible = rep.getStepAttributeBoolean(id_step, "isOnlyVisible");

		int nrfiles = rep.countNrStepAttributes(id_step, "file_name");
		fileNames =  new String[nrfiles];
		fileMasks =  new String[nrfiles];
		excludeFileMasks =  new String[nrfiles];
		fileRequireds =  new String[nrfiles];
		includeSubFolders =  new String[nrfiles];
		for (int i = 0; i < nrfiles; i++) {
			fileNames[i] =rep.getStepAttributeString(id_step, i, "file_name");
			fileMasks[i] =rep.getStepAttributeString(id_step, i, "file_mask");
			excludeFileMasks[i] =rep.getStepAttributeString(id_step, i, "exclude_file_mask");
			String fr = rep.getStepAttributeString(id_step, i, "file_required");
			if (!"Y".equalsIgnoreCase(fr)) {
				fileRequireds[i] ="N";
			} else {
				fileRequireds[i] ="Y";
			}
			String insf = rep.getStepAttributeString(id_step, i, "include_subfolders");
			if (!"Y".equalsIgnoreCase(insf)) {
				includeSubFolders[i] ="N";
			} else {
				includeSubFolders[i] ="Y";
			}
		}
	}

	@Override
	public void saveRep(Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step)
			throws KettleException {

		rep.saveStepAttribute(id_transformation, id_step, "file_type", type);
		rep.saveStepAttribute(id_transformation, id_step, "contentFieldName", contentFieldName);
		rep.saveStepAttribute(id_transformation, id_step, "isPageRow", isPageRow);
		rep.saveStepAttribute(id_transformation, id_step, "includeFileName", includeFileName);
		rep.saveStepAttribute(id_transformation, id_step, "includeOnlyFileName", includeOnlyFileName);
		rep.saveStepAttribute(id_transformation, id_step, "fileNameFieldName", fileNameFieldName);
		rep.saveStepAttribute(id_transformation, id_step, "acceptingFilenames", acceptingFilenames);
		rep.saveStepAttribute(id_transformation, id_step, "acceptingField", acceptingField);
		rep.saveStepAttribute(id_transformation, id_step, "acceptingStepName", acceptingStepName);
		rep.saveStepAttribute(id_transformation, id_step, "addResultFile", addResultFile);
		rep.saveStepAttribute(id_transformation, id_step, "encoding", encoding);
		rep.saveStepAttribute(id_transformation, id_step, "ignoreError", ignoreError);
		rep.saveStepAttribute(id_transformation, id_step, "isOnlyVisible", isOnlyVisible);

		for (int i = 0; i < fileNames.length; i++) {
			rep.saveStepAttribute(id_transformation, id_step, i, "file_name", Const.NVL(fileNames[i], ""));
			rep.saveStepAttribute(id_transformation, id_step, i, "file_mask", Const.NVL(fileMasks[i], ""));
			rep.saveStepAttribute(id_transformation, id_step, i, "exclude_file_mask",
					Const.NVL(excludeFileMasks[i], ""));
			rep.saveStepAttribute(id_transformation, id_step, i, "file_required", Const.NVL(fileRequireds[i], ""));
			rep.saveStepAttribute(id_transformation, id_step, i, "include_subfolders",
					Const.NVL(includeSubFolders[i], ""));
		}
	}

	@Override
	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev,
			String[] input, String[] output, RowMetaInterface info, VariableSpace space, Repository repository,
			IMetaStore metaStore) {
		// 为kettle UI dialog(org.pentaho.di.ui.spoon.dialog.CheckTransProgressDialog) 使用
		// 可以不实现
	}

	@Override
	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr,
			TransMeta transMeta, Trans trans) {
		return new ReadContentInput(stepMeta, stepDataInterface, copyNr, transMeta, trans);
	}

	@Override
	public StepDataInterface getStepData() {
		return new ReadContentInputData();
	}

	@Override
	public String exportResources(VariableSpace space, Map<String, ResourceDefinition> definitions,
			ResourceNamingInterface resourceNamingInterface, Repository repository, IMetaStore metaStore)
			throws KettleException {

		if (!acceptingFilenames) {

			// Replace the filename ONLY (folder or filename)
			//
			for (int i = 0; i < fileNames.length; i++) {
				FileObject fileObject = KettleVFS.getFileObject(space.environmentSubstitute(fileNames[i]), space);
				try {
					fileNames[i]= resourceNamingInterface.nameResource(fileObject, space, Utils.isEmpty(fileMasks[i]));
				} catch (FileSystemException e) {
					throw new KettleException(e);
				}
			}
		}

		return null;

	}

	public String[] getFilePaths(VariableSpace space) {
		return FileInputList.createFilePathList(space, getFileNames(),getFileMasks(), getExcludeFileMasks(),getFileRequireds(), getIncludeSubFolderArray());
	}

	public FileInputList getFileList(VariableSpace space) {
		return FileInputList.createFileList(space,getFileNames(),getFileMasks(), getExcludeFileMasks(),getFileRequireds(), getIncludeSubFolderArray());
	}

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

	public String[] getFileNames() {
		return fileNames;
	}

	public void setFileNames(String[] fileNames) {
		this.fileNames = fileNames;
	}

	public String[] getFileMasks() {
		return fileMasks;
	}

	public void setFileMasks(String[] fileMasks) {
		this.fileMasks = fileMasks;
	}

	public String[] getExcludeFileMasks() {
		return excludeFileMasks;
	}

	public void setExcludeFileMasks(String[] excludeFileMasks) {
		this.excludeFileMasks = excludeFileMasks;
	}

	public String[] getFileRequireds() {
		return fileRequireds;
	}

	public void setFileRequireds(String[] fileRequireds) {
		this.fileRequireds = fileRequireds;
	}

	public String[] getIncludeSubFolders() {
		return includeSubFolders;
	}

	public void setIncludeSubFolders(String[] includeSubFolders) {
		this.includeSubFolders = includeSubFolders;
	}

	public boolean[] getIncludeSubFolderArray() {

		boolean[] includeSubFolderBoolean = new boolean[fileRequireds.length];
		for (int i = 0; i < fileRequireds.length; i++) {
			includeSubFolderBoolean[i] = "Y".equalsIgnoreCase(fileRequireds[i]);
		}
		return includeSubFolderBoolean;

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

	public boolean isPageRow() {
		return isPageRow;
	}

	public void setPageRow(boolean isPageRow) {
		this.isPageRow = isPageRow;
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

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
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

}
