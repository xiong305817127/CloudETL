/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.steps.output;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.metrics2.sink.relocated.google.common.collect.Maps;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.value.ValueMetaBase;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.textfileoutput.TextFileField;
import org.pentaho.di.trans.steps.textfileoutput.TextFileOutputData;
import org.pentaho.di.trans.steps.textfileoutput.TextFileOutputMeta;
import org.pentaho.pms.util.Const;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.cloudetl.dto.step.StepFieldDto;
import com.ys.idatrix.cloudetl.dto.step.parts.TextFileFieldDto;
import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.ext.utils.FieldValidator;
import com.ys.idatrix.cloudetl.ext.utils.FilePathUtil;
import com.ys.idatrix.cloudetl.ext.utils.FilePathUtil.FileType;
import com.ys.idatrix.cloudetl.recovery.trans.ResumeStepDataParser;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationship;
import com.ys.idatrix.cloudetl.toolkit.analyzer.trans.step.StepDataRelationshipParser;
import com.ys.idatrix.cloudetl.toolkit.domain.DataNode;
import com.ys.idatrix.cloudetl.toolkit.domain.Relationship;
import com.ys.idatrix.cloudetl.toolkit.utils.DataNodeUtil;
import com.ys.idatrix.cloudetl.toolkit.utils.RelationshipUtil;

import net.sf.json.JSONObject;

/**
 * Step - Text file output(文本文件输出). 转换
 * org.pentaho.di.trans.steps.textfileoutput.TextFileOutputMeta
 * 
 * @author JW
 * @since 05-12-2017
 *
 */
@Component("SPTextFileOutput")
@Scope("prototype")
public class SPTextFileOutput implements StepParameter, StepDataRelationshipParser, ResumeStepDataParser {

	private String fileName;
	private boolean isCommand;
	private boolean servletOutput;
	private boolean createParentFolder;
	private boolean doNotOpenNewFileInit;
	private boolean fileNameInField;
	private String fileNameField;
	private String extention;
	private boolean stepNrInFilename;
	private boolean haspartno;
	private boolean addDate;
	private boolean addTime;
	private boolean specifyFormat;
	private String dateTimeFormat;
	private boolean addToResultFilenames;
	private boolean append;
	private String separator;
	private String enclosure;
	private boolean enclosureForced;
	private boolean enclosureFixDisabled;
	private boolean header;
	private boolean footer;
	private String format;
	private String compression;
	private String encoding;
	private boolean pad;
	private boolean fastDump;
	private int splitevery;
	private String endedLine;
	private List<TextFileFieldDto> fields;

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public void setIsCommand(boolean isCommand) {
		this.isCommand = isCommand;
	}

	public boolean getIsCommand() {
		return isCommand;
	}

	public void setServletOutput(boolean servletOutput) {
		this.servletOutput = servletOutput;
	}

	public boolean getServletOutput() {
		return servletOutput;
	}

	public void setCreateParentFolder(boolean createParentFolder) {
		this.createParentFolder = createParentFolder;
	}

	public boolean getCreateParentFolder() {
		return createParentFolder;
	}

	public void setDoNotOpenNewFileInit(boolean doNotOpenNewFileInit) {
		this.doNotOpenNewFileInit = doNotOpenNewFileInit;
	}

	public boolean getDoNotOpenNewFileInit() {
		return doNotOpenNewFileInit;
	}

	public void setFileNameInField(boolean fileNameInField) {
		this.fileNameInField = fileNameInField;
	}

	public boolean getFileNameInField() {
		return fileNameInField;
	}

	public void setFileNameField(String fileNameField) {
		this.fileNameField = fileNameField;
	}

	public String getFileNameField() {
		return fileNameField;
	}

	public void setExtention(String extention) {
		this.extention = extention;
	}

	public String getExtention() {
		return extention;
	}

	public void setStepNrInFilename(boolean stepNrInFilename) {
		this.stepNrInFilename = stepNrInFilename;
	}

	public boolean getStepNrInFilename() {
		return stepNrInFilename;
	}

	public void setHaspartno(boolean haspartno) {
		this.haspartno = haspartno;
	}

	public boolean getHaspartno() {
		return haspartno;
	}

	public void setAddDate(boolean addDate) {
		this.addDate = addDate;
	}

	public boolean getAddDate() {
		return addDate;
	}

	public void setAddTime(boolean addTime) {
		this.addTime = addTime;
	}

	public boolean getAddTime() {
		return addTime;
	}

	public void setSpecifyFormat(boolean specifyFormat) {
		this.specifyFormat = specifyFormat;
	}

	public boolean getSpecifyFormat() {
		return specifyFormat;
	}

	public void setDateTimeFormat(String dateTimeFormat) {
		this.dateTimeFormat = dateTimeFormat;
	}

	public String getDateTimeFormat() {
		return dateTimeFormat;
	}

	public void setAddToResultFilenames(boolean addToResultFilenames) {
		this.addToResultFilenames = addToResultFilenames;
	}

	public boolean getAddToResultFilenames() {
		return addToResultFilenames;
	}

	public void setAppend(boolean append) {
		this.append = append;
	}

	public boolean getAppend() {
		return append;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public String getSeparator() {
		return separator;
	}

	public void setEnclosure(String enclosure) {
		this.enclosure = enclosure;
	}

	public String getEnclosure() {
		return enclosure;
	}

	public void setEnclosureForced(boolean enclosureForced) {
		this.enclosureForced = enclosureForced;
	}

	public boolean getEnclosureForced() {
		return enclosureForced;
	}

	public void setEnclosureFixDisabled(boolean enclosureFixDisabled) {
		this.enclosureFixDisabled = enclosureFixDisabled;
	}

	public boolean getEnclosureFixDisabled() {
		return enclosureFixDisabled;
	}

	public void setHeader(boolean header) {
		this.header = header;
	}

	public boolean getHeader() {
		return header;
	}

	public void setFooter(boolean footer) {
		this.footer = footer;
	}

	public boolean getFooter() {
		return footer;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getFormat() {
		return format;
	}

	public void setCompression(String compression) {
		this.compression = compression;
	}

	public String getCompression() {
		return compression;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setPad(boolean pad) {
		this.pad = pad;
	}

	public boolean getPad() {
		return pad;
	}

	public void setFastDump(boolean fastDump) {
		this.fastDump = fastDump;
	}

	public boolean getFastDump() {
		return fastDump;
	}

	public void setSplitevery(int splitevery) {
		this.splitevery = splitevery;
	}

	public int getSplitevery() {
		return splitevery;
	}

	public void setEndedLine(String endedLine) {
		this.endedLine = endedLine;
	}

	public String getEndedLine() {
		return endedLine;
	}

	public void setFields(List<TextFileFieldDto> fields) {
		this.fields = fields;
	}

	public List<TextFileFieldDto> getFields() {
		return fields;
	}

	/*
	 * Parser JSON object as a step parameter object.
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);

		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("fields", TextFileFieldDto.class);

		return (SPTextFileOutput) JSONObject.toBean(jsonObj, SPTextFileOutput.class, classMap);
	}

	/*
	 * Encode a step parameter object from step meta.
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPTextFileOutput tfo = new SPTextFileOutput();
		TextFileOutputMeta textFileOutputMeta = (TextFileOutputMeta) stepMetaInterface;

		tfo.setAddDate(textFileOutputMeta.isDateInFilename());
		tfo.setAddTime(textFileOutputMeta.isTimeInFilename());
		tfo.setAddToResultFilenames(textFileOutputMeta.isAddToResultFiles());
		tfo.setAppend(textFileOutputMeta.isFileAppended());
		tfo.setCompression(textFileOutputMeta.getFileCompression());
		tfo.setCreateParentFolder(textFileOutputMeta.isCreateParentFolder());
		tfo.setDateTimeFormat(textFileOutputMeta.getDateTimeFormat());
		tfo.setDoNotOpenNewFileInit(textFileOutputMeta.isDoNotOpenNewFileInit());
		tfo.setEnclosure(textFileOutputMeta.getEnclosure());
		tfo.setEnclosureFixDisabled(textFileOutputMeta.isEnclosureFixDisabled());
		tfo.setEnclosureForced(textFileOutputMeta.isEnclosureForced());
		tfo.setEncoding(textFileOutputMeta.getEncoding());
		tfo.setEndedLine(textFileOutputMeta.getEndedLine());
		tfo.setExtention(textFileOutputMeta.getExtension());
		tfo.setFastDump(textFileOutputMeta.isFastDump());

		List<TextFileFieldDto> jtffs = new ArrayList<>();
		TextFileField[] outputFields = textFileOutputMeta.getOutputFields();
		if (outputFields != null) {
			for (TextFileField field : outputFields) {
				TextFileFieldDto jtff = new TextFileFieldDto();
				jtff.setCurrencyType(field.getCurrencySymbol());
				jtff.setDecimal(field.getDecimalSymbol());
				jtff.setFormat(field.getFormat());
				jtff.setGroup(field.getGroupingSymbol());

				jtff.setName(field.getName());
				jtff.setNullif(field.getNullString());

				jtff.setTrimType(field.getTrimTypeCode());
				jtff.setType(field.getTypeDesc());

				if (field.getLength() != -1)
					jtff.setLength(field.getLength());
				if (field.getPrecision() != -1)
					jtff.setPrecision(field.getPrecision());

				jtffs.add(jtff);
			}
		}
		tfo.setFields(jtffs);
		tfo.setFileName(FilePathUtil.getRelativeFileName(null, textFileOutputMeta.getFileName(), FileType.input));
		tfo.setFileNameField(textFileOutputMeta.getFileNameField());
		tfo.setFileNameInField(textFileOutputMeta.isFileNameInField());
		tfo.setFooter(textFileOutputMeta.isFooterEnabled());
		tfo.setFormat(textFileOutputMeta.getFileFormat());
		tfo.setHaspartno(textFileOutputMeta.isPartNrInFilename());
		tfo.setHeader(textFileOutputMeta.isHeaderEnabled());
		tfo.setIsCommand(textFileOutputMeta.isFileAsCommand());
		tfo.setPad(textFileOutputMeta.isPadded());
		tfo.setSeparator(textFileOutputMeta.getSeparator());
		tfo.setServletOutput(textFileOutputMeta.isServletOutput());
		tfo.setSpecifyFormat(textFileOutputMeta.isSpecifyingFormat());
		tfo.setStepNrInFilename(textFileOutputMeta.isStepNrInFilename());
		tfo.setSplitevery(textFileOutputMeta.getSplitEvery());

		return tfo;
	}

	/*
	 * Decode step parameter object into step meta.
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		TextFileOutputMeta textFileOutputMeta = (TextFileOutputMeta) stepMetaInterface;
		SPTextFileOutput jtfo = (SPTextFileOutput) po;

		if (!Utils.isEmpty(jtfo.getFileName())
				&& (jtfo.getFileName().endsWith("/") || jtfo.getFileName().endsWith("\\"))) {
			throw new KettleException("FileName is illegal!");
		}
		textFileOutputMeta.setFilename(jtfo.getFileName());
		if (!Utils.isEmpty(jtfo.getFileName()) && !jtfo.getFileNameInField()) {
			textFileOutputMeta.setFilename(FilePathUtil.getRealFileName(null,jtfo.getFileName(), FileType.input));
		}

		try {
			textFileOutputMeta.setFileAsCommand(jtfo.getIsCommand());
		} catch (RuntimeException ex) {
			// Forget it since this method maybe override by sub-class that no
			// need this.
		}
		textFileOutputMeta.setServletOutput(jtfo.getServletOutput());
		textFileOutputMeta.setCreateParentFolder(jtfo.getCreateParentFolder());
		textFileOutputMeta.setDoNotOpenNewFileInit(jtfo.getDoNotOpenNewFileInit());
		textFileOutputMeta.setFileNameInField(jtfo.getFileNameInField());
		textFileOutputMeta.setFileNameField(jtfo.getFileNameField());
		textFileOutputMeta.setExtension(jtfo.getExtention());
		textFileOutputMeta.setStepNrInFilename(jtfo.getStepNrInFilename());
		textFileOutputMeta.setPartNrInFilename(jtfo.getHaspartno());
		textFileOutputMeta.setDateInFilename(jtfo.getAddDate());
		textFileOutputMeta.setTimeInFilename(jtfo.getAddTime());
		textFileOutputMeta.setSpecifyingFormat(jtfo.getSpecifyFormat());
		textFileOutputMeta.setDateTimeFormat(jtfo.getDateTimeFormat());
		textFileOutputMeta.setAddToResultFiles(jtfo.getAddToResultFilenames());

		textFileOutputMeta.setFileAppended(jtfo.getAppend());
		textFileOutputMeta.setSeparator(jtfo.getSeparator());
		textFileOutputMeta.setEnclosure(jtfo.getEnclosure());
		textFileOutputMeta.setEnclosureForced(jtfo.getEnclosureForced());
		textFileOutputMeta.setEnclosureFixDisabled(jtfo.getEnclosureFixDisabled());
		textFileOutputMeta.setHeaderEnabled(jtfo.getHeader());
		textFileOutputMeta.setFooterEnabled(jtfo.getFooter());
		textFileOutputMeta.setFileFormat(jtfo.getFormat());
		textFileOutputMeta.setFileCompression(jtfo.getCompression());
		textFileOutputMeta.setEncoding(jtfo.getEncoding());
		textFileOutputMeta.setPadded(jtfo.getPad());
		textFileOutputMeta.setFastDump(jtfo.getFastDump());
		textFileOutputMeta.setSplitEvery(jtfo.getSplitevery());
		textFileOutputMeta.setEndedLine(jtfo.getEndedLine());
		textFileOutputMeta.setNewline(textFileOutputMeta.getNewLine(jtfo.getFormat()));

		List<TextFileFieldDto> jtffs = new ArrayList<>();
		if (jtfo.getFields() != null) {
			jtffs.addAll(jtfo.getFields());
		}
		TextFileField[] outputFields = new TextFileField[jtffs.size()];
		for (int i = 0; i < jtffs.size(); i++) {
			TextFileFieldDto jtff = jtffs.get(i);
			TextFileField field = new TextFileField();
			field.setName(jtff.getName());
			field.setType(jtff.getType());
			field.setFormat(jtff.getFormat());
			field.setCurrencySymbol(jtff.getCurrencyType());
			field.setDecimalSymbol(jtff.getDecimal());
			field.setGroupingSymbol(jtff.getGroup());
			field.setTrimType(ValueMetaBase.getTrimTypeByCode(jtff.getTrimType()));
			field.setNullString(jtff.getNullif());
			field.setLength(FieldValidator.fixedLength(jtff.getLength()));
			field.setPrecision(FieldValidator.fixedPrecision(jtff.getPrecision()));
			outputFields[i] = field;
		}
		textFileOutputMeta.setOutputFields(outputFields);
	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr)
			throws Exception {

		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		TextFileOutputMeta textFileOutputMeta = (TextFileOutputMeta) stepMetaInterface;

		String fileName = textFileOutputMeta.getFileName();

		getStepDataAndRelationship(transMeta, stepMeta, sdr, "Local", fileName);

	}

	protected void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr, String fileType , String fileName) throws Exception {

		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		TextFileOutputMeta textFileOutputMeta = (TextFileOutputMeta) stepMetaInterface;

		// 构建输出域
		TextFileField[] outputFields = textFileOutputMeta.getOutputFields();
		Map<String, StepFieldDto> fields = null;
		String[] outFieldNames = null;
		if (outputFields != null && outputFields.length > 0) {
			outFieldNames = new String[outputFields.length];
			fields = Maps.newHashMap();
			for (int i = 0; i < outputFields.length; i++) {
				TextFileField outfield = outputFields[i];

				outFieldNames[i] = outfield.getName();

				StepFieldDto stepFieldDto = new StepFieldDto();
				stepFieldDto.setName(outfield.getName());
				stepFieldDto.setOrigin(stepMeta.getName());
				stepFieldDto.setType(outfield.getTypeDesc());
				stepFieldDto.setPrecision(outfield.getPrecision() + "");

				fields.put(outfield.getName(), stepFieldDto);
			}
		}

		// 增加数据库系统节点
		fileName = Const.NVL(fileName, textFileOutputMeta.getFileName());
		if (StringUtils.isNotBlank(fileName)) {
			DataNode fileDataNode = DataNodeUtil.fileNodeParse(fileType, fileName.trim(), textFileOutputMeta.getEncoding(),  textFileOutputMeta.getFileCompression(), "" ) ;
			sdr.addOutputDataNode(fileDataNode);
			
			// 增加 流节点 和 输出系统节点 的关系
			String from = "转换:" + transMeta.getName() + ",步骤:" + stepMeta.getName();
			List<Relationship> relationships = RelationshipUtil.outputStepRelationship(null, fileDataNode, stepMeta.getName(), from, outFieldNames, outFieldNames) ;
			sdr.getDataRelationship().addAll(relationships);

		}

	}

	@Override
	public boolean afterSaveHandle(Map<Object, Object> cacheData ,TransMeta transMeta, StepMeta stepMeta,
			StepMetaInterface stepMetaInterface, StepDataInterface stepDataInterface, StepInterface stepInterface)
			throws Exception {
		
//		TextFileOutputMeta meta = (TextFileOutputMeta) stepMetaInterface;
//		TextFileOutput step = (TextFileOutput) stepInterface ;
		TextFileOutputData data = (TextFileOutputData)stepDataInterface;
		if(data.writer != null ) {
			data.writer.flush();
		}
		if(data.out != null) {
			data.out.flush();
		}
		if( data.fos != null) {
			data.fos.flush();
		}
		
		return true;
	}

	
	@Override
	public boolean dealStepMeta(TransMeta transMeta ,StepMeta stepMeta ,StepMetaInterface stepMetaInterface)  throws Exception  {
		
		TextFileOutputMeta meta = (TextFileOutputMeta) stepMetaInterface;
		meta.setFileAppended(true);
		 
		return true;
	}

//	 public Long getLinekeyIndex(StepLinesDto result) {
//			
//			Long rowLine = result.getRowLine();
//			if(rowLine ==  null) {
//				rowLine = result.getLinesOutput();
//			}
//			
//			if(header) {
//				return rowLine-1;
//			}else {
//				return rowLine ;
//			}
//	}
	
	@Override
	public int stepType() {
		return 6;
	}

}
