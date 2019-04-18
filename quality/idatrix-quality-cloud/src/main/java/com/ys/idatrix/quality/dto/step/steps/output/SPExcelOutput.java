package com.ys.idatrix.quality.dto.step.steps.output;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.metrics2.sink.relocated.google.common.collect.Maps;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.exceloutput.ExcelField;
import org.pentaho.di.trans.steps.exceloutput.ExcelOutputMeta;
import org.pentaho.pms.util.Const;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.quality.dto.step.StepFieldDto;
import com.ys.idatrix.quality.dto.step.parts.ExcelOutputExcelFieldDto;
import com.ys.idatrix.quality.dto.step.steps.StepParameter;
import com.ys.idatrix.quality.ext.utils.FilePathUtil;
import com.ys.idatrix.quality.ext.utils.FilePathUtil.FileType;
import com.ys.idatrix.quality.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.quality.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.quality.toolkit.domain.DataNode;
import com.ys.idatrix.quality.toolkit.domain.Relationship;
import com.ys.idatrix.quality.toolkit.utils.DataNodeUtil;
import com.ys.idatrix.quality.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - ExcelOutput. 转换 org.pentaho.di.trans.steps.exceloutput.ExcelOutputMeta
 * 
 * @author XH
 * @since 2018-10-12
 */
@Component("SPExcelOutput")
@Scope("prototype")
public class SPExcelOutput implements StepParameter, StepDataRelationshipParser, ResumeStepDataParser {

	boolean headerEnabled;
	boolean footerEnabled;
	String encoding;
	boolean append;
	boolean addToResultFilenames;

	String fileName;
	String extension;
	boolean doNotOpenNewFileInit;
	boolean createparentfolder;
	boolean stepNrInFilename;
	boolean dateInFilename;
	boolean timeInFilename;
	boolean SpecifyFormat;
	String dateTimeFormat;
	String sheetname;
	boolean autoSizeColumns;
	boolean nullIsBlank;
	boolean protectsheet;
	String password;
	int splitEvery;
	boolean usetempfiles;
	String tempdirectory;

	boolean templateEnabled;
	boolean templateAppend;
	String templateFileName;

	List<ExcelOutputExcelFieldDto> outputFields;

	String headerFontName;
	String headerFontSize;
	boolean headerFontBold;
	boolean headerFontItalic;
	String headerFontUnderline;
	String headerFontOrientation;
	String headerFontColor;
	String headerBackgroundColor;
	String headerRowHeight;
	String headerAlignment;
	String headerImage;
	String rowFontName;
	String rowFontSize;
	String rowFontColor;
	String rowBackgroundColor;

	public boolean isHeaderEnabled() {
		return headerEnabled;
	}

	public void setHeaderEnabled(boolean headerEnabled) {
		this.headerEnabled = headerEnabled;
	}

	public boolean isFooterEnabled() {
		return footerEnabled;
	}

	public void setFooterEnabled(boolean footerEnabled) {
		this.footerEnabled = footerEnabled;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public boolean isAppend() {
		return append;
	}

	public void setAppend(boolean append) {
		this.append = append;
	}

	public boolean isAddToResultFilenames() {
		return addToResultFilenames;
	}

	public void setAddToResultFilenames(boolean addToResultFilenames) {
		this.addToResultFilenames = addToResultFilenames;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public boolean isDoNotOpenNewFileInit() {
		return doNotOpenNewFileInit;
	}

	public void setDoNotOpenNewFileInit(boolean doNotOpenNewFileInit) {
		this.doNotOpenNewFileInit = doNotOpenNewFileInit;
	}

	public boolean isCreateparentfolder() {
		return createparentfolder;
	}

	public void setCreateparentfolder(boolean createparentfolder) {
		this.createparentfolder = createparentfolder;
	}

	public boolean isStepNrInFilename() {
		return stepNrInFilename;
	}

	public void setStepNrInFilename(boolean stepNrInFilename) {
		this.stepNrInFilename = stepNrInFilename;
	}

	public boolean isDateInFilename() {
		return dateInFilename;
	}

	public void setDateInFilename(boolean dateInFilename) {
		this.dateInFilename = dateInFilename;
	}

	public boolean isTimeInFilename() {
		return timeInFilename;
	}

	public void setTimeInFilename(boolean timeInFilename) {
		this.timeInFilename = timeInFilename;
	}

	public boolean isSpecifyFormat() {
		return SpecifyFormat;
	}

	public void setSpecifyFormat(boolean specifyFormat) {
		SpecifyFormat = specifyFormat;
	}

	public String getDateTimeFormat() {
		return dateTimeFormat;
	}

	public void setDateTimeFormat(String dateTimeFormat) {
		this.dateTimeFormat = dateTimeFormat;
	}

	public String getSheetname() {
		return sheetname;
	}

	public void setSheetname(String sheetname) {
		this.sheetname = sheetname;
	}

	public boolean isAutoSizeColumns() {
		return autoSizeColumns;
	}

	public void setAutoSizeColumns(boolean autoSizeColumns) {
		this.autoSizeColumns = autoSizeColumns;
	}

	public boolean isNullIsBlank() {
		return nullIsBlank;
	}

	public void setNullIsBlank(boolean nullIsBlank) {
		this.nullIsBlank = nullIsBlank;
	}

	public boolean isProtectsheet() {
		return protectsheet;
	}

	public void setProtectsheet(boolean protectsheet) {
		this.protectsheet = protectsheet;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getSplitEvery() {
		return splitEvery;
	}

	public void setSplitEvery(int splitEvery) {
		this.splitEvery = splitEvery;
	}

	public boolean isUsetempfiles() {
		return usetempfiles;
	}

	public void setUsetempfiles(boolean usetempfiles) {
		this.usetempfiles = usetempfiles;
	}

	public String getTempdirectory() {
		return tempdirectory;
	}

	public void setTempdirectory(String tempdirectory) {
		this.tempdirectory = tempdirectory;
	}

	public boolean isTemplateEnabled() {
		return templateEnabled;
	}

	public void setTemplateEnabled(boolean templateEnabled) {
		this.templateEnabled = templateEnabled;
	}

	public boolean isTemplateAppend() {
		return templateAppend;
	}

	public void setTemplateAppend(boolean templateAppend) {
		this.templateAppend = templateAppend;
	}

	public String getTemplateFileName() {
		return templateFileName;
	}

	public void setTemplateFileName(String templateFileName) {
		this.templateFileName = templateFileName;
	}

	public List<ExcelOutputExcelFieldDto> getOutputFields() {
		return outputFields;
	}

	public void setOutputFields(List<ExcelOutputExcelFieldDto> outputFields) {
		this.outputFields = outputFields;
	}

	public String getHeaderFontName() {
		return headerFontName;
	}

	public void setHeaderFontName(String headerFontName) {
		this.headerFontName = headerFontName;
	}

	public String getHeaderFontSize() {
		return headerFontSize;
	}

	public void setHeaderFontSize(String headerFontSize) {
		this.headerFontSize = headerFontSize;
	}

	public boolean isHeaderFontBold() {
		return headerFontBold;
	}

	public void setHeaderFontBold(boolean headerFontBold) {
		this.headerFontBold = headerFontBold;
	}

	public boolean isHeaderFontItalic() {
		return headerFontItalic;
	}

	public void setHeaderFontItalic(boolean headerFontItalic) {
		this.headerFontItalic = headerFontItalic;
	}

	public String getHeaderFontUnderline() {
		return headerFontUnderline;
	}

	public void setHeaderFontUnderline(String headerFontUnderline) {
		this.headerFontUnderline = headerFontUnderline;
	}

	public String getHeaderFontOrientation() {
		return headerFontOrientation;
	}

	public void setHeaderFontOrientation(String headerFontOrientation) {
		this.headerFontOrientation = headerFontOrientation;
	}

	public String getHeaderFontColor() {
		return headerFontColor;
	}

	public void setHeaderFontColor(String headerFontColor) {
		this.headerFontColor = headerFontColor;
	}

	public String getHeaderBackgroundColor() {
		return headerBackgroundColor;
	}

	public void setHeaderBackgroundColor(String headerBackgroundColor) {
		this.headerBackgroundColor = headerBackgroundColor;
	}

	public String getHeaderRowHeight() {
		return headerRowHeight;
	}

	public void setHeaderRowHeight(String headerRowHeight) {
		this.headerRowHeight = headerRowHeight;
	}

	public String getHeaderAlignment() {
		return headerAlignment;
	}

	public void setHeaderAlignment(String headerAlignment) {
		this.headerAlignment = headerAlignment;
	}

	public String getHeaderImage() {
		return headerImage;
	}

	public void setHeaderImage(String headerImage) {
		this.headerImage = headerImage;
	}

	public String getRowFontName() {
		return rowFontName;
	}

	public void setRowFontName(String rowFontName) {
		this.rowFontName = rowFontName;
	}

	public String getRowFontSize() {
		return rowFontSize;
	}

	public void setRowFontSize(String rowFontSize) {
		this.rowFontSize = rowFontSize;
	}

	public String getRowFontColor() {
		return rowFontColor;
	}

	public void setRowFontColor(String rowFontColor) {
		this.rowFontColor = rowFontColor;
	}

	public String getRowBackgroundColor() {
		return rowBackgroundColor;
	}

	public void setRowBackgroundColor(String rowBackgroundColor) {
		this.rowBackgroundColor = rowBackgroundColor;
	}

	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);
		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("outputFields", ExcelOutputExcelFieldDto.class);
		return (SPExcelOutput) JSONObject.toBean(jsonObj, SPExcelOutput.class, classMap);
	}

	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPExcelOutput spExcelOutput = new SPExcelOutput();
		ExcelOutputMeta exceloutputmeta = (ExcelOutputMeta) stepMetaInterface;

		spExcelOutput.setFileName(FilePathUtil.getRelativeFileName(null, exceloutputmeta.getFileName(), FileType.input));
		spExcelOutput.setEncoding(exceloutputmeta.getEncoding());
		spExcelOutput.setExtension(exceloutputmeta.getExtension());
		spExcelOutput.setPassword(exceloutputmeta.getPassword());
		spExcelOutput.setSheetname(exceloutputmeta.getSheetname());
		spExcelOutput.setTempdirectory(exceloutputmeta.getTempDirectory());
		spExcelOutput.setSplitEvery(exceloutputmeta.getSplitEvery());
		ExcelField[] outputFieldsArray = exceloutputmeta.getOutputFields();
		if (outputFieldsArray != null) {
			List<ExcelOutputExcelFieldDto> outputFieldsList = Arrays.asList(outputFieldsArray).stream().map(temp1 -> {
				ExcelOutputExcelFieldDto temp2 = new ExcelOutputExcelFieldDto();
				temp2.setName(temp1.getName());
				temp2.setTypedesc(temp1.getTypeDesc());
				temp2.setFormat(temp1.getFormat());
				return temp2;
			}).collect(Collectors.toList());
			spExcelOutput.setOutputFields(outputFieldsList);
		}
		spExcelOutput.setDateTimeFormat(exceloutputmeta.getDateTimeFormat());
		spExcelOutput.setTemplateFileName(exceloutputmeta.getTemplateFileName());
		spExcelOutput.setHeaderFontName( ExcelOutputMeta.getFontNameDesc( exceloutputmeta.getHeaderFontName()));
		spExcelOutput.setHeaderFontUnderline( ExcelOutputMeta.getFontUnderlineDesc( exceloutputmeta.getHeaderFontUnderline()));
		spExcelOutput.setHeaderFontOrientation( ExcelOutputMeta.getFontOrientationDesc( exceloutputmeta.getHeaderFontOrientation()));
		spExcelOutput.setHeaderAlignment( ExcelOutputMeta.getFontAlignmentDesc( exceloutputmeta.getHeaderAlignment()));
		spExcelOutput.setHeaderFontColor(ExcelOutputMeta.getFontColorDesc( exceloutputmeta.getHeaderFontColor()));
		spExcelOutput.setHeaderBackgroundColor(ExcelOutputMeta.getFontColorDesc( exceloutputmeta.getHeaderBackGroundColor()) );
		spExcelOutput.setRowBackgroundColor( ExcelOutputMeta.getFontColorDesc( exceloutputmeta.getRowBackGroundColor()));
		spExcelOutput.setHeaderFontSize(exceloutputmeta.getHeaderFontSize());
		spExcelOutput.setHeaderRowHeight(exceloutputmeta.getHeaderRowHeight());
		spExcelOutput.setRowFontName( ExcelOutputMeta.getFontNameDesc( exceloutputmeta.getRowFontName()));
		spExcelOutput.setRowFontColor( ExcelOutputMeta.getFontColorDesc( exceloutputmeta.getRowFontColor()));
		spExcelOutput.setRowFontSize(exceloutputmeta.getRowFontSize());
		spExcelOutput.setHeaderImage(exceloutputmeta.getHeaderImage());
		spExcelOutput.setDateInFilename(exceloutputmeta.isDateInFilename());
		spExcelOutput.setFooterEnabled(exceloutputmeta.isFooterEnabled());
		spExcelOutput.setHeaderEnabled(exceloutputmeta.isHeaderEnabled());
		spExcelOutput.setSpecifyFormat(exceloutputmeta.isSpecifyFormat());
		spExcelOutput.setTimeInFilename(exceloutputmeta.isTimeInFilename());
		spExcelOutput.setUsetempfiles(exceloutputmeta.isUseTempFiles());
		spExcelOutput.setCreateparentfolder(exceloutputmeta.isCreateParentFolder());
		spExcelOutput.setAutoSizeColumns(exceloutputmeta.isAutoSizeColumns());
		spExcelOutput.setStepNrInFilename(exceloutputmeta.isStepNrInFilename());
		spExcelOutput.setTemplateEnabled(exceloutputmeta.isTemplateEnabled());
		spExcelOutput.setDoNotOpenNewFileInit(exceloutputmeta.isDoNotOpenNewFileInit());
		spExcelOutput.setHeaderFontItalic(exceloutputmeta.isHeaderFontItalic());
		spExcelOutput.setTemplateAppend(exceloutputmeta.isTemplateAppend());
		spExcelOutput.setAppend(exceloutputmeta.isAppend());
		spExcelOutput.setHeaderFontBold(exceloutputmeta.isHeaderFontBold());

		spExcelOutput.setAddToResultFilenames(exceloutputmeta.isAddToResultFiles());
		spExcelOutput.setNullIsBlank(exceloutputmeta.isNullBlank());
		spExcelOutput.setProtectsheet(exceloutputmeta.isSheetProtected());

		return spExcelOutput;
	}

	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases, TransMeta transMeta)
			throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();

		SPExcelOutput spExcelOutput = (SPExcelOutput) po;
		ExcelOutputMeta exceloutputmeta = (ExcelOutputMeta) stepMetaInterface;

		exceloutputmeta.setExtension(spExcelOutput.getExtension());
		exceloutputmeta.setSheetname(spExcelOutput.getSheetname());
		exceloutputmeta.setFileName(FilePathUtil.getRealFileName(null, spExcelOutput.getFileName(), FileType.input));
		exceloutputmeta.setPassword(spExcelOutput.getPassword());
		exceloutputmeta.setFooterEnabled(spExcelOutput.isFooterEnabled());
		exceloutputmeta.setTempDirectory(spExcelOutput.getTempdirectory());
		exceloutputmeta.setNullIsBlank(spExcelOutput.isNullIsBlank());
		exceloutputmeta.setHeaderEnabled(spExcelOutput.isHeaderEnabled());
		exceloutputmeta.setSpecifyFormat(spExcelOutput.isSpecifyFormat());
		exceloutputmeta.setSplitEvery(spExcelOutput.getSplitEvery());
		exceloutputmeta.setProtectSheet(spExcelOutput.isProtectsheet());
		exceloutputmeta.setUseTempFiles(spExcelOutput.isUsetempfiles());
		if (spExcelOutput.getOutputFields() != null) {
			List<ExcelField> outputFieldsList = spExcelOutput.getOutputFields().stream().map(temp1 -> {
				ExcelField temp2 = new ExcelField();
				temp2.setName(temp1.getName());
				temp2.setType(temp1.getTypedesc());
				temp2.setFormat(temp1.getFormat());
				return temp2;
			}).collect(Collectors.toList());
			exceloutputmeta.setOutputFields(outputFieldsList.toArray(new ExcelField[spExcelOutput.getOutputFields().size()]));
		}
		exceloutputmeta.setCreateParentFolder(spExcelOutput.isCreateparentfolder());
		exceloutputmeta.setDateInFilename(spExcelOutput.isDateInFilename());
		exceloutputmeta.setAutoSizeColumns(spExcelOutput.isAutoSizeColumns());
		exceloutputmeta.setDateTimeFormat(spExcelOutput.getDateTimeFormat());
		exceloutputmeta.setStepNrInFilename(spExcelOutput.isStepNrInFilename());
		exceloutputmeta.setTimeInFilename(spExcelOutput.isTimeInFilename());
		exceloutputmeta.setTemplateEnabled(spExcelOutput.isTemplateEnabled());
		exceloutputmeta.setTemplateAppend(spExcelOutput.isAppend());
		exceloutputmeta.setTemplateFileName(spExcelOutput.getTemplateFileName());
		exceloutputmeta.setDoNotOpenNewFileInit(spExcelOutput.isDoNotOpenNewFileInit());
		exceloutputmeta.setHeaderFontName(spExcelOutput.getHeaderFontName());
		exceloutputmeta.setHeaderFontUnderline(spExcelOutput.getHeaderFontUnderline());
		exceloutputmeta.setHeaderFontOrientation(spExcelOutput.getHeaderFontOrientation());
		exceloutputmeta.setHeaderFontColor(ExcelOutputMeta.getFontColorByDesc( spExcelOutput.getHeaderFontColor()) );
		exceloutputmeta.setHeaderBackGroundColor(ExcelOutputMeta.getFontColorByDesc(spExcelOutput.getHeaderBackgroundColor()) );
		exceloutputmeta.setHeaderAlignment(spExcelOutput.getHeaderAlignment());
		exceloutputmeta.setHeaderFontSize(spExcelOutput.getHeaderFontSize());
		exceloutputmeta.setHeaderRowHeight(spExcelOutput.getHeaderRowHeight());
		exceloutputmeta.setHeaderFontItalic(spExcelOutput.isHeaderFontItalic());
		exceloutputmeta.setHeaderFontBold(spExcelOutput.isHeaderFontBold());
		exceloutputmeta.setEncoding(spExcelOutput.getEncoding());
		exceloutputmeta.setAppend(spExcelOutput.isAppend());
		exceloutputmeta.setRowFontName(spExcelOutput.getRowFontName());
		exceloutputmeta.setRowBackGroundColor( ExcelOutputMeta.getFontColorByDesc( spExcelOutput.getRowBackgroundColor() ) );
		exceloutputmeta.setRowFontColor(ExcelOutputMeta.getFontColorByDesc( spExcelOutput.getRowFontColor()) );
		exceloutputmeta.setRowFontSize(spExcelOutput.getRowFontSize());
		exceloutputmeta.setHeaderImage(spExcelOutput.getHeaderImage());

		exceloutputmeta.setAddToResultFiles(spExcelOutput.isAddToResultFilenames());

	}

	@Override
	public int stepType() {
		return 6;
	}

	@Override
	public boolean dealStepMeta(TransMeta transMeta, StepMeta stepMeta, StepMetaInterface stepMetaInterface)
			throws Exception {

		ExcelOutputMeta meta = (ExcelOutputMeta) stepMetaInterface;
		meta.setAppend(true);

		return true;
	}

	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr) throws Exception {

		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		ExcelOutputMeta meta = (ExcelOutputMeta) stepMetaInterface;

		ExcelField[] ofs = meta.getOutputFields();
		Map<String, StepFieldDto> fields = null;
		String[] outFieldNames = null;
		if (outputFields != null && ofs.length > 0) {
			outFieldNames = new String[ofs.length];
			fields = Maps.newHashMap();
			for (int i = 0; i < ofs.length; i++) {
				 ExcelField outfield = ofs[i];

				outFieldNames[i] = outfield.getName();

				StepFieldDto stepFieldDto = new StepFieldDto();
				stepFieldDto.setName(outfield.getName());
				stepFieldDto.setOrigin(stepMeta.getName());
				stepFieldDto.setType(outfield.getTypeDesc());

				fields.put(outfield.getName(), stepFieldDto);
			}
		}

		// 增加系统节点
		fileName = Const.NVL(fileName, meta.getFileName());
		if (StringUtils.isNotBlank(fileName)) {
			
			DataNode fileDataNode = DataNodeUtil.fileNodeParse("Excel", fileName.trim(), meta.getEncoding(), "", "" ) ;
			sdr.addOutputDataNode(fileDataNode);

			// 增加 流节点 和 输出系统节点 的关系
			String from = "转换:" + transMeta.getName() + ",步骤:" + stepMeta.getName();
			List<Relationship> relationships = RelationshipUtil.outputStepRelationship(null, fileDataNode, stepMeta.getName(), from, outFieldNames, outFieldNames) ;
			sdr.getDataRelationship().addAll(relationships);

		}
		
	}

}
