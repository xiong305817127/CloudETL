/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.steps.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.util.EnvUtil;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.file.BaseFileErrorHandling;
import org.pentaho.di.trans.steps.file.BaseFileField;
import org.pentaho.di.trans.steps.file.BaseFileInputAdditionalField;
import org.pentaho.di.trans.steps.file.BaseFileInputFiles;
import org.pentaho.di.trans.steps.fileinput.text.TextFileFilter;
import org.pentaho.di.trans.steps.fileinput.text.TextFileInputMeta;
import org.pentaho.di.trans.steps.fileinput.text.TextFileInputMeta.Content;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.cloudetl.dto.step.parts.AdditionalOutputFieldsDto;
import com.ys.idatrix.cloudetl.dto.step.parts.TextFileInputContentDto;
import com.ys.idatrix.cloudetl.dto.step.parts.TextFileInputErrorHandlingDto;
import com.ys.idatrix.cloudetl.dto.step.parts.TextFileInputFieldDto;
import com.ys.idatrix.cloudetl.dto.step.parts.TextFileInputFileDto;
import com.ys.idatrix.cloudetl.dto.step.parts.TextFileInputFilterDto;
import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.ext.utils.FieldValidator;
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
 * Step - Text file input(文本文件输入). 转换
 * org.pentaho.di.trans.steps.fileinput.text.TextFileInputMeta
 * 
 * @author JW
 * @since 05-12-2017
 *
 */
@Component("SPTextFileInput")
@Scope("prototype")
public class SPTextFileInput implements StepParameter, StepDataRelationshipParser ,ResumeStepDataParser {

	String templeteFile;

	private boolean acceptingFilenames;
	private boolean passingThruFields;
	private String acceptingField;
	private String acceptingStepName;
	private List<TextFileInputFileDto> inputFiles;
	private TextFileInputContentDto content;
	private TextFileInputErrorHandlingDto errorHandling;
	private List<TextFileInputFilterDto> filters;
	private List<TextFileInputFieldDto> fields;
	private AdditionalOutputFieldsDto additionalOutputFields;

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

	public void setAcceptingFilenames(boolean acceptingFilenames) {
		this.acceptingFilenames = acceptingFilenames;
	}

	public boolean getAcceptingFilenames() {
		return this.acceptingFilenames;
	}

	public void setPassingThruFields(boolean passingThruFields) {
		this.passingThruFields = passingThruFields;
	}

	public boolean getPassingThruFields() {
		return this.passingThruFields;
	}

	public void setAcceptingField(String acceptingField) {
		this.acceptingField = acceptingField;
	}

	public String getAcceptingField() {
		return this.acceptingField;
	}

	public void setAcceptingStepName(String acceptingStepName) {
		this.acceptingStepName = acceptingStepName;
	}

	public String getAcceptingStepName() {
		return this.acceptingStepName;
	}

	public void setInputFiles(List<TextFileInputFileDto> inputFiles) {
		this.inputFiles = inputFiles;
	}

	public List<TextFileInputFileDto> getInputFiles() {
		return this.inputFiles;
	}

	public void setContent(TextFileInputContentDto content) {
		this.content = content;
	}

	public TextFileInputContentDto getContent() {
		return this.content;
	}

	public void setErrorHandling(TextFileInputErrorHandlingDto errorHandling) {
		this.errorHandling = errorHandling;
	}

	public TextFileInputErrorHandlingDto getErrorHandling() {
		return this.errorHandling;
	}

	public void setFilters(List<TextFileInputFilterDto> filters) {
		this.filters = filters;
	}

	public List<TextFileInputFilterDto> getFilters() {
		return this.filters;
	}

	public void setFields(List<TextFileInputFieldDto> fields) {
		this.fields = fields;
	}

	public List<TextFileInputFieldDto> getFields() {
		return this.fields;
	}

	public void setAdditionalOutputFields(AdditionalOutputFieldsDto additionalOutputFields) {
		this.additionalOutputFields = additionalOutputFields;
	}

	public AdditionalOutputFieldsDto getAdditionalOutputFields() {
		return this.additionalOutputFields;
	}

	/*
	 * Parser JSON object as a step parameter object.
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);

		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("inputFiles", TextFileInputFileDto.class);
		classMap.put("content", TextFileInputContentDto.class);
		classMap.put("errorHandling", TextFileInputErrorHandlingDto.class);
		classMap.put("filters", TextFileInputFilterDto.class);
		classMap.put("fields", TextFileInputFieldDto.class);
		classMap.put("additionalOutputFields", AdditionalOutputFieldsDto.class);

		return (SPTextFileInput) JSONObject.toBean(jsonObj, SPTextFileInput.class, classMap);
	}

	/*
	 * Encode a step parameter object from step meta.
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPTextFileInput tfi = new SPTextFileInput();
		TextFileInputMeta textFileInputMeta = (TextFileInputMeta) stepMetaInterface;

		tfi.setAcceptingField(textFileInputMeta.getAcceptingField());
		tfi.setAcceptingFilenames(textFileInputMeta.isAcceptingFilenames());
		tfi.setAcceptingStepName(textFileInputMeta.getAcceptingStepName());

		// Additional
		AdditionalOutputFieldsDto additional = new AdditionalOutputFieldsDto();
		BaseFileInputAdditionalField aofs = textFileInputMeta.additionalOutputFields;
		if (aofs != null) {
			additional.setExtensionField(aofs.extensionField);
			additional.setHiddenField(aofs.hiddenField);
			additional.setLastModificationField(aofs.lastModificationField);
			additional.setPathField(aofs.pathField);
			additional.setRootUriField(aofs.rootUriField);
			additional.setShortFilenameField(aofs.shortFilenameField);
			additional.setSizeField(aofs.sizeField);
			additional.setUriField(aofs.uriField);
		}
		tfi.setAdditionalOutputFields(additional);

		// Content
		TextFileInputContentDto content = new TextFileInputContentDto();
		Content ct = textFileInputMeta.content;
		if (ct != null) {
			content.setBreakInEnclosureAllowed(ct.breakInEnclosureAllowed);
			content.setDateFormatLenient(ct.dateFormatLenient);
			content.setDateFormatLocale(ct.dateFormatLocale.toString());
			content.setEnclosure(ct.enclosure);
			content.setEncoding(ct.encoding);
			content.setEscapeCharacter(ct.escapeCharacter);
			content.setFileCompression(ct.fileCompression);
			content.setFileFormat(ct.fileFormat);
			content.setFilenameField(ct.filenameField);
			content.setFileType(ct.fileType);
			content.setFooter(ct.footer);
			content.setHeader(ct.header);
			content.setIncludeFilename(ct.includeFilename);
			content.setIncludeRowNumber(ct.includeRowNumber);
			content.setLayoutPaged(ct.layoutPaged);
			content.setLineWrapped(ct.lineWrapped);
			content.setNoEmptyLines(ct.noEmptyLines);
			content.setNrFooterLines(ct.nrFooterLines);
			content.setNrHeaderLines(ct.nrHeaderLines);
			content.setNrLinesDocHeader(ct.nrLinesDocHeader);
			content.setNrLinesPerPage(ct.nrLinesPerPage);
			content.setNrWraps(ct.nrWraps);
			content.setRowLimit((int) ct.rowLimit);
			content.setRowNumberByFile(ct.rowNumberByFile);
			content.setRowNumberField(ct.rowNumberField);
			content.setSeparator(ct.separator);
		}
		tfi.setContent(content);

		// Error handling
		TextFileInputErrorHandlingDto handling = new TextFileInputErrorHandlingDto();
		BaseFileErrorHandling eh = textFileInputMeta.errorHandling;
		if (eh != null) {
			handling.setErrorCountField(textFileInputMeta.errorCountField);
			handling.setErrorFieldsField(textFileInputMeta.errorFieldsField);
			handling.setErrorFilesDestinationDirectory(eh.errorFilesDestinationDirectory);
			handling.setErrorFilesExtension(eh.errorFilesExtension);
			handling.setErrorIgnored(eh.errorIgnored);
			handling.setErrorLineSkipped(textFileInputMeta.errorLineSkipped);
			handling.setErrorTextField(textFileInputMeta.errorTextField);
			handling.setFileErrorField(eh.fileErrorField);
			handling.setFileErrorMessageField(eh.fileErrorMessageField);
			handling.setLineNumberFilesDestinationDirectory(eh.lineNumberFilesDestinationDirectory);
			handling.setLineNumberFilesExtension(eh.lineNumberFilesExtension);
			handling.setSkipBadFiles(eh.skipBadFiles);
			handling.setWarningFilesDestinationDirectory(eh.warningFilesDestinationDirectory);
			handling.setWarningFilesExtension(eh.warningFilesExtension);
		}
		tfi.setErrorHandling(handling);

		// Fields
		List<TextFileInputFieldDto> fields = new ArrayList<>();
		BaseFileField[] inputFields = textFileInputMeta.inputFields;
		if (inputFields != null) {
			for (BaseFileField fif : inputFields) {
				TextFileInputFieldDto field = new TextFileInputFieldDto();
				field.setCurrency(fif.getCurrencySymbol());
				field.setDecimal(fif.getDecimalSymbol());
				field.setFormat(fif.getFormat());
				field.setGroup(fif.getGroupSymbol());
				field.setIfnull(fif.getIfNullValue());
				field.setLength(fif.getLength());
				field.setName(fif.getName());
				field.setNullif(fif.getNullString());
				field.setPosition(fif.getPosition());
				field.setPrecision(fif.getPrecision());
				field.setRepeat(fif.isRepeated());
				field.setTrimType(fif.getTrimTypeCode());
				field.setType(fif.getTypeDesc());
				fields.add(field);
			}
		}
		tfi.setFields(fields);

		// Filters
		List<TextFileInputFilterDto> filters = new ArrayList<>();
		TextFileFilter[] tffs = textFileInputMeta.getFilter();
		if (tffs != null) {
			for (TextFileFilter tff : tffs) {
				TextFileInputFilterDto filter = new TextFileInputFilterDto();
				filter.setFilterIsLastLine(tff.isFilterLastLine());
				filter.setFilterIsPositive(tff.isFilterPositive());
				filter.setFilterPosition(tff.getFilterPosition());
				filter.setFilterString(tff.getFilterString());
				filters.add(filter);
			}
		}
		tfi.setFilters(filters);

		// Input files (in file panel)
		List<TextFileInputFileDto> inputFiles = new ArrayList<>();
		BaseFileInputFiles files = textFileInputMeta.inputFiles;
		if (files != null && files.fileName != null) {
			for (int i = 0; i < files.fileName.length; i++) {
				TextFileInputFileDto inputFile = new TextFileInputFileDto();
				inputFile.setExcludeFileMask(files.excludeFileMask[i]);
				inputFile.setFileMask(files.fileMask[i]);
				inputFile.setFileName(FilePathUtil.getRelativeFileName(null, files.fileName[i], FileType.input));

				inputFile.setFileRequired(files.fileRequired[i]);
				inputFile.setIncludeSubFolders(files.includeSubFolders[i]);
				inputFiles.add(inputFile);
			}
		}
		tfi.setInputFiles(inputFiles);

		tfi.setPassingThruFields(textFileInputMeta.inputFiles.passingThruFields);

		return tfi;
	}
	
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		decodeParameterObject(stepMeta.getStepMetaInterface(), po, databases, transMeta);
	}

	/*
	 * Decode step parameter object into step meta.
	 */
	public void decodeParameterObject(StepMetaInterface stepMetaInterface , Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		TextFileInputMeta textFileInputMeta = (TextFileInputMeta) stepMetaInterface;
		SPTextFileInput tfi = (SPTextFileInput) po;

		// Allocate heap for files, filters and fields!
		int nrfiles = tfi.inputFiles.size();
		int nrfields = tfi.fields.size();
		int nrfilters = tfi.filters.size();
		textFileInputMeta.allocate(nrfiles, nrfields, nrfilters);

		// Input files (in file panel)
		BaseFileInputFiles files = textFileInputMeta.inputFiles;

		files.passingThruFields = tfi.passingThruFields;
		files.acceptingField = tfi.acceptingField;
		files.acceptingFilenames = tfi.acceptingFilenames;
		files.acceptingStepName = tfi.acceptingStepName;

		for (int i = 0; i < nrfiles; i++) {
			files.fileName[i] = FilePathUtil.getRealFileName(null, tfi.inputFiles.get(i).getFileName(), FileType.input);
			files.fileMask[i] = tfi.inputFiles.get(i).getFileMask();
			files.excludeFileMask[i] = tfi.inputFiles.get(i).getExcludeFileMask();
			files.fileRequired[i] = tfi.inputFiles.get(i).getFileRequired();
			files.includeSubFolders[i] = tfi.inputFiles.get(i).getIncludeSubFolders();
		}

		// Filters
		TextFileFilter[] tffs = textFileInputMeta.getFilter();
		for (int i = 0; i < nrfilters; i++) {
			tffs[i] = new TextFileFilter();
			tffs[i].setFilterLastLine(tfi.filters.get(i).getFilterIsLastLine());
			tffs[i].setFilterPosition(tfi.filters.get(i).getFilterPosition());
			tffs[i].setFilterPositive(tfi.filters.get(i).getFilterIsPositive());
			tffs[i].setFilterString(tfi.filters.get(i).getFilterString());
		}

		// Fields
		BaseFileField[] inputFields = textFileInputMeta.inputFields;
		for (int i = 0; i < nrfields; i++) {
			BaseFileField field = new BaseFileField();
			field.setCurrencySymbol(tfi.fields.get(i).getCurrency());
			field.setDecimalSymbol(tfi.fields.get(i).getDecimal());
			field.setFormat(tfi.fields.get(i).getFormat());
			field.setGroupSymbol(tfi.fields.get(i).getGroup());
			field.setIfNullValue(tfi.fields.get(i).getIfnull());
			// field.setIgnored();
			field.setLength(FieldValidator.fixedLength(tfi.fields.get(i).getLength()));
			field.setName(tfi.fields.get(i).getName());
			field.setNullString(tfi.fields.get(i).getNullif());
			field.setPosition(tfi.fields.get(i).getPosition());
			field.setPrecision(FieldValidator.fixedPrecision(tfi.fields.get(i).getPrecision()));
			field.setRepeated(tfi.fields.get(i).getRepeat());
			field.setTrimType(tfi.fields.get(i).getTrimType());
			field.setType(tfi.fields.get(i).getType());
			inputFields[i] = field;
		}

		// Error handling
		textFileInputMeta.errorCountField = tfi.errorHandling.getErrorCountField();
		textFileInputMeta.errorFieldsField = tfi.errorHandling.getErrorFieldsField();
		textFileInputMeta.errorLineSkipped = tfi.errorHandling.getErrorLineSkipped();
		textFileInputMeta.errorTextField = tfi.errorHandling.getErrorTextField();

		BaseFileErrorHandling handling = textFileInputMeta.errorHandling;
		if (handling == null) {
			handling = new BaseFileErrorHandling();
			textFileInputMeta.errorHandling = handling;
		}
		handling.errorFilesDestinationDirectory = tfi.errorHandling.getErrorFilesDestinationDirectory();
		handling.errorFilesExtension = tfi.errorHandling.getErrorFilesExtension();
		handling.errorIgnored = tfi.errorHandling.getErrorIgnored();
		handling.fileErrorField = tfi.errorHandling.getFileErrorField();
		handling.fileErrorMessageField = tfi.errorHandling.getFileErrorMessageField();
		handling.lineNumberFilesDestinationDirectory = tfi.errorHandling.getLineNumberFilesDestinationDirectory();
		handling.lineNumberFilesExtension = tfi.errorHandling.getLineNumberFilesExtension();
		handling.skipBadFiles = tfi.errorHandling.getSkipBadFiles();
		handling.warningFilesDestinationDirectory = tfi.errorHandling.getWarningFilesDestinationDirectory();
		handling.warningFilesExtension = tfi.errorHandling.getWarningFilesExtension();

		// Content
		Content content = textFileInputMeta.content;
		if (content == null) {
			content = new Content();
			textFileInputMeta.content = content;
		}
		content.breakInEnclosureAllowed = tfi.content.getBreakInEnclosureAllowed();
		content.dateFormatLenient = tfi.content.getDateFormatLenient();
		String dateLocale = tfi.content.getDateFormatLocale();
		if (dateLocale != null) {
			content.dateFormatLocale = EnvUtil.createLocale(dateLocale);
		} else {
			content.dateFormatLocale = Locale.getDefault();
		}

		content.enclosure = tfi.content.getEnclosure();
		content.encoding = tfi.content.getEncoding();
		content.escapeCharacter = tfi.content.getEscapeCharacter();
		content.fileCompression = tfi.content.getFileCompression();
		content.fileFormat = tfi.content.getFileFormat();

		content.filenameField = tfi.content.getFilenameField();
		content.fileType = tfi.content.getFileType();
		content.footer = tfi.content.getFooter();
		content.header = tfi.content.getHeader();
		content.includeFilename = tfi.content.getIncludeFilename();
		content.includeRowNumber = tfi.content.getIncludeRowNumber();
		content.layoutPaged = tfi.content.getLayoutPaged();
		content.lineWrapped = tfi.content.getLineWrapped();
		content.noEmptyLines = tfi.content.getNoEmptyLines();
		content.nrFooterLines = tfi.content.getNrFooterLines();
		content.nrHeaderLines = tfi.content.getNrHeaderLines();
		content.nrLinesDocHeader = tfi.content.getNrLinesDocHeader();
		content.nrLinesPerPage = tfi.content.getNrLinesPerPage();
		content.nrWraps = tfi.content.getNrWraps();

		content.rowLimit = tfi.content.getRowLimit();
		content.rowNumberByFile = tfi.content.getRowNumberByFile();
		content.rowNumberField = tfi.content.getRowNumberField();
		content.separator = tfi.content.getSeparator();

		// Additional
		BaseFileInputAdditionalField aofs = textFileInputMeta.additionalOutputFields;
		if (aofs == null) {
			aofs = new BaseFileInputAdditionalField();
			textFileInputMeta.additionalOutputFields = aofs;
		}
		aofs.extensionField = tfi.additionalOutputFields.getExtensionField();
		aofs.hiddenField = tfi.additionalOutputFields.getHiddenField();
		aofs.lastModificationField = tfi.additionalOutputFields.getLastModificationField();
		aofs.pathField = tfi.additionalOutputFields.getPathField();
		aofs.rootUriField = tfi.additionalOutputFields.getRootUriField();
		aofs.shortFilenameField = tfi.additionalOutputFields.getShortFilenameField();
		aofs.sizeField = tfi.additionalOutputFields.getSizeField();
		aofs.uriField = tfi.additionalOutputFields.getUriField();
	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {

		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		TextFileInputMeta textFileInputMeta = (TextFileInputMeta) stepMetaInterface;

		String[] fileNames = textFileInputMeta.getFileName();
		for (String name : fileNames) {
			if (StringUtils.isNotEmpty(name)) {
				
				DataNode fileDataNode = DataNodeUtil.fileNodeParse("Text", name.trim(),  textFileInputMeta.getEncoding(), textFileInputMeta.content.fileCompression, "" ) ;
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
		//文本文件输入  可能是 多文件 输入,无法定位行数(组件根据\n\r确定是否新行),游标无法定位,使用暴力忽略
		waitPutRowData(stepInterface, linesDto.getRowLine());
		return true;
	}
	
	@Override
	public int stepType() {
		return 1;
	}

}
