package com.ys.idatrix.cloudetl.service.step;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.pentaho.di.core.logging.CloudLogListener;
import org.pentaho.di.core.row.value.ValueMetaBase;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.job.IncrementalParser;
import org.pentaho.di.trans.steps.excelinput.SpreadSheetType;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.ys.idatrix.cloudetl.dto.step.parts.AccessInputAccessInputFieldDto;
import com.ys.idatrix.cloudetl.dto.step.parts.AdditionalOutputFieldsDto;
import com.ys.idatrix.cloudetl.dto.step.parts.CsvInputTextFileInputFieldDto;
import com.ys.idatrix.cloudetl.dto.step.parts.ExcelInputExcelInputFieldDto;
import com.ys.idatrix.cloudetl.dto.step.parts.ExcelInputsheetNameDto;
import com.ys.idatrix.cloudetl.dto.step.parts.TextFileInputContentDto;
import com.ys.idatrix.cloudetl.dto.step.parts.TextFileInputFieldDto;
import com.ys.idatrix.cloudetl.dto.step.steps.StepParameter;
import com.ys.idatrix.cloudetl.dto.step.steps.flow.SPFilterRows;
import com.ys.idatrix.cloudetl.dto.step.steps.input.SPAccessInput;
import com.ys.idatrix.cloudetl.dto.step.steps.input.SPCsvInput;
import com.ys.idatrix.cloudetl.dto.step.steps.input.SPExcelInput;
import com.ys.idatrix.cloudetl.dto.step.steps.input.SPGetFileNames;
import com.ys.idatrix.cloudetl.dto.step.steps.input.SPGetVariable;
import com.ys.idatrix.cloudetl.dto.step.steps.input.SPReadContentInput;
import com.ys.idatrix.cloudetl.dto.step.steps.input.SPTextFileInput;
import com.ys.idatrix.cloudetl.dto.step.steps.script.SPScriptValueMod;
import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.InputFieldsDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.RowConditionDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.FileInputDto;
import com.ys.idatrix.cloudetl.subscribe.api.dto.step.FilterRowsDto;

@Component
@Scope("prototype")
public class FileInputService  extends StepServiceInterface<FileInputDto> {

	/**
	 * 创建文件类型的 StepParameter对象 <br>
	 * 当类型为 Excel, Text, ReadType 时 需要传参 上一步骤名 fileStepName
	 */
	@Override
	public Object createParameter(Object... params) throws Exception {

		FileInputDto fileInput = getStepDto();
		String fileStepName = (String) getParam(0, params);
				
		StepParameter fileInputStep = null;
		switch (fileInput.getType()) {
		case FileInputDto.CsvType:
			SPCsvInput csvInput = new SPCsvInput();

			csvInput.setFilenameField("uri");
			csvInput.setIncludingFilename(true);
			csvInput.setDelimiter(fileInput.getTextSeparator());
			csvInput.setEncoding(fileInput.getTextEncoding());
			csvInput.setHeaderPresent(fileInput.isTextHeader());

			if (fileInput.getFields() != null) {
				List<CsvInputTextFileInputFieldDto> tfifs = Lists.newArrayList();
				for (InputFieldsDto ifd : fileInput.getFields()) {
					CsvInputTextFileInputFieldDto eif = new CsvInputTextFileInputFieldDto();
					eif.setName(ifd.getFieldName());
					eif.setType(ValueMetaBase.getType(ifd.getType()));
					eif.setFormat(ifd.getFormat());
					eif.setLength(ifd.getLength());
					eif.setPrecision(ifd.getPrecision());
					tfifs.add(eif);
				}
				csvInput.setInputFields(tfifs);
			}
			
			fileInputStep = csvInput;
			break;
		case FileInputDto.AccessType:
			SPAccessInput ai = new SPAccessInput();

			ai.setFilefield(true);
			ai.setDynamicFilenameField("uri");
			ai.setTableName(fileInput.getAccessTable());

			if (fileInput.getFields() != null) {
				List<AccessInputAccessInputFieldDto> aiaifs = Lists.newArrayList();
				for (InputFieldsDto ifd : fileInput.getFields()) {
					AccessInputAccessInputFieldDto eif = new AccessInputAccessInputFieldDto();
					eif.setName(ifd.getFieldName());
					eif.setColumn(ifd.getFieldName());
					eif.setTypedesc(ValueMetaBase.getType(ifd.getType()));
					eif.setFormat(ifd.getFormat());
					eif.setLength(ifd.getLength());
					eif.setPrecision(ifd.getPrecision());
					aiaifs.add(eif);
				}
				ai.setInputFields(aiaifs);
			}
			
			fileInputStep = ai;
			break;
		case FileInputDto.ExcelType:
			SPExcelInput ei = new SPExcelInput();
			
			ei.setAcceptingFilenames(true);
			ei.setAcceptingStepName(fileStepName);
			ei.setAcceptingField("uri");
			ei.setSpreadSheetType(SpreadSheetType.valueOf(fileInput.getExcelType()));
			if (fileInput.getFields() != null) {
				List<ExcelInputExcelInputFieldDto> eieifs = Lists.newArrayList();
				for (InputFieldsDto ifd : fileInput.getFields()) {
					ExcelInputExcelInputFieldDto eif = new ExcelInputExcelInputFieldDto();
					eif.setName(ifd.getFieldName());
					eif.setTypedesc(ifd.getType());
					eif.setFormat(ifd.getFormat());
					eif.setLength(ifd.getLength());
					eif.setPrecision(ifd.getPrecision());
					eieifs.add(eif);
				}
				ei.setField(eieifs);
			}
			if (fileInput.getExcelsheetName() != null) {
				List<ExcelInputsheetNameDto> sns = Lists.newArrayList();
				for (String name : fileInput.getExcelsheetName()) {
					ExcelInputsheetNameDto eisn = new ExcelInputsheetNameDto();
					eisn.setSheetName(name);
					sns.add(eisn);
				}
				ei.setSheetName(sns);
			}
			
			fileInputStep = ei;
			break;
		case FileInputDto.TextType:
			SPTextFileInput tfi = new SPTextFileInput();

			tfi.setAcceptingFilenames(true);
			tfi.setAcceptingStepName(fileStepName);
			tfi.setAcceptingField("uri");
			tfi.setPassingThruFields(true);
			tfi.setAdditionalOutputFields(new AdditionalOutputFieldsDto());
			if (fileInput.getFields() != null) {
				List<TextFileInputFieldDto> tfifs = Lists.newArrayList();
				for (InputFieldsDto ifd : fileInput.getFields()) {
					TextFileInputFieldDto eif = new TextFileInputFieldDto();
					eif.setName(ifd.getFieldName());
					eif.setType(ifd.getType());
					eif.setFormat(ifd.getFormat());
					eif.setLength(ifd.getLength());
					eif.setPrecision(ifd.getPrecision());
					tfifs.add(eif);
				}
				tfi.setFields(tfifs);
			}

			TextFileInputContentDto tfic = new TextFileInputContentDto();
			tfic.setSeparator(fileInput.getTextSeparator());
			tfic.setEncoding(fileInput.getTextEncoding());
			tfic.setHeader(fileInput.isTextHeader());
			tfi.setContent(tfic);
			
			fileInputStep = tfi;
			break;
		
		case FileInputDto.PDFType:
		case FileInputDto.WordType:
		case FileInputDto.PPTType: 
		case FileInputDto.ReadType: 
			SPReadContentInput sreadContent = new SPReadContentInput();

			sreadContent.setType(FileInputDto.ReadType.equals(fileInput.getType())? "" : fileInput.getType() );
			sreadContent.setContentFieldName(fileInput.getContentFieldName());

			sreadContent.setAcceptingFilenames(true);
			sreadContent.setAcceptingField("uri");
			sreadContent.setAcceptingStepName(fileStepName);
			sreadContent.setEncoding(fileInput.getTextEncoding());

			sreadContent.setIncludeFileName(true);
			sreadContent.setIncludeOnlyFileName(true);
			sreadContent.setFileNameFieldName("short_filename");

			fileInputStep = sreadContent;
			break;
		}
		return fileInputStep;
	}

	@Override
	public List<String> addCurStepToMeta(String transName, String group, Map<String, String> params)
			throws Exception {
		List<String> result = new ArrayList<String>();
		FileInputDto fileInput = getStepDto();
		
		String fileInputName = fileInput.getType();
		String incremental = fileInput.getIncremental();
		String startVarName = IncrementalParser.DEFAULT_START_LIMIT_VAR_NAME;
		String endVarName = IncrementalParser.INCREMENTAL_FIELD;

		
		if (fileInput.getFiles() == null) {
			return result;
		}
		String fileInputStepName;
		// 创建获取文件名
		String getFileName = "file-GetFileNames";
		SPGetFileNames gfn = stepService.createGetFileNames(fileInput.getFiles(), fileInput.getSourceType(),
				fileInput.getFileMask(), fileInput.getExcludeFileMask(), fileInput.isIncludeSubFolders() ,FileInputDto.ReadType.equals(fileInput.getType())? false : true);
		// 增加到TransMeta
		stepService.addAndUpdateStepMeta(transName, group, getFileName, "GetFileNames", gfn);
		result.add(getFileName);
		fileInputStepName = getFileName;

		if (!Utils.isEmpty(incremental)) {
			// 需要增量处理
			// 创建获取变量
			String getVarName = "file-getVarName";
			SPGetVariable getVar = stepService.createGetVariable(new String[] { endVarName, startVarName }, new String[] { startVarName, startVarName });
			// 增加到TransMeta
			stepService.addAndUpdateStepMeta(transName, group, getVarName, "GetVariable", getVar);
			result.add(getVarName);

			// 创建脚本，获取文件的增量信息
			String scriptName = "file-javascript";
			String scriptStr = "var fmatch = short_filename.match(/" + fileInput.getFileMask() + "/);\n"
					+ "if(fmatch && fmatch[1]){\n" + "  " + endVarName + " = fmatch[1];\n" + "  if('" + incremental
					+ "' == 'date'){\n" + "	" + endVarName + " = str2date(" + endVarName + " , '"
					+ fileInput.getFileNameDateFormat() + "').getTime() \n" + "  }\n" + "}";
			SPScriptValueMod script = stepService.createScript(scriptStr, new String[] { endVarName }, null, true);
			// 增加到TransMeta
			stepService.addAndUpdateStepMeta(transName, group, scriptName, "ScriptValueMod", script);
			result.add(scriptName);

			// 创建过滤器，过滤掉已处理的文件名
			
			
			
			String filterName = "file-rowFilter";
			FilterRowsDto fr = new FilterRowsDto();
			RowConditionDto rc = new RowConditionDto(endVarName, ">", startVarName);
			fr.setCondition(rc);
			SPFilterRows filter = (SPFilterRows) stepService.getStepService(fr).createParameter(fileInputName);
			// 增加到TransMeta
			stepService.addAndUpdateStepMeta(transName, group, filterName, "FilterRows", filter);
			result.add(filterName);

			fileInputStepName = filterName;

			// 增加增量设置
			params.put(IncrementalParser.INCREMENTAL_FIELD, IncrementalParser.INCREMENTAL_FIELD);
			params.put(IncrementalParser.INCREMENTAL_INIT_VALUE, "0");
			params.put(IncrementalParser.INCREMENTAL_MAINTRANSNAME, transName);// 主trans

		}

		// 创建文件输入
		String steptype = fileInput.getType() ;
		if (Lists.newArrayList(FileInputDto.PDFType, FileInputDto.WordType,FileInputDto.PPTType, FileInputDto.ReadType).contains(fileInput.getType()) ) {
			steptype = "ReadContentInput" ;
		}
		StepParameter fileInputStep = (StepParameter) createParameter(fileInputStepName);
		// 增加到TransMeta
		stepService.addAndUpdateStepMeta(transName, group, fileInputName, steptype , fileInputStep);
		result.add(fileInputName);
		params.put(CloudLogListener.LOG_INSTEPNAME, fileInputName);

		if (!Utils.isEmpty(incremental)) {
			// 需要增量处理

			// 创建脚本，获取文件的增量信息
			String scriptName = "javascript-add_end_key";
			String scriptStr = "var short_filename = short_filename | uri ;\n var fmatch = short_filename.match(/"
					+ fileInput.getFileMask() + "/);\n" + "if(fmatch && fmatch[1]){\n" + "  " + endVarName
					+ " = fmatch[1];\n" + "  if('" + incremental + "' == 'date'){\n" + "	" + endVarName
					+ " = str2date(" + endVarName + " , '" + fileInput.getFileNameDateFormat() + "').getTime() \n"
					+ "  }\n" + "}";
			SPScriptValueMod script = stepService.createScript(scriptStr, new String[] { endVarName }, null, false);
			// 增加到TransMeta
			stepService.addAndUpdateStepMeta(transName, group, scriptName, "ScriptValueMod", script);
			result.add(scriptName);
		}

		return result;
	}
	
}
