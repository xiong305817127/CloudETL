package com.ys.idatrix.cloudetl.subscribe.api.dto.step;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.ys.idatrix.cloudetl.subscribe.api.dto.parts.InputFieldsDto;

public class FileInputDto  extends StepDto implements Serializable{

	private static final long serialVersionUID = -2141760248784099638L;
	
	public static  final String ExcelType ="ExcelInput";
	public static  final String TextType ="TextFileInput";
	public static  final String AccessType ="AccessInput";
	public static  final String CsvType ="CsvInput";
	
	//读取内容
	public static  final String ReadType ="readContent";
	public static  final String WordType ="word";
	public static  final String PDFType ="pdf";
	public static  final String PPTType ="ppt";
	
	private String type = ExcelType ; //TextFileInput,AccessInput,ExcelInput,CsvType,WordType,PDFType,PPTType
	
	private String incremental; //增量方式 ：  可为空 ，为空则表示该任务不增量获取，不为空：可选 date,sequence，表示增量类型 日期/序列(数字型)
	
	private List<String> files;//	文件列表，可为文件夹
	private String fileMask;//	文件名正则匹配，格式:fileName前缀(日期时间/序列)fileName后缀,XXXXX_(\d{8})_.*
	private String fileNameDateFormat ;//	文件名的日期格式，即正则匹配的括号内的日期时间格式,格式:yyyyMMdd,yyyyMMddHHmmss,...
	private String excludeFileMask;//	文件名排除匹配
	private boolean includeSubFolders = true ;//	当是文件夹时，是否搜索子目录
	private String sourceType = "hdfs"	; //文件来源类型,可选 local，hdfs
	
	
	//WordType,PDFType,PPTType
	private String contentFieldName = "content" ;//	读取内容域名	
	//TextFileInput,AccessInput,ExcelInput,CsvType,
	private List<InputFieldsDto> fields; //文件输入域
	
	//TextFileInput 或者 Csv 类型时 有效
	private String textSeparator = ";" ;//	文本文件列分割符
	private String textEncoding = "UTF-8" ;//	文本文件编码
	private boolean textHeader = true ;//	文本文件是否包含头部
	
	//AccessInput 类型时 有效
	private String accessTable;//不可为空，access文件的表名
	
	//ExcelInput 类型时 有效
	private String excelType = "SAX_POI";//	excel引擎类型,可选：POI,JXL,SAX_POI,ODS
	private String[] excelsheetName = new String[] {"Sheet1"};//	excel sheet 名称


	public List<String> getFiles() {
		return files;
	}

	public void setFiles(List<String> files) {
		this.files = files;
	}
	
	public void addFile(String file) {
		if(this.files == null ) {
			this.files = new ArrayList<String>();
		}
		this.files.add(file);
	}

	public String getFileMask() {
		return fileMask;
	}

	public void setFileMask(String fileMask) {
		this.fileMask = fileMask;
	}

	public String getFileNameDateFormat() {
		return fileNameDateFormat;
	}

	public void setFileNameDateFormat(String fileNameDateFormat) {
		this.fileNameDateFormat = fileNameDateFormat;
	}

	public String getExcludeFileMask() {
		return excludeFileMask;
	}

	public void setExcludeFileMask(String excludeFileMask) {
		this.excludeFileMask = excludeFileMask;
	}

	public boolean isIncludeSubFolders() {
		return includeSubFolders;
	}

	public void setIncludeSubFolders(boolean includeSubFolders) {
		this.includeSubFolders = includeSubFolders;
	}
	
	public String getSourceType() {
		return sourceType;
	}

	public void setSourceType(String sourceType) {
		this.sourceType = sourceType;
	}

	public List<InputFieldsDto> getFields() {
		return fields;
	}

	public void setFields(List<InputFieldsDto> fields) {
		this.fields = fields;
	}
	
	public String getContentFieldName() {
		return contentFieldName;
	}

	public void setContentFieldName(String contentFieldName) {
		this.contentFieldName = contentFieldName;
	}

	public void addField(InputFieldsDto field) {
		if(this.fields ==  null) {
			this.fields = new ArrayList<InputFieldsDto>();
		}
		this.fields.add(field);
	}

	public String getTextSeparator() {
		return textSeparator;
	}

	public void setTextSeparator(String textSeparator) {
		this.textSeparator = textSeparator;
	}

	public String getTextEncoding() {
		return textEncoding;
	}

	public void setTextEncoding(String textEncoding) {
		this.textEncoding = textEncoding;
	}

	public boolean isTextHeader() {
		return textHeader;
	}

	public void setTextHeader(boolean textHeader) {
		this.textHeader = textHeader;
	}

	public String getAccessTable() {
		return accessTable;
	}

	public void setAccessTable(String accessTable) {
		this.accessTable = accessTable;
	}

	public String getExcelType() {
		return excelType;
	}

	public void setExcelType(String excelType) {
		this.excelType = excelType;
	}

	public String[] getExcelsheetName() {
		return excelsheetName;
	}

	public void setExcelsheetName(String[] excelsheetName) {
		this.excelsheetName = excelsheetName;
	}

	public static String getExceltype() {
		return ExcelType;
	}

	public static String getTexttype() {
		return TextType;
	}

	public static String getAccesstype() {
		return AccessType;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public boolean isJobStep() {
		return false;
	}
	
	public String getIncremental() {
		return incremental;
	}
	public void setIncremental(String incremental) {
		this.incremental = incremental;
	}
	

	@Override
	public String toString() {
		return "FileInputDto [type=" + type + ", incremental=" + incremental + ", files=" + files + ", fileMask="
				+ fileMask + ", fileNameDateFormat=" + fileNameDateFormat + ", excludeFileMask=" + excludeFileMask
				+ ", includeSubFolders=" + includeSubFolders + ", sourceType=" + sourceType + ", contentFieldName="
				+ contentFieldName + ", fields=" + fields + ", textSeparator=" + textSeparator + ", textEncoding="
				+ textEncoding + ", textHeader=" + textHeader + ", accessTable=" + accessTable + ", excelType="
				+ excelType + ", excelsheetName=" + Arrays.toString(excelsheetName) + ", super=" + super.toString()
				+ "]";
	}

}
