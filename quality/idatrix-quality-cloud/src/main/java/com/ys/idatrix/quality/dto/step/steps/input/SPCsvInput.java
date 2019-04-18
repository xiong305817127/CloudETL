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

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.row.value.ValueMetaBase;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.steps.csvinput.CsvInputMeta;
import org.pentaho.di.trans.steps.textfileinput.TextFileInputField;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ys.idatrix.quality.dto.step.parts.CsvInputTextFileInputFieldDto;
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
 * Step - Csv Input(CSV文件输入). 转换
 * org.pentaho.di.trans.steps.csvinput.CsvInputMeta
 * 
 * @author Xh
 * @since 05-12-2017
 *
 */
@SuppressWarnings("deprecation")
@Component("SPCsvInput")
@Scope("prototype")
public class SPCsvInput implements StepParameter, StepDataRelationshipParser ,ResumeStepDataParser{

	String templeteFile;

	String filename;
	String filenameField;
	String rowNumField;
	boolean includingFilename;
	String delimiter = ",";
	String enclosure = "\"";
	boolean headerPresent = true;
	String bufferSize = "50000";
	boolean lazyConversionActive = true;
	boolean isaddresult;
	boolean runningInParallel;
	boolean newlinePossibleInFields;
	String encoding;
	List<CsvInputTextFileInputFieldDto> inputFields;

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
	 * @return filename
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * @param filename
	 *            要设置的 filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
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
	 * @return rowNumField
	 */
	public String getRowNumField() {
		return rowNumField;
	}

	/**
	 * @param rowNumField
	 *            要设置的 rowNumField
	 */
	public void setRowNumField(String rowNumField) {
		this.rowNumField = rowNumField;
	}

	/**
	 * @return includingFilename
	 */
	public boolean isIncludingFilename() {
		return includingFilename;
	}

	/**
	 * @param includingFilename
	 *            要设置的 includingFilename
	 */
	public void setIncludingFilename(boolean includingFilename) {
		this.includingFilename = includingFilename;
	}

	/**
	 * @return delimiter
	 */
	public String getDelimiter() {
		return delimiter;
	}

	/**
	 * @param delimiter
	 *            要设置的 delimiter
	 */
	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	/**
	 * @return enclosure
	 */
	public String getEnclosure() {
		return enclosure;
	}

	/**
	 * @param enclosure
	 *            要设置的 enclosure
	 */
	public void setEnclosure(String enclosure) {
		this.enclosure = enclosure;
	}

	/**
	 * @return headerPresent
	 */
	public boolean isHeaderPresent() {
		return headerPresent;
	}

	/**
	 * @param headerPresent
	 *            要设置的 headerPresent
	 */
	public void setHeaderPresent(boolean headerPresent) {
		this.headerPresent = headerPresent;
	}

	/**
	 * @return bufferSize
	 */
	public String getBufferSize() {
		return bufferSize;
	}

	/**
	 * @param bufferSize
	 *            要设置的 bufferSize
	 */
	public void setBufferSize(String bufferSize) {
		this.bufferSize = bufferSize;
	}

	/**
	 * @return lazyConversionActive
	 */
	public boolean isLazyConversionActive() {
		return lazyConversionActive;
	}

	/**
	 * @param lazyConversionActive
	 *            要设置的 lazyConversionActive
	 */
	public void setLazyConversionActive(boolean lazyConversionActive) {
		this.lazyConversionActive = lazyConversionActive;
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
	 * @return runningInParallel
	 */
	public boolean isRunningInParallel() {
		return runningInParallel;
	}

	/**
	 * @param runningInParallel
	 *            要设置的 runningInParallel
	 */
	public void setRunningInParallel(boolean runningInParallel) {
		this.runningInParallel = runningInParallel;
	}

	/**
	 * @return newlinePossibleInFields
	 */
	public boolean isNewlinePossibleInFields() {
		return newlinePossibleInFields;
	}

	/**
	 * @param newlinePossibleInFields
	 *            要设置的 newlinePossibleInFields
	 */
	public void setNewlinePossibleInFields(boolean newlinePossibleInFields) {
		this.newlinePossibleInFields = newlinePossibleInFields;
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
	 * @return inputFields
	 */
	public List<CsvInputTextFileInputFieldDto> getInputFields() {
		return inputFields;
	}

	/**
	 * @param inputFields
	 *            要设置的 inputFields
	 */
	public void setInputFields(List<CsvInputTextFileInputFieldDto> inputFields) {
		this.inputFields = inputFields;
	}

	/* 
	 * 
	 */
	@Override
	public Object getParameterObject(Object json) {
		JSONObject jsonObj = JSONObject.fromObject(json);

		Map<String, Class<?>> classMap = new HashMap<>();
		classMap.put("inputFields", CsvInputTextFileInputFieldDto.class);

		return (SPCsvInput) JSONObject.toBean(jsonObj, SPCsvInput.class, classMap);
	}

	/* 
	 * 
	 */
	@Override
	public Object encodeParameterObject(StepMeta stepMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPCsvInput spCsvInput = new SPCsvInput();
		CsvInputMeta csvinputmeta = (CsvInputMeta) stepMetaInterface;

		spCsvInput.setEncoding(csvinputmeta.getEncoding());
		spCsvInput.setDelimiter(csvinputmeta.getDelimiter());

		spCsvInput.setFilename(FilePathUtil.getRelativeFileName(null,csvinputmeta.getFilename(), FileType.input));

		spCsvInput.setFilenameField(csvinputmeta.getFilenameField());
		spCsvInput.setBufferSize(csvinputmeta.getBufferSize());
		spCsvInput.setEnclosure(csvinputmeta.getEnclosure());
		
		if(csvinputmeta.getInputFields()!= null && csvinputmeta.getInputFields().length >0) {
			TextFileInputField[] inputFieldsArray = csvinputmeta.getInputFields();
			List<CsvInputTextFileInputFieldDto> inputFieldsList = Arrays.asList(inputFieldsArray).stream().map(temp1 -> {
				CsvInputTextFileInputFieldDto temp2 = new CsvInputTextFileInputFieldDto();
				temp2.setName(temp1.getName());
				temp2.setType(temp1.getType());
				temp2.setFormat(temp1.getFormat());
				temp2.setCurrencysymbol(temp1.getCurrencySymbol());
				temp2.setDecimalsymbol(temp1.getDecimalSymbol());
				temp2.setGroupsymbol(temp1.getGroupSymbol());
				temp2.setLength(temp1.getLength());
				temp2.setPrecision(temp1.getPrecision());
				temp2.setTrimType(ValueMetaBase.getTrimTypeCode(temp1.getTrimType()));
				return temp2;
			}).collect(Collectors.toList());
			spCsvInput.setInputFields(inputFieldsList);
		}
	
		spCsvInput.setRowNumField(csvinputmeta.getRowNumField());
		spCsvInput.setIncludingFilename(csvinputmeta.isIncludingFilename());
		spCsvInput.setRunningInParallel(csvinputmeta.isRunningInParallel());
		spCsvInput.setNewlinePossibleInFields(csvinputmeta.isNewlinePossibleInFields());
		spCsvInput.setLazyConversionActive(csvinputmeta.isLazyConversionActive());
		spCsvInput.setHeaderPresent(csvinputmeta.isHeaderPresent());
		spCsvInput.setIsaddresult(csvinputmeta.isAddResultFile());

		return spCsvInput;
	}

	/* 
	 * 
	 */
	@Override
	public void decodeParameterObject(StepMeta stepMeta, Object po, List<DatabaseMeta> databases,TransMeta transMeta) throws Exception {
		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		SPCsvInput spCsvInput = (SPCsvInput) po;
		CsvInputMeta csvinputmeta = (CsvInputMeta) stepMetaInterface;

		csvinputmeta.setFilename(FilePathUtil.getRealFileName(null,spCsvInput.getFilename(), FileType.input));
		csvinputmeta.setFilenameField(spCsvInput.getFilenameField());
		csvinputmeta.setRunningInParallel(spCsvInput.isRunningInParallel());
		csvinputmeta.setNewlinePossibleInFields(spCsvInput.isNewlinePossibleInFields());
		csvinputmeta.setLazyConversionActive(spCsvInput.isLazyConversionActive());
		csvinputmeta.setDelimiter(spCsvInput.getDelimiter());
		csvinputmeta.setBufferSize(spCsvInput.getBufferSize());
		csvinputmeta.setHeaderPresent(spCsvInput.isHeaderPresent());
		csvinputmeta.setEnclosure(spCsvInput.getEnclosure());
		
		if(spCsvInput.getInputFields() != null && spCsvInput.getInputFields().size() >0 ) {
			List<TextFileInputField> inputFieldsList = spCsvInput.getInputFields().stream().map(temp1 -> {
				TextFileInputField temp2 = new TextFileInputField();
				temp2.setName(temp1.getName());
				temp2.setType(temp1.getType());
				temp2.setFormat(temp1.getFormat());
				temp2.setCurrencySymbol(temp1.getCurrencysymbol());
				temp2.setDecimalSymbol(temp1.getDecimalsymbol());
				temp2.setGroupSymbol(temp1.getGroupsymbol());
				temp2.setLength(temp1.getLength());
				temp2.setPrecision(temp1.getPrecision());
				temp2.setTrimType(ValueMetaBase.getTrimTypeByCode(temp1.getTrimType()));
				return temp2;
			}).collect(Collectors.toList());
			csvinputmeta.setInputFields(inputFieldsList.toArray(new TextFileInputField[spCsvInput.getInputFields().size()]));
		}
		
		csvinputmeta.setRowNumField(spCsvInput.getRowNumField());
		csvinputmeta.setEncoding(spCsvInput.getEncoding());

		csvinputmeta.setIncludingFilename(spCsvInput.isIncludingFilename());
		csvinputmeta.setAddResultFile(spCsvInput.isIsaddresult());

	}

	/*
	 * 覆盖方法：getStepDataAndRelationship
	 */
	@Override
	public void getStepDataAndRelationship(TransMeta transMeta, StepMeta stepMeta, StepDataRelationship sdr)
			throws Exception {

		StepMetaInterface stepMetaInterface = stepMeta.getStepMetaInterface();
		CsvInputMeta csvInputMeta = (CsvInputMeta) stepMetaInterface;


		String fileName = csvInputMeta.getFilename();
		if (!Utils.isEmpty(fileName)) {
			
			DataNode fileDataNode = DataNodeUtil.fileNodeParse("Csv", fileName.trim(), "", "", "" ) ;
			sdr.addInputDataNode(fileDataNode);
			
			// 增加 系统节点 和 流节点的关系
			String from = "转换:" + transMeta.getName() + ",步骤:" + stepMeta.getName();
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
