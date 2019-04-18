package com.ys.idatrix.quality.dto.step.steps.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.row.value.ValueMetaBase;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.quality.dto.step.parts.AdditionalOutputFieldsDto;
import com.ys.idatrix.quality.dto.step.parts.JsonInputFieldDto;
import com.ys.idatrix.quality.dto.step.parts.TextFileInputFileDto;
import com.ys.idatrix.quality.dto.step.steps.StepParameter;
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
 * Step - JsonInput(json输入). 转换 org.pentaho.di.trans.steps.jsoninput.JsonInputMeta
 * 
 * @author FBZ
 * @since 11-28-2017
 */
@Component("SPJsonInput")
@Scope("prototype")
public class SPJsonInput implements StepParameter, StepDataRelationshipParser ,ResumeStepDataParser {
	// 文件 start
	/** Is In fields, 源定义在一个字段里? */
	private boolean inFields;

	/** Is In fields, 从字段获取源 */
	private String valueField;

	/** Is a File, 源是一个文件名? */
	private boolean afile;

	/** Flag : read url as source; 以Url获取源? */
	private boolean readurl;

	/** Do not pass field downstream */
	private boolean removeSourceField;

	/** 选中的文件 */
	private List<TextFileInputFileDto> fileName;
	// 文件 end

	// 内容 start
	/** Flag : do we ignore empty files ; 忽然空文件 */
	private boolean isIgnoreEmptyFile;

	/** Flag : do not fail if no file; 如果没有文件不进行报错 */
	private boolean doNotFailIfNoFile;

	/** 忽略不完整的路径 */
	private boolean ignoreMissingPath;

	/** The maximum number or lines to read; 限制 */
	private String rowLimit;

	/**
	 * Flag indicating that we should include the filename in the output; 在输出中包含文件名
	 */
	private boolean includeFilename; // InputFiles.isaddresult?..

	/**
	 * The name of the field in the output containing the filename; 包含文件名的字段名
	 */
	private String filenameField;

	/**
	 * Flag indicating that a row number field should be included in the output;
	 * 在输出中包括行数
	 */
	private boolean includeRowNumber;

	/**
	 * The name of the field in the output containing the row number; 包含行数的字段名
	 */
	private String rowNumberField;

	/** Flag: add result filename; 添加文件名 **/
	private boolean addResultFile;
	// 内容 end

	// 字段
	private List<JsonInputFieldDto> inputFields;

	// 其他输出字段
	private AdditionalOutputFieldsDto additionalOutputFields;

	public boolean isInFields() {
		return inFields;
	}

	public void setInFields(boolean inFields) {
		this.inFields = inFields;
	}

	public String getValueField() {
		return valueField;
	}

	public void setValueField(String valueField) {
		this.valueField = valueField;
	}

	public boolean isAfile() {
		return afile;
	}

	public void setAfile(boolean afile) {
		this.afile = afile;
	}

	public boolean isReadurl() {
		return readurl;
	}

	public void setReadurl(boolean readurl) {
		this.readurl = readurl;
	}

	public boolean isRemoveSourceField() {
		return removeSourceField;
	}

	public void setRemoveSourceField(boolean removeSourceField) {
		this.removeSourceField = removeSourceField;
	}

	public List<TextFileInputFileDto> getFileName() {
		return fileName;
	}

	public void setFileName(List<TextFileInputFileDto> fileName) {
		this.fileName = fileName;
	}

	public boolean isIgnoreEmptyFile() {
		return isIgnoreEmptyFile;
	}

	public void setIgnoreEmptyFile(boolean isIgnoreEmptyFile) {
		this.isIgnoreEmptyFile = isIgnoreEmptyFile;
	}

	public boolean isDoNotFailIfNoFile() {
		return doNotFailIfNoFile;
	}

	public void setDoNotFailIfNoFile(boolean doNotFailIfNoFile) {
		this.doNotFailIfNoFile = doNotFailIfNoFile;
	}

	public boolean isIgnoreMissingPath() {
		return ignoreMissingPath;
	}

	public void setIgnoreMissingPath(boolean ignoreMissingPath) {
		this.ignoreMissingPath = ignoreMissingPath;
	}

	public String getRowLimit() {
		return rowLimit;
	}

	public void setRowLimit(String rowLimit) {
		this.rowLimit = rowLimit;
	}

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

	public String getRowNumberField() {
		return rowNumberField;
	}

	public void setRowNumberField(String rowNumberField) {
		this.rowNumberField = rowNumberField;
	}

	public boolean isAddResultFile() {
		return addResultFile;
	}

	public void setAddResultFile(boolean addResultFile) {
		this.addResultFile = addResultFile;
	}

	public AdditionalOutputFieldsDto getAdditionalOutputFields() {
		return additionalOutputFields;
	}

	public void setAdditionalOutputFields(AdditionalOutputFieldsDto additionalOutputFields) {
		this.additionalOutputFields = additionalOutputFields;
	}

	public List<JsonInputFieldDto> getInputFields() {
		return inputFields;
	}

	public void setInputFields(List<JsonInputFieldDto> inputFields) {
		this.inputFields = inputFields;
	}

	/*
	 * Parser JSON object as a step parameter object.
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);

		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("inputFields", JsonInputFieldDto.class);
		classMap.put("fileName", TextFileInputFileDto.class);
		classMap.put("additionalOutputFields", AdditionalOutputFieldsDto.class);

		return (SPJsonInput) JSONObject.toBean(jsonObj, SPJsonInput.class, classMap);
	}

	/*
	 * Encode a step parameter object from step meta.
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepM = stepMeta.getStepMetaInterface();
		SPJsonInput jsonBean = new SPJsonInput();

		String[] fileName = (String[]) OsgiBundleUtils.invokeOsgiMethod(stepM, "getFileName"); // jsonInputMeta.getFileName()

		if (fileName != null) {

			TextFileInputFileDto textFileInputFileDto;
			jsonBean.setFileName(new ArrayList<TextFileInputFileDto>(fileName.length));

			String[] fileMask = (String[]) OsgiBundleUtils.invokeOsgiMethod(stepM, "getFileMask"); // jsonInputMeta.getFileMask()
			String[] excludeFM = (String[]) OsgiBundleUtils.invokeOsgiMethod(stepM, "getExcludeFileMask"); // jsonInputMeta.getExcludeFileMask()
			String[] fileRequired = (String[]) OsgiBundleUtils.invokeOsgiMethod(stepM, "getFileRequired"); // jsonInputMeta.getFileRequired()
			String[] includeSF = (String[]) OsgiBundleUtils.invokeOsgiMethod(stepM, "getIncludeSubFolders"); // jsonInputMeta.getIncludeSubFolders()
			for (int i = 0; i < fileName.length; i++) {
				textFileInputFileDto = new TextFileInputFileDto();
				jsonBean.getFileName().add(textFileInputFileDto);

				textFileInputFileDto.setFileName(fileName[i]);
				textFileInputFileDto.setFileMask(fileMask[i]);
				textFileInputFileDto.setExcludeFileMask(excludeFM[i]);
				textFileInputFileDto.setFileRequired(fileRequired[i]);
				textFileInputFileDto.setIncludeSubFolders(includeSF[i]);
			}
		}

		jsonBean.setIncludeFilename((boolean) OsgiBundleUtils.invokeOsgiMethod(stepM, "includeFilename")); // jsonInputMeta.includeFilename()
		jsonBean.setIncludeRowNumber((boolean) OsgiBundleUtils.invokeOsgiMethod(stepM, "includeRowNumber")); // jsonInputMeta.includeRowNumber()
		jsonBean.setAddResultFile((boolean) OsgiBundleUtils.invokeOsgiMethod(stepM, "addResultFile")); // jsonInputMeta.addResultFile()
		jsonBean.setReadurl((boolean) OsgiBundleUtils.invokeOsgiMethod(stepM, "isReadUrl")); // jsonInputMeta.isReadUrl()
		jsonBean.setIgnoreEmptyFile((boolean) OsgiBundleUtils.invokeOsgiMethod(stepM, "isIgnoreEmptyFile")); // jsonInputMeta.isIgnoreEmptyFile()
		jsonBean.setDoNotFailIfNoFile((boolean) OsgiBundleUtils.invokeOsgiMethod(stepM, "isDoNotFailIfNoFile")); // jsonInputMeta.isDoNotFailIfNoFile()
		jsonBean.setIgnoreMissingPath((boolean) OsgiBundleUtils.invokeOsgiMethod(stepM, "isIgnoreMissingPath")); // jsonInputMeta.isIgnoreMissingPath()
		jsonBean.setRemoveSourceField((boolean) OsgiBundleUtils.invokeOsgiMethod(stepM, "isRemoveSourceField")); // jsonInputMeta.isRemoveSourceField()
		jsonBean.setInFields((boolean) OsgiBundleUtils.invokeOsgiMethod(stepM, "isInFields")); // jsonInputMeta.isInFields()
		jsonBean.setAfile((boolean) OsgiBundleUtils.invokeOsgiMethod(stepM, "getIsAFile")); // jsonInputMeta.getIsAFile()

		jsonBean.setValueField((String) OsgiBundleUtils.invokeOsgiMethod(stepM, "getFieldValue")); // jsonInputMeta.getFieldValue()
		jsonBean.setFilenameField((String) OsgiBundleUtils.invokeOsgiMethod(stepM, "getFilenameField")); // jsonInputMeta.getFilenameField()
		jsonBean.setRowNumberField((String) OsgiBundleUtils.invokeOsgiMethod(stepM, "getRowNumberField")); // jsonInputMeta.getRowNumberField()
		jsonBean.setRowLimit("" + (long) OsgiBundleUtils.invokeOsgiMethod(stepM, "getRowLimit")); // jsonInputMeta.getRowLimit()

		Object[] inputFieldDatas = (Object[]) OsgiBundleUtils.invokeOsgiMethod(stepM, "getInputFields"); // jsonInputMeta.getInputFields()

		int inputFieldsLength = null == inputFieldDatas ? 0 : inputFieldDatas.length;
		jsonBean.setInputFields(new ArrayList<JsonInputFieldDto>(inputFieldsLength));
		JsonInputFieldDto jsonInputFieldDto;
		for (int i = 0; i < inputFieldsLength; i++) {
			Object field = inputFieldDatas[i];

			if (field != null) {
				jsonInputFieldDto = new JsonInputFieldDto();
				jsonBean.getInputFields().add(jsonInputFieldDto);

				jsonInputFieldDto.setName((String) OsgiBundleUtils.invokeOsgiMethod(field, "getName")); // field.getName()
				jsonInputFieldDto.setPath((String) OsgiBundleUtils.invokeOsgiMethod(field, "getPath")); // field.getPath()
				jsonInputFieldDto.setType(
						ValueMetaFactory.getValueMetaName((int) OsgiBundleUtils.invokeOsgiMethod(field, "getType"))); // field.getType()
				jsonInputFieldDto.setFormat((String) OsgiBundleUtils.invokeOsgiMethod(field, "getFormat")); // field.getFormat()
				jsonInputFieldDto.setLength((int) OsgiBundleUtils.invokeOsgiMethod(field, "getLength") + ""); // field.getLength()
				jsonInputFieldDto.setPrecision((int) OsgiBundleUtils.invokeOsgiMethod(field, "getPrecision") + ""); // field.getPrecision()
				jsonInputFieldDto.setCurrency((String) OsgiBundleUtils.invokeOsgiMethod(field, "getCurrencySymbol")); // field.getCurrencySymbol();
				jsonInputFieldDto.setGroup((String) OsgiBundleUtils.invokeOsgiMethod(field, "getGroupSymbol")); // field.getGroupSymbol()
				jsonInputFieldDto.setDecimal((String) OsgiBundleUtils.invokeOsgiMethod(field, "getDecimalSymbol")); // field.getDecimalSymbol()
				jsonInputFieldDto.setTrimType(ValueMetaBase.getTrimTypeCode((int) OsgiBundleUtils.invokeOsgiMethod(field, "getTrimType")));// field.getTrimType()
				jsonInputFieldDto.setRepeat((boolean) OsgiBundleUtils.invokeOsgiMethod(field, "isRepeated")); // field.isRepeated()
			}
		}

		Object additionals = OsgiBundleUtils.getOsgiField(stepM, "additionalOutputFields", false); // jsonInputMeta.additionalOutputFields
		if (null != additionals) {
			AdditionalOutputFieldsDto addDto = new AdditionalOutputFieldsDto();
			jsonBean.setAdditionalOutputFields(addDto);
			addDto.setShortFilenameField((String) OsgiBundleUtils.invokeOsgiMethod(stepM, "getShortFileNameField"));// jsonInputMeta.getShortFileNameField()
			addDto.setPathField((String) OsgiBundleUtils.invokeOsgiMethod(stepM, "getPathField")); // jsonInputMeta.getPathField()
			addDto.setHiddenField((String) OsgiBundleUtils.invokeOsgiMethod(stepM, "isHiddenField")); // jsonInputMeta.isHiddenField()
			addDto.setLastModificationField(
					(String) OsgiBundleUtils.invokeOsgiMethod(stepM, "getLastModificationDateField")); // jsonInputMeta.getLastModificationDateField()
			addDto.setUriField((String) OsgiBundleUtils.invokeOsgiMethod(stepM, "getUriField")); // jsonInputMeta.getUriField()
			addDto.setRootUriField((String) OsgiBundleUtils.invokeOsgiMethod(stepM, "getRootUriField")); // jsonInputMeta.getRootUriField()
			addDto.setExtensionField((String) OsgiBundleUtils.invokeOsgiMethod(stepM, "getExtensionField")); // jsonInputMeta.getExtensionField()
			addDto.setSizeField((String) OsgiBundleUtils.invokeOsgiMethod(stepM, "getSizeField")); // jsonInputMeta.getSizeField()
		}

		return jsonBean;
	}

	/*
	 * Decode step parameter object into step meta.
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepM = stepMeta.getStepMetaInterface();
		SPJsonInput jsonBean = (SPJsonInput) po;

		OsgiBundleUtils.invokeOsgiMethod(stepM, "setRowLimit", Const.toLong(jsonBean.getRowLimit(), 0L)); // inputMeta.setRowLimit(Const.toLong(jsonBean.getRowLimit(),
																											// 0L));
		OsgiBundleUtils.invokeOsgiMethod(stepM, "setFilenameField", jsonBean.getFilenameField()); // inputMeta.setFilenameField(jsonBean.getFilenameField());

		OsgiBundleUtils.invokeOsgiMethod(stepM, "setRowNumberField", jsonBean.getRowNumberField()); // inputMeta.setRowNumberField(jsonBean.getRowNumberField());
		OsgiBundleUtils.invokeOsgiMethod(stepM, "setAddResultFile", jsonBean.isAddResultFile()); // inputMeta.setAddResultFile(jsonBean.isAddResultFile());
		OsgiBundleUtils.invokeOsgiMethod(stepM, "setIncludeFilename", jsonBean.isIncludeFilename()); // inputMeta.setIncludeFilename(jsonBean.isIncludeFilename());
		OsgiBundleUtils.invokeOsgiMethod(stepM, "setIncludeRowNumber", jsonBean.isIncludeRowNumber()); // inputMeta.setIncludeRowNumber(jsonBean.isIncludeRowNumber());
		OsgiBundleUtils.invokeOsgiMethod(stepM, "setReadUrl", jsonBean.isReadurl()); // inputMeta.setReadUrl(jsonBean.isReadurl());
		OsgiBundleUtils.invokeOsgiMethod(stepM, "setIgnoreEmptyFile", jsonBean.isIgnoreEmptyFile()); // inputMeta.setIgnoreEmptyFile(jsonBean.isIgnoreEmptyFile());
		OsgiBundleUtils.invokeOsgiMethod(stepM, "setDoNotFailIfNoFile", jsonBean.isDoNotFailIfNoFile()); // inputMeta.setDoNotFailIfNoFile(jsonBean.isDoNotFailIfNoFile());
		OsgiBundleUtils.invokeOsgiMethod(stepM, "setIgnoreMissingPath", jsonBean.isIgnoreMissingPath()); // inputMeta.setIgnoreMissingPath(jsonBean.isIgnoreMissingPath());
		OsgiBundleUtils.invokeOsgiMethod(stepM, "setRemoveSourceField", jsonBean.isRemoveSourceField()); // inputMeta.setRemoveSourceField(jsonBean.isRemoveSourceField());
		OsgiBundleUtils.invokeOsgiMethod(stepM, "setInFields", jsonBean.isInFields()); // inputMeta.setInFields(jsonBean.isInFields());
		OsgiBundleUtils.invokeOsgiMethod(stepM, "setIsAFile", jsonBean.isAfile()); // inputMeta.setIsAFile();
		OsgiBundleUtils.invokeOsgiMethod(stepM, "setFieldValue", jsonBean.getValueField()); // inputMeta.setFieldValue(jsonBean.getValueField());

		int nrFiles = null != jsonBean.fileName ? jsonBean.fileName.size() : 0;
		int nrFields = null != jsonBean.inputFields ? jsonBean.inputFields.size() : 0;

		OsgiBundleUtils.invokeOsgiMethod(stepM, "allocate", nrFiles, nrFields); // jsonInputMeta.allocate(nrFiles,
																				// nrFields)

		if (nrFiles > 0) {
			String[] fileName = (String[]) OsgiBundleUtils.invokeOsgiMethod(stepM, "getFileName"); // inputMeta.getFileName();
			String[] fileMask = (String[]) OsgiBundleUtils.invokeOsgiMethod(stepM, "getFileMask"); // inputMeta.getFileMask();
			String[] excludeFileMask = (String[]) OsgiBundleUtils.invokeOsgiMethod(stepM, "getExcludeFileMask"); // inputMeta.getExcludeFileMask();
			String[] fileRequired = (String[]) OsgiBundleUtils.invokeOsgiMethod(stepM, "getFileRequired"); // inputMeta.getFileRequired();
			String[] includeSubFolders = (String[]) OsgiBundleUtils.invokeOsgiMethod(stepM, "getIncludeSubFolders"); // inputMeta.getIncludeSubFolders();

			TextFileInputFileDto textFileInputFileDto;
			for (int i = 0; i < nrFiles; i++) {
				textFileInputFileDto = jsonBean.getFileName().get(i);
				fileName[i] = textFileInputFileDto.getFileName();
				fileMask[i] = textFileInputFileDto.getFileMask();
				excludeFileMask[i] = textFileInputFileDto.getExcludeFileMask();
				fileRequired[i] = textFileInputFileDto.getFileRequired();
				includeSubFolders[i] = textFileInputFileDto.getIncludeSubFolders();
			}
		}

		if (nrFields > 0) {
			JsonInputFieldDto jsonInputFieldDto;
			Object[] fields = (Object[]) OsgiBundleUtils.invokeOsgiMethod(stepM, "getInputFields"); // inputMeta.getInputFields();

			ClassLoader classLoader = stepM.getClass().getClassLoader();
			for (int i = 0; i < nrFields; i++) {
				Object in = classLoader.loadClass("org.pentaho.di.trans.steps.jsoninput.JsonInputField").newInstance();
				fields[i] = in;
				jsonInputFieldDto = jsonBean.getInputFields().get(i);

				OsgiBundleUtils.invokeOsgiMethod(in, "setName", jsonInputFieldDto.getName()); // field.setName();
				OsgiBundleUtils.invokeOsgiMethod(in, "setPath", jsonInputFieldDto.getPath()); // field.setPath();

				OsgiBundleUtils.invokeOsgiMethod(in, "setType",
						ValueMetaFactory.getIdForValueMeta(jsonInputFieldDto.getType())); // field.setType();
				OsgiBundleUtils.invokeOsgiMethod(in, "setFormat", jsonInputFieldDto.getFormat()); // field.setFormat());
				OsgiBundleUtils.invokeOsgiMethod(in, "setLength", Const.toInt(jsonInputFieldDto.getLength(), -1)); // field.setLength();

				OsgiBundleUtils.invokeOsgiMethod(in, "setPrecision", Const.toInt(jsonInputFieldDto.getPrecision(), -1)); // field.setPrecision();
				OsgiBundleUtils.invokeOsgiMethod(in, "setCurrencySymbol", jsonInputFieldDto.getCurrency()); // field.setCurrencySymbol();
				OsgiBundleUtils.invokeOsgiMethod(in, "setDecimalSymbol", jsonInputFieldDto.getDecimal()); // field.setDecimalSymbol();
				OsgiBundleUtils.invokeOsgiMethod(in, "setGroupSymbol", jsonInputFieldDto.getGroup()); // field.setGroupSymbol();

				OsgiBundleUtils.invokeOsgiMethod(in, "setTrimType", ValueMetaBase.getTrimTypeByCode(jsonInputFieldDto.getTrimType()) ); // field.setTrimType();

				OsgiBundleUtils.invokeOsgiMethod(in, "setRepeated", Boolean.TRUE.equals(jsonInputFieldDto.getRepeat())); // field.setRepeated();
			}
		}

		AdditionalOutputFieldsDto addDto = jsonBean.getAdditionalOutputFields();
		if (null != addDto) {

			OsgiBundleUtils.invokeOsgiMethod(stepM, "setShortFileNameField", addDto.getShortFilenameField()); // inputMeta.setShortFileNameField(addDto.getShortFilenameField());

			OsgiBundleUtils.invokeOsgiMethod(stepM, "setPathField", addDto.getPathField()); // inputMeta.setPathField(addDto.getPathField());
			OsgiBundleUtils.invokeOsgiMethod(stepM, "setIsHiddenField", addDto.getHiddenField()); // inputMeta.setIsHiddenField(addDto.getHiddenField());
			OsgiBundleUtils.invokeOsgiMethod(stepM, "setLastModificationDateField", addDto.getLastModificationField()); // inputMeta.setLastModificationDateField(addDto.getLastModificationField());

			OsgiBundleUtils.invokeOsgiMethod(stepM, "setUriField", addDto.getUriField()); // inputMeta.setUriField(addDto.getUriField());
			OsgiBundleUtils.invokeOsgiMethod(stepM, "setRootUriField", addDto.getRootUriField()); // inputMeta.setRootUriField(addDto.getRootUriField());
			OsgiBundleUtils.invokeOsgiMethod(stepM, "setExtensionField", addDto.getExtensionField()); // inputMeta.setExtensionField(addDto.getExtensionField());
			OsgiBundleUtils.invokeOsgiMethod(stepM, "setSizeField", addDto.getSizeField()); // inputMeta.setSizeField(addDto.getSizeField());
		}
	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {

		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();

		String fileName = (String) OsgiBundleUtils.invokeOsgiMethod(stepMetaInterface, "getFileName");
		if (StringUtils.isNotBlank(fileName)) {
			
			DataNode fileDataNode = DataNodeUtil.fileNodeParse("Json", fileName.trim(), "", "", "" ) ;
			sdr.addInputDataNode(fileDataNode);
			
			// 增加 系统节点 和 流节点的关系
			String from= "转换:"+transMeta.getName()+",步骤:"+stepMeta.getName();
			List<Relationship> relationships = RelationshipUtil.inputStepRelationship(null, fileDataNode, sdr.getOutputStream(), stepMeta.getName(), from);
			sdr.getDataRelationship().addAll(relationships);
		}
	}
	
	@Override
	 public boolean waitPut(StepLinesDto linesDto,List<StepMeta> nextStepMeta ,StepMeta curStepMeta ,StepMetaInterface stepMetaInterface,StepDataInterface stepDataInterface , StepInterface stepInterface)  throws Exception  {
	//  可能是 多文件 输入,无法定位行数,游标无法定位,使用暴力忽略
		waitPutRowData(stepInterface, linesDto.getRowLine());
		return true;
	}
	
	@Override
	public int stepType() {
		return 1;
	}
	
}
