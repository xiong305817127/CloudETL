/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step.steps.input;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.row.value.ValueMetaBase;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.accessinput.AccessInputField;
import org.pentaho.di.trans.steps.accessinput.AccessInputMeta;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.quality.dto.step.parts.AccessInputAccessInputFieldDto;
import com.ys.idatrix.quality.dto.step.parts.TextFileInputFileDto;
import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.ext.utils.FilePathUtil;
import com.ys.idatrix.quality.ext.utils.FilePathUtil.FileType;
import com.ys.idatrix.quality.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.quality.recovery.trans.dto.StepLinesDto;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.quality.toolkit.domain.DataNode;
import com.ys.idatrix.quality.toolkit.domain.Relationship;
import com.ys.idatrix.quality.toolkit.utils.DataNodeUtil;
import com.ys.idatrix.quality.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - Access Input(Access输入). 转换
 * org.pentaho.di.trans.steps.accessinput.AccessInputMeta
 * 
 * @author XH
 * @since 2017-05-12
 *
 */
@Component("SPAccessInput")
@Scope("prototype")
public class SPAccessInput implements StepParameter, StepDataRelationshipParser,ResumeStepDataParser {

	String templeteFile;

	boolean includeFilename = false;
	String filenameField;
	boolean includeTablename = false;
	String dynamicFilenameField;
	String tablenameField;
	boolean includeRowNumber = false;
	boolean isaddresult=true;
	boolean filefield;
	String rowNumberField;
	boolean resetRowNumber = false;
	String TableName;
	long rowLimit;
	String shortFileFieldName = "short_filename";
	String pathFieldName;
	String hiddenFieldName;
	String lastModificationTimeFieldName;
	String uriNameFieldName;
	String rootUriNameFieldName;
	String extensionFieldName;
	String sizeFieldName;

	List<TextFileInputFileDto> fileName;
	List<AccessInputAccessInputFieldDto> inputFields;

	/**
	 * @return the templeteFile
	 */
	public String getTempleteFile() {
		return templeteFile;
	}

	/**
	 * @param 设置
	 *            templeteFile
	 */
	public void setTempleteFile(String templeteFile) {
		this.templeteFile = templeteFile;
	}

	/**
	 * @return includeFilename
	 */
	public boolean isIncludeFilename() {
		return includeFilename;
	}

	/**
	 * @param includeFilename
	 *            要设置的 includeFilename
	 */
	public void setIncludeFilename(boolean includeFilename) {
		this.includeFilename = includeFilename;
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
	 * @return includeTablename
	 */
	public boolean isIncludeTablename() {
		return includeTablename;
	}

	/**
	 * @param includeTablename
	 *            要设置的 includeTablename
	 */
	public void setIncludeTablename(boolean includeTablename) {
		this.includeTablename = includeTablename;
	}

	/**
	 * @return dynamicFilenameField
	 */
	public String getDynamicFilenameField() {
		return dynamicFilenameField;
	}

	/**
	 * @param dynamicFilenameField
	 *            要设置的 dynamicFilenameField
	 */
	public void setDynamicFilenameField(String dynamicFilenameField) {
		this.dynamicFilenameField = dynamicFilenameField;
	}

	/**
	 * @return tablenameField
	 */
	public String getTablenameField() {
		return tablenameField;
	}

	/**
	 * @param tablenameField
	 *            要设置的 tablenameField
	 */
	public void setTablenameField(String tablenameField) {
		this.tablenameField = tablenameField;
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
	 * @return isaddresult
	 */
	public boolean isIsaddresult() {
		return isaddresult;
	}

	/**
	 * @param isaddresult
	 *            要设置的 isaddresult
	 */
	public void setIsaddresult(boolean isaddresult) {
		this.isaddresult = isaddresult;
	}

	/**
	 * @return filefield
	 */
	public boolean isFilefield() {
		return filefield;
	}

	/**
	 * @param filefield
	 *            要设置的 filefield
	 */
	public void setFilefield(boolean filefield) {
		this.filefield = filefield;
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
	 * @return resetRowNumber
	 */
	public boolean isResetRowNumber() {
		return resetRowNumber;
	}

	/**
	 * @param resetRowNumber
	 *            要设置的 resetRowNumber
	 */
	public void setResetRowNumber(boolean resetRowNumber) {
		this.resetRowNumber = resetRowNumber;
	}

	/**
	 * @return tableName
	 */
	public String getTableName() {
		return TableName;
	}

	/**
	 * @param tableName
	 *            要设置的 tableName
	 */
	public void setTableName(String tableName) {
		TableName = tableName;
	}

	/**
	 * @return rowLimit
	 */
	public long getRowLimit() {
		return rowLimit;
	}

	/**
	 * @param rowLimit
	 *            要设置的 rowLimit
	 */
	public void setRowLimit(long rowLimit) {
		this.rowLimit = rowLimit;
	}

	/**
	 * @return shortFileFieldName
	 */
	public String getShortFileFieldName() {
		return shortFileFieldName;
	}

	/**
	 * @param shortFileFieldName
	 *            要设置的 shortFileFieldName
	 */
	public void setShortFileFieldName(String shortFileFieldName) {
		this.shortFileFieldName = shortFileFieldName;
	}

	/**
	 * @return pathFieldName
	 */
	public String getPathFieldName() {
		return pathFieldName;
	}

	/**
	 * @param pathFieldName
	 *            要设置的 pathFieldName
	 */
	public void setPathFieldName(String pathFieldName) {
		this.pathFieldName = pathFieldName;
	}

	/**
	 * @return hiddenFieldName
	 */
	public String getHiddenFieldName() {
		return hiddenFieldName;
	}

	/**
	 * @param hiddenFieldName
	 *            要设置的 hiddenFieldName
	 */
	public void setHiddenFieldName(String hiddenFieldName) {
		this.hiddenFieldName = hiddenFieldName;
	}

	/**
	 * @return lastModificationTimeFieldName
	 */
	public String getLastModificationTimeFieldName() {
		return lastModificationTimeFieldName;
	}

	/**
	 * @param lastModificationTimeFieldName
	 *            要设置的 lastModificationTimeFieldName
	 */
	public void setLastModificationTimeFieldName(String lastModificationTimeFieldName) {
		this.lastModificationTimeFieldName = lastModificationTimeFieldName;
	}

	/**
	 * @return uriNameFieldName
	 */
	public String getUriNameFieldName() {
		return uriNameFieldName;
	}

	/**
	 * @param uriNameFieldName
	 *            要设置的 uriNameFieldName
	 */
	public void setUriNameFieldName(String uriNameFieldName) {
		this.uriNameFieldName = uriNameFieldName;
	}

	/**
	 * @return rootUriNameFieldName
	 */
	public String getRootUriNameFieldName() {
		return rootUriNameFieldName;
	}

	/**
	 * @param rootUriNameFieldName
	 *            要设置的 rootUriNameFieldName
	 */
	public void setRootUriNameFieldName(String rootUriNameFieldName) {
		this.rootUriNameFieldName = rootUriNameFieldName;
	}

	/**
	 * @return extensionFieldName
	 */
	public String getExtensionFieldName() {
		return extensionFieldName;
	}

	/**
	 * @param extensionFieldName
	 *            要设置的 extensionFieldName
	 */
	public void setExtensionFieldName(String extensionFieldName) {
		this.extensionFieldName = extensionFieldName;
	}

	/**
	 * @return sizeFieldName
	 */
	public String getSizeFieldName() {
		return sizeFieldName;
	}

	/**
	 * @param sizeFieldName
	 *            要设置的 sizeFieldName
	 */
	public void setSizeFieldName(String sizeFieldName) {
		this.sizeFieldName = sizeFieldName;
	}

	/**
	 * @return fileName
	 */
	public List<TextFileInputFileDto> getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            要设置的 fileName
	 */
	public void setFileName(List<TextFileInputFileDto> fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return inputFields
	 */
	public List<AccessInputAccessInputFieldDto> getInputFields() {
		return inputFields;
	}

	/**
	 * @param inputFields
	 *            要设置的 inputFields
	 */
	public void setInputFields(List<AccessInputAccessInputFieldDto> inputFields) {
		this.inputFields = inputFields;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);

		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("inputFields", AccessInputAccessInputFieldDto.class);
		classMap.put("fileName", TextFileInputFileDto.class);

		return (SPAccessInput) JSONObject.toBean(jsonObj, SPAccessInput.class, classMap);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPAccessInput spAccessInput = new SPAccessInput();
		AccessInputMeta accessinputmeta = (AccessInputMeta) stepMetaInterface;

		spAccessInput.setDynamicFilenameField(accessinputmeta.getDynamicFilenameField());
		spAccessInput.setRowNumberField(accessinputmeta.getRowNumberField());
		spAccessInput.setTablenameField(accessinputmeta.getTablenameField());
		spAccessInput.setFilenameField(accessinputmeta.getFilenameField());
		spAccessInput.setRowLimit(accessinputmeta.getRowLimit());
		spAccessInput.setTableName(accessinputmeta.getTableName());
		spAccessInput.setIncludeFilename(accessinputmeta.isIncludeFilename());
		spAccessInput.setIncludeTablename(accessinputmeta.isIncludeTablename());
		spAccessInput.setIncludeRowNumber(accessinputmeta.isIncludeRowNumber());
		spAccessInput.setFilefield(accessinputmeta.isFileField());
		spAccessInput.setResetRowNumber(accessinputmeta.isResetRowNumber());

		spAccessInput.setIsaddresult(accessinputmeta.isAddResultFile());
		spAccessInput.setShortFileFieldName(accessinputmeta.getShortFileNameField());
		spAccessInput.setPathFieldName(accessinputmeta.getPathField());
		spAccessInput.setHiddenFieldName(accessinputmeta.isHiddenField());
		spAccessInput.setLastModificationTimeFieldName(accessinputmeta.getLastModificationDateField());
		spAccessInput.setUriNameFieldName(accessinputmeta.getUriField());
		spAccessInput.setRootUriNameFieldName(accessinputmeta.getRootUriField());
		spAccessInput.setExtensionFieldName(accessinputmeta.getExtensionField());
		spAccessInput.setSizeFieldName(accessinputmeta.getSizeField());

		if( accessinputmeta.getFileName() != null &&  accessinputmeta.getFileName().length >0 ) {
			List<TextFileInputFileDto> fileNameList = Lists.newArrayList();
			String[] fileNames = accessinputmeta.getFileName();
			String[] fileMask = accessinputmeta.getFileMask();
			String[] excludeFileMask = accessinputmeta.getExcludeFileMask();
			String[] fileRequired = accessinputmeta.getFileRequired();
			String[] includeSubFolders = accessinputmeta.getIncludeSubFolders();
			for (int i = 0; i < fileNames.length; i++) {
				TextFileInputFileDto aifnd = new TextFileInputFileDto();
				aifnd.setFileName(FilePathUtil.getRelativeFileName(null, fileNames[i], FileType.input));
				aifnd.setFileMask(fileMask[i]);
				aifnd.setExcludeFileMask(excludeFileMask[i]);
				aifnd.setFileRequired(fileRequired[i]);
				aifnd.setIncludeSubFolders(includeSubFolders[i]);

				fileNameList.add(aifnd);
			}
			spAccessInput.setFileName(fileNameList);
		}
		
		if(accessinputmeta.getInputFields() != null && accessinputmeta.getInputFields().length >0){
			AccessInputField[] inputFieldsArray = accessinputmeta.getInputFields();
			List<AccessInputAccessInputFieldDto> inputFieldsList = Arrays.asList(inputFieldsArray).stream()
					.map(accessInputField -> {
						AccessInputAccessInputFieldDto aiaifd = new AccessInputAccessInputFieldDto();

						aiaifd.setLength(accessInputField.getLength());
						aiaifd.setName(accessInputField.getName());
						aiaifd.setTrimType(ValueMetaBase.getTrimTypeCode(accessInputField.getTrimType()));
						aiaifd.setColumn(accessInputField.getColumn());
						aiaifd.setTypedesc(accessInputField.getType());
						aiaifd.setFormat(accessInputField.getFormat());
						aiaifd.setGroupsymbol(accessInputField.getGroupSymbol());
						aiaifd.setDecimalsymbol(accessInputField.getDecimalSymbol());
						aiaifd.setPrecision(accessInputField.getPrecision());
						aiaifd.setCurrencysymbol(accessInputField.getCurrencySymbol());
						aiaifd.setRepeated(accessInputField.isRepeated());

						return aiaifd;
					}).collect(Collectors.toList());
			spAccessInput.setInputFields(inputFieldsList);
		}

		return spAccessInput;

	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPAccessInput spAccessInput = (SPAccessInput) po;
		AccessInputMeta accessinputmeta = (AccessInputMeta) stepMetaInterface;

		accessinputmeta.setFilenameField(spAccessInput.getFilenameField());
		accessinputmeta.setFileField(spAccessInput.isFilefield());
		accessinputmeta.setRowLimit(spAccessInput.getRowLimit());
		accessinputmeta.setTableName(spAccessInput.getTableName());
		accessinputmeta.setDynamicFilenameField(spAccessInput.getDynamicFilenameField());
		accessinputmeta.setIncludeFilename(spAccessInput.isIncludeFilename());
		accessinputmeta.setIncludeTablename(spAccessInput.isIncludeTablename());
		accessinputmeta.setIncludeRowNumber(spAccessInput.isIncludeRowNumber());
		accessinputmeta.setResetRowNumber(spAccessInput.isResetRowNumber());
		accessinputmeta.setRowNumberField(spAccessInput.getRowNumberField());
		accessinputmeta.setTablenameField(spAccessInput.getTablenameField());

		accessinputmeta.setAddResultFile(spAccessInput.isIsaddresult());
		accessinputmeta.setShortFileNameField(spAccessInput.getShortFileFieldName());
		accessinputmeta.setPathField(spAccessInput.getPathFieldName());
		accessinputmeta.setHiddenField(spAccessInput.getHiddenFieldName());
		accessinputmeta.setLastModificationDateField(spAccessInput.getLastModificationTimeFieldName());
		accessinputmeta.setUriField(spAccessInput.getUriNameFieldName());
		accessinputmeta.setRootUriField(spAccessInput.getRootUriNameFieldName());
		accessinputmeta.setExtensionField(spAccessInput.getExtensionFieldName());
		accessinputmeta.setSizeField(spAccessInput.getSizeFieldName());

		if(spAccessInput.getFileName() != null && spAccessInput.getFileName().size()>0) {
			String fileNames[] = new String[spAccessInput.getFileName().size()];
			String fileMask[] = new String[spAccessInput.getFileName().size()];
			String excludeFileMask[] = new String[spAccessInput.getFileName().size()];
			String fileRequired[] = new String[spAccessInput.getFileName().size()];
			String includeSubFolders[] = new String[spAccessInput.getFileName().size()];
			for (int i = 0; i < spAccessInput.getFileName().size(); i++) {
				TextFileInputFileDto aifnd = spAccessInput.getFileName().get(i);
				fileNames[i] = FilePathUtil.getRealFileName(null,aifnd.getFileName(),FileType.input);
				fileMask[i] = aifnd.getFileMask();
				excludeFileMask[i] = aifnd.getExcludeFileMask();
				fileRequired[i] = aifnd.getFileRequired();
				includeSubFolders[i] = aifnd.getIncludeSubFolders();
			}
			accessinputmeta.setFileName(fileNames);
			accessinputmeta.setFileMask(fileMask);
			accessinputmeta.setExcludeFileMask(excludeFileMask);
			accessinputmeta.setFileRequired(fileRequired);
			accessinputmeta.setIncludeSubFolders(includeSubFolders);
		}
	
		if(spAccessInput.getInputFields() != null && spAccessInput.getInputFields().size() >0) {
			List<AccessInputField> accessInputFieldList = spAccessInput.getInputFields().stream().map(aiaifd -> {
				AccessInputField accessInputField = new AccessInputField();

				accessInputField.setLength(aiaifd.getLength());
				accessInputField.setName(aiaifd.getName());
				accessInputField.setTrimType(ValueMetaBase.getTrimTypeByCode(aiaifd.getTrimType()));
				accessInputField.setColumn(aiaifd.getColumn());
				accessInputField.setType(aiaifd.getTypedesc());
				accessInputField.setFormat(aiaifd.getFormat());
				accessInputField.setGroupSymbol(aiaifd.getGroupsymbol());
				accessInputField.setDecimalSymbol(aiaifd.getDecimalsymbol());
				accessInputField.setPrecision(aiaifd.getPrecision());
				accessInputField.setCurrencySymbol(aiaifd.getCurrencysymbol());
				accessInputField.setRepeated(aiaifd.isRepeated());

				return accessInputField;
			}).collect(Collectors.toList());
			accessinputmeta.setInputFields( accessInputFieldList.toArray(new AccessInputField[spAccessInput.getInputFields().size()]));
		}

	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {

		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		AccessInputMeta accessInputMeta = (AccessInputMeta) stepMetaInterface;

		String[] fileNames = accessInputMeta.getFileName();
		for (String name : fileNames) {
			if (StringUtils.isNotEmpty(name)) {
				
				DataNode fileDataNode = DataNodeUtil.fileNodeParse("Access", name.trim(), "", "", "" ) ;
				sdr.addInputDataNode(fileDataNode);
				
				// 增加 系统节点 和 流节点的关系
				String from= "转换:"+transMeta.getName()+",步骤:"+stepMeta.getName();
				List<Relationship> relationships = RelationshipUtil.inputStepRelationship(null, fileDataNode, sdr.getOutputStream(), stepMeta.getName(), from);
				sdr.getDataRelationship().addAll(relationships);
			}
		}
	}
	
	@Override
	 public boolean waitPut(StepLinesDto linesDto,List<StepMeta> nextStepMeta ,StepMeta curStepMeta ,StepMetaInterface stepMetaInterface,StepDataInterface stepDataInterface , StepInterface stepInterface)  throws Exception  {
		//access文件输入  可能是 多文件 输入,无法定位行数,游标无法定位,使用暴力忽略
		waitPutRowData(stepInterface, linesDto.getRowLine());
		return true;
	}
	
	@Override
	public int stepType() {
		return 1;
	}

}
