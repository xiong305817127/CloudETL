/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.steps.input;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.excelinput.ExcelInputField;
import org.pentaho.di.trans.steps.excelinput.ExcelInputMeta;
import org.pentaho.di.trans.steps.excelinput.SpreadSheetType;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.cloudetl.dto.step.parts.ExcelInputExcelInputFieldDto;
import com.ys.idatrix.cloudetl.dto.step.parts.ExcelInputsheetNameDto;
import com.ys.idatrix.cloudetl.dto.step.parts.TextFileInputFileDto;
import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.ext.utils.FilePathUtil;
import com.ys.idatrix.cloudetl.ext.utils.FilePathUtil.FileType;
import com.ys.idatrix.cloudetl.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.cloudetl.recovery.trans.dto.StepLinesDto;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.cloudetl.toolkit.domain.DataNode;
import com.ys.idatrix.cloudetl.toolkit.domain.Relationship;
import com.ys.idatrix.cloudetl.toolkit.utils.DataNodeUtil;
import com.ys.idatrix.cloudetl.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - Excel Input(Excel输入). 转换
 * org.pentaho.di.trans.steps.excelinput.ExcelInputMeta
 * 
 * @author XH
 * @since 05-12-2017
 *
 */
@Component("SPExcelInput")
@Scope("prototype")
public class SPExcelInput implements StepParameter, StepDataRelationshipParser ,ResumeStepDataParser{

	String templeteFile;

	boolean startsWithHeader = true;
	boolean ignoreEmptyRows = true;
	boolean stopOnEmpty = false;
	String fileField;
	String sheetField;
	String sheetRowNumberField ;
	String rowNumberField;
	long rowLimit;
	String encoding = "UTF-8";
	boolean isaddresult = true;
	boolean acceptingFilenames;
	String acceptingField;
	String acceptingStepName;

	boolean strictTypes = false;
	boolean errorIgnored = false;
	boolean errorLineSkipped = false;
	String warningFilesDestinationDirectory;
	String warningFilesExtension = "warning";
	String errorFilesDestinationDirectory;
	String errorFilesExtension = "error";
	String lineNumberFilesDestinationDirectory;
	String lineNumberFilesExtension = "line";
	String shortFileFieldName = "short_filename";
	String pathFieldName;
	String hiddenFieldName;
	String lastModificationTimeFieldName;
	String uriNameFieldName;
	String rootUriNameFieldName;
	String extensionFieldName;
	String sizeFieldName;
	SpreadSheetType spreadSheetType = SpreadSheetType.POI;

	List<TextFileInputFileDto> fileName;
	List<ExcelInputExcelInputFieldDto> field;
	List<ExcelInputsheetNameDto> sheetName;

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
	 * @return startsWithHeader
	 */
	public boolean isStartsWithHeader() {
		return startsWithHeader;
	}

	/**
	 * @param startsWithHeader
	 *            要设置的 startsWithHeader
	 */
	public void setStartsWithHeader(boolean startsWithHeader) {
		this.startsWithHeader = startsWithHeader;
	}

	/**
	 * @return ignoreEmptyRows
	 */
	public boolean isIgnoreEmptyRows() {
		return ignoreEmptyRows;
	}

	/**
	 * @param ignoreEmptyRows
	 *            要设置的 ignoreEmptyRows
	 */
	public void setIgnoreEmptyRows(boolean ignoreEmptyRows) {
		this.ignoreEmptyRows = ignoreEmptyRows;
	}

	/**
	 * @return stopOnEmpty
	 */
	public boolean isStopOnEmpty() {
		return stopOnEmpty;
	}

	/**
	 * @param stopOnEmpty
	 *            要设置的 stopOnEmpty
	 */
	public void setStopOnEmpty(boolean stopOnEmpty) {
		this.stopOnEmpty = stopOnEmpty;
	}

	/**
	 * @return fileField
	 */
	public String getFileField() {
		return fileField;
	}

	/**
	 * @param fileField
	 *            要设置的 fileField
	 */
	public void setFileField(String fileField) {
		this.fileField = fileField;
	}

	/**
	 * @return sheetField
	 */
	public String getSheetField() {
		return sheetField;
	}

	/**
	 * @param sheetField
	 *            要设置的 sheetField
	 */
	public void setSheetField(String sheetField) {
		this.sheetField = sheetField;
	}

	/**
	 * @return sheetRowNumberField
	 */
	public String getSheetRowNumberField() {
		return sheetRowNumberField;
	}

	/**
	 * @param sheetRowNumberField
	 *            要设置的 sheetRowNumberField
	 */
	public void setSheetRowNumberField(String sheetRowNumberField) {
		this.sheetRowNumberField = sheetRowNumberField;
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
	 * @return encoding
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * @param encoding
	 *            要设置的 encoding
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
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
	 * @return acceptingFilenames
	 */
	public boolean isAcceptingFilenames() {
		return acceptingFilenames;
	}

	/**
	 * @param acceptingFilenames
	 *            要设置的 acceptingFilenames
	 */
	public void setAcceptingFilenames(boolean acceptingFilenames) {
		this.acceptingFilenames = acceptingFilenames;
	}

	/**
	 * @return acceptingField
	 */
	public String getAcceptingField() {
		return acceptingField;
	}

	/**
	 * @param acceptingField
	 *            要设置的 acceptingField
	 */
	public void setAcceptingField(String acceptingField) {
		this.acceptingField = acceptingField;
	}

	/**
	 * @return acceptingStepName
	 */
	public String getAcceptingStepName() {
		return acceptingStepName;
	}

	/**
	 * @param acceptingStepName
	 *            要设置的 acceptingStepName
	 */
	public void setAcceptingStepName(String acceptingStepName) {
		this.acceptingStepName = acceptingStepName;
	}

	/**
	 * @return strictTypes
	 */
	public boolean isStrictTypes() {
		return strictTypes;
	}

	/**
	 * @param strictTypes
	 *            要设置的 strictTypes
	 */
	public void setStrictTypes(boolean strictTypes) {
		this.strictTypes = strictTypes;
	}

	/**
	 * @return errorIgnored
	 */
	public boolean isErrorIgnored() {
		return errorIgnored;
	}

	/**
	 * @param errorIgnored
	 *            要设置的 errorIgnored
	 */
	public void setErrorIgnored(boolean errorIgnored) {
		this.errorIgnored = errorIgnored;
	}

	/**
	 * @return errorLineSkipped
	 */
	public boolean isErrorLineSkipped() {
		return errorLineSkipped;
	}

	/**
	 * @param errorLineSkipped
	 *            要设置的 errorLineSkipped
	 */
	public void setErrorLineSkipped(boolean errorLineSkipped) {
		this.errorLineSkipped = errorLineSkipped;
	}

	/**
	 * @return warningFilesDestinationDirectory
	 */
	public String getWarningFilesDestinationDirectory() {
		return warningFilesDestinationDirectory;
	}

	/**
	 * @param warningFilesDestinationDirectory
	 *            要设置的 warningFilesDestinationDirectory
	 */
	public void setWarningFilesDestinationDirectory(String warningFilesDestinationDirectory) {
		this.warningFilesDestinationDirectory = warningFilesDestinationDirectory;
	}

	/**
	 * @return warningFilesExtension
	 */
	public String getWarningFilesExtension() {
		return warningFilesExtension;
	}

	/**
	 * @param warningFilesExtension
	 *            要设置的 warningFilesExtension
	 */
	public void setWarningFilesExtension(String warningFilesExtension) {
		this.warningFilesExtension = warningFilesExtension;
	}

	/**
	 * @return errorFilesDestinationDirectory
	 */
	public String getErrorFilesDestinationDirectory() {
		return errorFilesDestinationDirectory;
	}

	/**
	 * @param errorFilesDestinationDirectory
	 *            要设置的 errorFilesDestinationDirectory
	 */
	public void setErrorFilesDestinationDirectory(String errorFilesDestinationDirectory) {
		this.errorFilesDestinationDirectory = errorFilesDestinationDirectory;
	}

	/**
	 * @return errorFilesExtension
	 */
	public String getErrorFilesExtension() {
		return errorFilesExtension;
	}

	/**
	 * @param errorFilesExtension
	 *            要设置的 errorFilesExtension
	 */
	public void setErrorFilesExtension(String errorFilesExtension) {
		this.errorFilesExtension = errorFilesExtension;
	}

	/**
	 * @return lineNumberFilesDestinationDirectory
	 */
	public String getLineNumberFilesDestinationDirectory() {
		return lineNumberFilesDestinationDirectory;
	}

	/**
	 * @param lineNumberFilesDestinationDirectory
	 *            要设置的 lineNumberFilesDestinationDirectory
	 */
	public void setLineNumberFilesDestinationDirectory(String lineNumberFilesDestinationDirectory) {
		this.lineNumberFilesDestinationDirectory = lineNumberFilesDestinationDirectory;
	}

	/**
	 * @return lineNumberFilesExtension
	 */
	public String getLineNumberFilesExtension() {
		return lineNumberFilesExtension;
	}

	/**
	 * @param lineNumberFilesExtension
	 *            要设置的 lineNumberFilesExtension
	 */
	public void setLineNumberFilesExtension(String lineNumberFilesExtension) {
		this.lineNumberFilesExtension = lineNumberFilesExtension;
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
	 * @return spreadSheetType
	 */
	public SpreadSheetType getSpreadSheetType() {
		return spreadSheetType;
	}

	/**
	 * @param spreadSheetType
	 *            要设置的 spreadSheetType
	 */
	public void setSpreadSheetType(SpreadSheetType spreadSheetType) {
		this.spreadSheetType = spreadSheetType;
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
	 * @return field
	 */
	public List<ExcelInputExcelInputFieldDto> getField() {
		return field;
	}

	/**
	 * @param field
	 *            要设置的 field
	 */
	public void setField(List<ExcelInputExcelInputFieldDto> field) {
		this.field = field;
	}

	/**
	 * @return sheetName
	 */
	public List<ExcelInputsheetNameDto> getSheetName() {
		return sheetName;
	}

	/**
	 * @param sheetName
	 *            要设置的 sheetName
	 */
	public void setSheetName(List<ExcelInputsheetNameDto> sheetName) {
		this.sheetName = sheetName;
	}

	@Override
	public void initParamObject(StepMeta stepMeta) {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		ExcelInputMeta excelinputmeta = (ExcelInputMeta) stepMetaInterface;
		excelinputmeta.setSpreadSheetType(  SpreadSheetType.POI );

	}
	
	
	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);

		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("sheetName", ExcelInputsheetNameDto.class);
		classMap.put("fileName", TextFileInputFileDto.class);
		classMap.put("field", ExcelInputExcelInputFieldDto.class);

		return (SPExcelInput) JSONObject.toBean(jsonObj, SPExcelInput.class, classMap);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPExcelInput spExcelInput = new SPExcelInput();
		ExcelInputMeta excelinputmeta = (ExcelInputMeta) stepMetaInterface;

		List<TextFileInputFileDto> fileNameList = Lists.newArrayList();
		String[] fileNames = excelinputmeta.getFileName();
		String[] fileMasks = excelinputmeta.getFileMask();
		String[] excludeFileMasks = excelinputmeta.getExcludeFileMask();
		String[] fileRequireds = excelinputmeta.getFileRequired();
		String[] includeSubFolderss = excelinputmeta.getIncludeSubFolders();
		for (int i = 0; i < fileNames.length; i++) {
			TextFileInputFileDto excelinputfilenamedto = new TextFileInputFileDto();
			excelinputfilenamedto.setFileName(FilePathUtil.getRelativeFileName(null, fileNames[i], FileType.input));
			excelinputfilenamedto.setFileMask(fileMasks[i]);
			excelinputfilenamedto.setExcludeFileMask(excludeFileMasks[i]);
			excelinputfilenamedto.setFileRequired(fileRequireds[i]);
			excelinputfilenamedto.setIncludeSubFolders(includeSubFolderss[i]);
			fileNameList.add(excelinputfilenamedto);
		}
		spExcelInput.setFileName(fileNameList);

		spExcelInput.setEncoding(excelinputmeta.getEncoding());
		spExcelInput.setErrorFilesExtension(excelinputmeta.getErrorFilesExtension());
		spExcelInput.setLineNumberFilesExtension(excelinputmeta.getLineNumberFilesExtension());
		spExcelInput.setSpreadSheetType(excelinputmeta.getSpreadSheetType());
		spExcelInput.setRowNumberField(excelinputmeta.getRowNumberField());
		spExcelInput.setSheetRowNumberField(excelinputmeta.getSheetRowNumberField());
		spExcelInput.setFileField(excelinputmeta.getFileField());
		spExcelInput.setRowLimit(excelinputmeta.getRowLimit());
		spExcelInput.setSheetField(excelinputmeta.getSheetField());

		List<ExcelInputsheetNameDto> sheetNameList = Lists.newArrayList();
		String[] sheetNames = excelinputmeta.getSheetName();
		int[] startRows = excelinputmeta.getStartRow();
		int[] startColumns = excelinputmeta.getStartColumn();
		for (int i = 0; i < sheetNames.length; i++) {
			ExcelInputsheetNameDto excelinputsheetnamedto = new ExcelInputsheetNameDto();
			excelinputsheetnamedto.setSheetName(sheetNames[i]);
			excelinputsheetnamedto.setStartRow(startRows[i]);
			excelinputsheetnamedto.setStartColumn(startColumns[i]);
			sheetNameList.add(excelinputsheetnamedto);
		}
		spExcelInput.setSheetName(sheetNameList);

		spExcelInput.setWarningFilesDestinationDirectory(excelinputmeta.getWarningFilesDestinationDirectory());
		spExcelInput.setErrorFilesDestinationDirectory(excelinputmeta.getErrorFilesDestinationDirectory());
		spExcelInput.setLineNumberFilesDestinationDirectory(excelinputmeta.getLineNumberFilesDestinationDirectory());
		spExcelInput.setErrorLineSkipped(excelinputmeta.isErrorLineSkipped());
		spExcelInput.setAcceptingFilenames(excelinputmeta.isAcceptingFilenames());
		spExcelInput.setErrorIgnored(excelinputmeta.isErrorIgnored());
		spExcelInput.setStrictTypes(excelinputmeta.isStrictTypes());
		spExcelInput.setAcceptingStepName(excelinputmeta.getAcceptingStepName());
		spExcelInput.setStartsWithHeader(excelinputmeta.startsWithHeader());
		spExcelInput.setIgnoreEmptyRows(excelinputmeta.ignoreEmptyRows());
		spExcelInput.setStopOnEmpty(excelinputmeta.stopOnEmpty());
		spExcelInput.setIsaddresult(excelinputmeta.isAddResultFile());
		spExcelInput.setWarningFilesExtension(excelinputmeta.getBadLineFilesExtension());
		spExcelInput.setShortFileFieldName(excelinputmeta.getShortFileNameField());
		spExcelInput.setPathFieldName(excelinputmeta.getPathField());
		spExcelInput.setHiddenFieldName(excelinputmeta.isHiddenField());
		spExcelInput.setLastModificationTimeFieldName(excelinputmeta.getLastModificationDateField());
		spExcelInput.setUriNameFieldName(excelinputmeta.getUriField());
		spExcelInput.setRootUriNameFieldName(excelinputmeta.getRootUriField());
		spExcelInput.setExtensionFieldName(excelinputmeta.getExtensionField());
		spExcelInput.setSizeFieldName(excelinputmeta.getSizeField());
		spExcelInput.setAcceptingField(excelinputmeta.getAcceptingField());

		ExcelInputField[] fieldArray = excelinputmeta.getField();
		List<ExcelInputExcelInputFieldDto> fieldList = Arrays.asList(fieldArray).stream().map(temp1 -> {
			ExcelInputExcelInputFieldDto temp2 = new ExcelInputExcelInputFieldDto();
			temp2.setName(temp1.getName());
			temp2.setTypedesc(temp1.getTypeDesc());
			temp2.setLength(temp1.getLength());
			temp2.setPrecision(temp1.getPrecision());
			temp2.setTrimtypecode(temp1.getTrimTypeCode());
			temp2.setRepeated(temp1.isRepeated());
			temp2.setFormat(temp1.getFormat());
			temp2.setCurrencysymbol(temp1.getCurrencySymbol());
			temp2.setDecimalsymbol(temp1.getDecimalSymbol());
			temp2.setGroupsymbol(temp1.getGroupSymbol());
			return temp2;
		}).collect(Collectors.toList());
		spExcelInput.setField(fieldList);

		return spExcelInput;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPExcelInput spExcelInput = (SPExcelInput) po;
		ExcelInputMeta excelinputmeta = (ExcelInputMeta) stepMetaInterface;

		excelinputmeta.setWarningFilesDestinationDirectory(spExcelInput.getWarningFilesDestinationDirectory());
		excelinputmeta.setErrorFilesDestinationDirectory(spExcelInput.getErrorFilesDestinationDirectory());
		excelinputmeta.setLineNumberFilesDestinationDirectory(spExcelInput.getLineNumberFilesDestinationDirectory());
		excelinputmeta.setErrorIgnored(spExcelInput.isErrorIgnored());
		excelinputmeta.setStrictTypes(spExcelInput.isStrictTypes());
		excelinputmeta.setAcceptingStepName(spExcelInput.getAcceptingStepName());
		excelinputmeta.setErrorFilesExtension(spExcelInput.getErrorFilesExtension());
		excelinputmeta.setLineNumberFilesExtension(spExcelInput.getLineNumberFilesExtension());
		excelinputmeta.setErrorLineSkipped(spExcelInput.isErrorLineSkipped());
		excelinputmeta.setAcceptingFilenames(spExcelInput.isAcceptingFilenames());
		excelinputmeta.setSpreadSheetType(spExcelInput.getSpreadSheetType());
		excelinputmeta.setEncoding(spExcelInput.getEncoding());
		excelinputmeta.setIgnoreEmptyRows(spExcelInput.isIgnoreEmptyRows());
		excelinputmeta.setFileField(spExcelInput.getFileField());
		excelinputmeta.setRowLimit(spExcelInput.getRowLimit());
		excelinputmeta.setSheetField(spExcelInput.getSheetField());

		List<ExcelInputField> fieldList = spExcelInput.getField().stream().map(temp1 -> {
			ExcelInputField temp2 = new ExcelInputField();
			temp2.setName(temp1.getName());
			temp2.setType(temp1.getTypedesc());
			temp2.setLength(temp1.getLength());
			temp2.setPrecision(temp1.getPrecision());
			temp2.setTrimType(temp1.getTrimtypecode());
			temp2.setRepeated(temp1.isRepeated());
			temp2.setFormat(temp1.getFormat());
			temp2.setCurrencySymbol(temp1.getCurrencysymbol());
			temp2.setDecimalSymbol(temp1.getDecimalsymbol());
			temp2.setGroupSymbol(temp1.getGroupsymbol());
			return temp2;
		}).collect(Collectors.toList());
		excelinputmeta.setField(fieldList.toArray(new ExcelInputField[spExcelInput.getField().size()]));

		String[] fileNames = new String[spExcelInput.getFileName().size()];
		String[] fileMasks = new String[spExcelInput.getFileName().size()];
		String[] excludeFileMasks = new String[spExcelInput.getFileName().size()];
		String[] fileRequireds = new String[spExcelInput.getFileName().size()];
		String[] includeSubFolderss = new String[spExcelInput.getFileName().size()];
		for (int i = 0; i < spExcelInput.getFileName().size(); i++) {
			TextFileInputFileDto excelinputfilenamedto = spExcelInput.getFileName().get(i);
			fileNames[i] = FilePathUtil.getRealFileName(null,excelinputfilenamedto.getFileName(), FileType.input);
			fileMasks[i] = excelinputfilenamedto.getFileMask();
			excludeFileMasks[i] = excelinputfilenamedto.getExcludeFileMask();
			fileRequireds[i] = excelinputfilenamedto.getFileRequired();
			includeSubFolderss[i] = excelinputfilenamedto.getIncludeSubFolders();
		}
		excelinputmeta.setFileName(fileNames);
		excelinputmeta.setFileMask(fileMasks);
		excelinputmeta.setExcludeFileMask(excludeFileMasks);
		excelinputmeta.setFileRequired(fileRequireds);
		excelinputmeta.setIncludeSubFolders(includeSubFolderss);

		String[] sheetNames = new String[spExcelInput.getSheetName().size()];
		int[] startRows = new int[spExcelInput.getSheetName().size()];
		int[] startColumns = new int[spExcelInput.getSheetName().size()];
		for (int i = 0; i < spExcelInput.getSheetName().size(); i++) {
			ExcelInputsheetNameDto excelinputsheetnamedto = spExcelInput.getSheetName().get(i);
			sheetNames[i] = excelinputsheetnamedto.getSheetName();
			startRows[i] = excelinputsheetnamedto.getStartRow();
			startColumns[i] = excelinputsheetnamedto.getStartColumn();
		}
		excelinputmeta.setSheetName(sheetNames);
		excelinputmeta.setStartRow(startRows);
		excelinputmeta.setStartColumn(startColumns);

		excelinputmeta.setStopOnEmpty(spExcelInput.isStopOnEmpty());
		excelinputmeta.setRowNumberField(spExcelInput.getRowNumberField());
		excelinputmeta.setSheetRowNumberField(spExcelInput.getSheetRowNumberField());
		excelinputmeta.setStartsWithHeader(spExcelInput.isStartsWithHeader());
		excelinputmeta.setAddResultFile(spExcelInput.isIsaddresult());
		excelinputmeta.setBadLineFilesExtension(spExcelInput.getWarningFilesExtension());
		excelinputmeta.setShortFileNameField(spExcelInput.getShortFileFieldName());
		excelinputmeta.setPathField(spExcelInput.getPathFieldName());
		excelinputmeta.setIsHiddenField(spExcelInput.getHiddenFieldName());
		excelinputmeta.setLastModificationDateField(spExcelInput.getLastModificationTimeFieldName());
		excelinputmeta.setUriField(spExcelInput.getUriNameFieldName());
		excelinputmeta.setRootUriField(spExcelInput.getRootUriNameFieldName());
		excelinputmeta.setExtensionField(spExcelInput.getExtensionFieldName());
		excelinputmeta.setSizeField(spExcelInput.getSizeFieldName());
		excelinputmeta.setAcceptingField(spExcelInput.getAcceptingField());

		// T为AcceptingStep赋值通过 acceptingStepName
		excelinputmeta.searchInfoAndTargetSteps(transMeta.getSteps());

	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {

		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		ExcelInputMeta excelInputMeta = (ExcelInputMeta) stepMetaInterface;

		String[] fileNames = excelInputMeta.getFileName();
		for (String name : fileNames) {
			if (!Utils.isEmpty(name)) {
				
				DataNode fileDataNode = DataNodeUtil.fileNodeParse("Excel", name.trim(),  excelInputMeta.getEncoding(), "", "" ) ;
				sdr.addInputDataNode(fileDataNode);
				
				// 增加 系统节点 和 流节点的关系
				String from = "转换:" + transMeta.getName() + ",步骤:" + stepMeta.getName();
				List<Relationship> relationships = RelationshipUtil.inputStepRelationship(null, fileDataNode, sdr.getOutputStream(), stepMeta.getName(), from);
				sdr.getDataRelationship().addAll(relationships);
			}
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
