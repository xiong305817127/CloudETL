/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step.steps.input;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.fileinput.FileInputList;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.getfilenames.GetFileNamesMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.quality.dto.step.parts.TextFileInputFileDto;
import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.ext.utils.FilePathUtil;
import com.ys.idatrix.quality.ext.utils.FilePathUtil.FileType;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.quality.toolkit.domain.DataNode;
import com.ys.idatrix.quality.toolkit.domain.Relationship;
import com.ys.idatrix.quality.toolkit.utils.DataNodeUtil;
import com.ys.idatrix.quality.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - Get File Names(获取文件名). 转换
 * org.pentaho.di.trans.steps.getfilenames.GetFileNamesMeta
 * 
 * @author XH
 * @since 05-12-2017
 *
 */
@Component("SPGetFileNames")
@Scope("prototype")
public class SPGetFileNames implements StepParameter, StepDataRelationshipParser {

	String filterFileType = "all_files";
	boolean doNotFailIfNoFile = false;
	boolean includeRowNumber = false;
	boolean isAddResult = true;
	boolean isFileField = false;
	String rowNumberField;
	String filenameField;
	String wildcardField;
	String excludeWildcardField;
	boolean isIncludeSubfolders;
	long limit;
	List<TextFileInputFileDto> selectedFiles;

	/**
	 * @return filterFileType
	 */
	public String getFilterFileType() {
		return filterFileType;
	}

	/**
	 * @param filterFileType
	 *            要设置的 filterFileType
	 */
	public void setFilterFileType(String filterFileType) {
		this.filterFileType = filterFileType;
	}

	/**
	 * @return doNotFailIfNoFile
	 */
	public boolean isDoNotFailIfNoFile() {
		return doNotFailIfNoFile;
	}

	/**
	 * @param doNotFailIfNoFile
	 *            要设置的 doNotFailIfNoFile
	 */
	public void setDoNotFailIfNoFile(boolean doNotFailIfNoFile) {
		this.doNotFailIfNoFile = doNotFailIfNoFile;
	}

	/**
	 * @return includeRowNumber
	 */
	public boolean isIncludeRowNumber() {
		return includeRowNumber;
	}

	/**
	 * @param includeRowNumber
	 *            要设置的 includeRowNumber
	 */
	public void setIncludeRowNumber(boolean includeRowNumber) {
		this.includeRowNumber = includeRowNumber;
	}

	/**
	 * @return isAddResult
	 */
	public boolean isAddResult() {
		return isAddResult;
	}

	/**
	 * @param isAddResult
	 *            要设置的 isAddResult
	 */
	public void setAddResult(boolean isAddResult) {
		this.isAddResult = isAddResult;
	}

	/**
	 * @return isFileField
	 */
	public boolean isFileField() {
		return isFileField;
	}

	/**
	 * @param isFileField
	 *            要设置的 isFileField
	 */
	public void setFileField(boolean isFileField) {
		this.isFileField = isFileField;
	}

	/**
	 * @return rowNumberField
	 */
	public String getRowNumberField() {
		return rowNumberField;
	}

	/**
	 * @param rowNumberField
	 *            要设置的 rowNumberField
	 */
	public void setRowNumberField(String rowNumberField) {
		this.rowNumberField = rowNumberField;
	}

	/**
	 * @return filenameField
	 */
	public String getFilenameField() {
		return filenameField;
	}

	/**
	 * @param filenameField
	 *            要设置的 filenameField
	 */
	public void setFilenameField(String filenameField) {
		this.filenameField = filenameField;
	}

	/**
	 * @return wildcardField
	 */
	public String getWildcardField() {
		return wildcardField;
	}

	/**
	 * @param wildcardField
	 *            要设置的 wildcardField
	 */
	public void setWildcardField(String wildcardField) {
		this.wildcardField = wildcardField;
	}

	/**
	 * @return excludeWildcardField
	 */
	public String getExcludeWildcardField() {
		return excludeWildcardField;
	}

	/**
	 * @param excludeWildcardField
	 *            要设置的 excludeWildcardField
	 */
	public void setExcludeWildcardField(String excludeWildcardField) {
		this.excludeWildcardField = excludeWildcardField;
	}

	/**
	 * @return isIncludeSubfolders
	 */
	public boolean isIncludeSubfolders() {
		return isIncludeSubfolders;
	}

	/**
	 * @param isIncludeSubfolders
	 *            要设置的 isIncludeSubfolders
	 */
	public void setIncludeSubfolders(boolean isIncludeSubfolders) {
		this.isIncludeSubfolders = isIncludeSubfolders;
	}

	/**
	 * @return limit
	 */
	public long getLimit() {
		return limit;
	}

	/**
	 * @param limit
	 *            要设置的 limit
	 */
	public void setLimit(long limit) {
		this.limit = limit;
	}

	/**
	 * @return selectedFiles
	 */
	public List<TextFileInputFileDto> getSelectedFiles() {
		return selectedFiles;
	}

	/**
	 * @param selectedFiles
	 *            要设置的 selectedFiles
	 */
	public void setSelectedFiles(List<TextFileInputFileDto> selectedFiles) {
		this.selectedFiles = selectedFiles;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);

		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("selectedFiles", TextFileInputFileDto.class);

		return (SPGetFileNames) JSONObject.toBean(jsonObj, SPGetFileNames.class, classMap);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPGetFileNames spGetFileNames = new SPGetFileNames();
		GetFileNamesMeta getfilenamesmeta = (GetFileNamesMeta) stepMetaInterface;

		List<TextFileInputFileDto> fileNameList = Lists.newArrayList();
		String[] fileNames = getfilenamesmeta.getFileName();
		String[] fileMasks = getfilenamesmeta.getFileMask();
		String[] excludeFileMasks = getfilenamesmeta.getExcludeFileMask();
		String[] fileRequireds = getfilenamesmeta.getFileRequired();
		String[] includeSubFolderss = getfilenamesmeta.getIncludeSubFolders();
		for (int i = 0; i < fileNames.length; i++) {
			TextFileInputFileDto getfilenamesfilenamedto = new TextFileInputFileDto();
			getfilenamesfilenamedto.setFileName(FilePathUtil.getRelativeFileName(null,fileNames[i], FileType.input));
			getfilenamesfilenamedto.setFileMask(fileMasks[i]);
			getfilenamesfilenamedto.setExcludeFileMask(excludeFileMasks[i]);
			getfilenamesfilenamedto.setFileRequired(fileRequireds[i]);
			getfilenamesfilenamedto.setIncludeSubFolders(includeSubFolderss[i]);
			fileNameList.add(getfilenamesfilenamedto);
		}
		spGetFileNames.setSelectedFiles(fileNameList);
		spGetFileNames.setRowNumberField(getfilenamesmeta.getRowNumberField());
		spGetFileNames.setFilenameField(getfilenamesmeta.getDynamicFilenameField());
		spGetFileNames.setWildcardField(getfilenamesmeta.getDynamicWildcardField());
		spGetFileNames.setExcludeWildcardField(getfilenamesmeta.getDynamicExcludeWildcardField());
		spGetFileNames.setFilterFileType(getfilenamesmeta.getFileTypeFilter().toString());
		spGetFileNames.setLimit(getfilenamesmeta.getRowLimit());
		spGetFileNames.setDoNotFailIfNoFile(getfilenamesmeta.isdoNotFailIfNoFile());
		spGetFileNames.setIncludeSubfolders(getfilenamesmeta.isDynamicIncludeSubFolders());
		spGetFileNames.setFileField(getfilenamesmeta.isFileField());

		spGetFileNames.setIncludeRowNumber(getfilenamesmeta.includeRowNumber());
		spGetFileNames.setAddResult(getfilenamesmeta.isAddResultFile());

		return spGetFileNames;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPGetFileNames spGetFileNames = (SPGetFileNames) po;
		GetFileNamesMeta getfilenamesmeta = (GetFileNamesMeta) stepMetaInterface;

		getfilenamesmeta.setdoNotFailIfNoFile(spGetFileNames.isDoNotFailIfNoFile());
		getfilenamesmeta.setDynamicFilenameField(spGetFileNames.getFilenameField());
		getfilenamesmeta.setDynamicWildcardField(spGetFileNames.getWildcardField());
		getfilenamesmeta.setRowNumberField(spGetFileNames.getRowNumberField());
		getfilenamesmeta.setDynamicExcludeWildcardField(spGetFileNames.getExcludeWildcardField());
		getfilenamesmeta.setDynamicIncludeSubFolders(spGetFileNames.isIncludeSubfolders());
		getfilenamesmeta.setIncludeRowNumber(spGetFileNames.isIncludeRowNumber());
		getfilenamesmeta.setFileField(spGetFileNames.isFileField());
		String[] fileNames = new String[spGetFileNames.getSelectedFiles().size()];
		String[] fileMasks = new String[spGetFileNames.getSelectedFiles().size()];
		String[] excludeFileMasks = new String[spGetFileNames.getSelectedFiles().size()];
		String[] fileRequireds = new String[spGetFileNames.getSelectedFiles().size()];
		String[] includeSubFolderss = new String[spGetFileNames.getSelectedFiles().size()];
		for (int i = 0; i < spGetFileNames.getSelectedFiles().size(); i++) {
			TextFileInputFileDto getfilenamesfilenamedto = spGetFileNames.getSelectedFiles().get(i);
			fileNames[i] = FilePathUtil.getRealFileName(null,getfilenamesfilenamedto.getFileName(), FileType.input);
			fileMasks[i] = getfilenamesfilenamedto.getFileMask();
			excludeFileMasks[i] = getfilenamesfilenamedto.getExcludeFileMask();
			fileRequireds[i] = getfilenamesfilenamedto.getFileRequired();
			includeSubFolderss[i] = getfilenamesfilenamedto.getIncludeSubFolders();
		}
		getfilenamesmeta.setFileName(fileNames);
		getfilenamesmeta.setFileMask(fileMasks);
		getfilenamesmeta.setExcludeFileMask(excludeFileMasks);
		getfilenamesmeta.setFileRequired(fileRequireds);
		getfilenamesmeta.setIncludeSubFolders(includeSubFolderss);
		getfilenamesmeta.setRowLimit(spGetFileNames.getLimit());

		getfilenamesmeta.setFilterFileType(FileInputList.FileTypeFilter.getByName(spGetFileNames.getFilterFileType()));
		getfilenamesmeta.setAddResultFile(spGetFileNames.isAddResult());

	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {

		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		GetFileNamesMeta getFileNamesMeta = (GetFileNamesMeta) stepMetaInterface;

		String[] fileNames = getFileNamesMeta.getFileName();
		for (String name : fileNames) {
			if (!Utils.isEmpty(name)) {
				
				DataNode fileDataNode = DataNodeUtil.fileNodeParse("File", name.trim(), "", "", "" ) ;
				sdr.addInputDataNode(fileDataNode);
				
				// 增加 系统节点 和 流节点的关系
				String from = "转换:" + transMeta.getName() + ",步骤:" + stepMeta.getName();
				List<Relationship> relationships = RelationshipUtil.inputStepRelationship(null, fileDataNode, sdr.getOutputStream(), stepMeta.getName(), from);
				sdr.getDataRelationship().addAll(relationships);
			}
		}


	}

}
