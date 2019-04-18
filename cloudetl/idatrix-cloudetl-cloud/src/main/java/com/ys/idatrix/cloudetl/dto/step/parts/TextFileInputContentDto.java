/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.parts;

/**
 * SPTextFileInput 的 Content 域, 等效org.pentaho.di.trans.steps.fileinput.text.TextFileInputMeta.Content
 * @author JW
 * @since 2017年5月13日
 *
 */
public class TextFileInputContentDto {

	private String separator =";";

	private String enclosure = "\"";

	private boolean breakInEnclosureAllowed =false;

	private String escapeCharacter;

	private boolean header = true;

	private int nrHeaderLines =1;

	private boolean footer =false;

	private int nrFooterLines =1;

	private boolean lineWrapped = false;

	private int nrWraps =1;

	private boolean layoutPaged = false;

	private int nrLinesPerPage = 80;

	private int nrLinesDocHeader= 0;

	private boolean noEmptyLines =true;

	private boolean includeFilename = false;

	private String filenameField;

	private boolean includeRowNumber =false;

	private boolean rowNumberByFile = false;

	private String rowNumberField;

	private String fileFormat="DOS";

	private String encoding="UTF-8";

	private int rowLimit=0;

	private String fileType ="CSV";

	private String fileCompression = "None";

	private boolean dateFormatLenient = true;

	private String dateFormatLocale ="zh_CN";

	public void setSeparator(String separator){
		this.separator = separator;
	}
	public String getSeparator(){
		return this.separator;
	}
	public void setEnclosure(String enclosure){
		this.enclosure = enclosure;
	}
	public String getEnclosure(){
		return this.enclosure;
	}
	public void setBreakInEnclosureAllowed(boolean breakInEnclosureAllowed){
		this.breakInEnclosureAllowed = breakInEnclosureAllowed;
	}
	public boolean getBreakInEnclosureAllowed(){
		return this.breakInEnclosureAllowed;
	}
	public void setEscapeCharacter(String escapeCharacter){
		this.escapeCharacter = escapeCharacter;
	}
	public String getEscapeCharacter(){
		return this.escapeCharacter;
	}
	public void setHeader(boolean header){
		this.header = header;
	}
	public boolean getHeader(){
		return this.header;
	}
	public void setNrHeaderLines(int nrHeaderLines){
		this.nrHeaderLines = nrHeaderLines;
	}
	public int getNrHeaderLines(){
		return this.nrHeaderLines;
	}
	public void setFooter(boolean footer){
		this.footer = footer;
	}
	public boolean getFooter(){
		return this.footer;
	}
	public void setNrFooterLines(int nrFooterLines){
		this.nrFooterLines = nrFooterLines;
	}
	public int getNrFooterLines(){
		return this.nrFooterLines;
	}
	public void setLineWrapped(boolean lineWrapped){
		this.lineWrapped = lineWrapped;
	}
	public boolean getLineWrapped(){
		return this.lineWrapped;
	}
	public void setNrWraps(int nrWraps){
		this.nrWraps = nrWraps;
	}
	public int getNrWraps(){
		return this.nrWraps;
	}
	public void setLayoutPaged(boolean layoutPaged){
		this.layoutPaged = layoutPaged;
	}
	public boolean getLayoutPaged(){
		return this.layoutPaged;
	}
	public void setNrLinesPerPage(int nrLinesPerPage){
		this.nrLinesPerPage = nrLinesPerPage;
	}
	public int getNrLinesPerPage(){
		return this.nrLinesPerPage;
	}
	public void setNrLinesDocHeader(int nrLinesDocHeader){
		this.nrLinesDocHeader = nrLinesDocHeader;
	}
	public int getNrLinesDocHeader(){
		return this.nrLinesDocHeader;
	}
	public void setNoEmptyLines(boolean noEmptyLines){
		this.noEmptyLines = noEmptyLines;
	}
	public boolean getNoEmptyLines(){
		return this.noEmptyLines;
	}
	public void setIncludeFilename(boolean includeFilename){
		this.includeFilename = includeFilename;
	}
	public boolean getIncludeFilename(){
		return this.includeFilename;
	}
	public void setFilenameField(String filenameField){
		this.filenameField = filenameField;
	}
	public String getFilenameField(){
		return this.filenameField;
	}
	public void setIncludeRowNumber(boolean includeRowNumber){
		this.includeRowNumber = includeRowNumber;
	}
	public boolean getIncludeRowNumber(){
		return this.includeRowNumber;
	}
	public void setRowNumberByFile(boolean rowNumberByFile){
		this.rowNumberByFile = rowNumberByFile;
	}
	public boolean getRowNumberByFile(){
		return this.rowNumberByFile;
	}
	public void setRowNumberField(String rowNumberField){
		this.rowNumberField = rowNumberField;
	}
	public String getRowNumberField(){
		return this.rowNumberField;
	}
	public void setFileFormat(String fileFormat){
		this.fileFormat = fileFormat;
	}
	public String getFileFormat(){
		return this.fileFormat;
	}
	public void setEncoding(String encoding){
		this.encoding = encoding;
	}
	public String getEncoding(){
		return this.encoding;
	}
	public void setRowLimit(int rowLimit){
		this.rowLimit = rowLimit;
	}
	public int getRowLimit(){
		return this.rowLimit;
	}
	public void setFileType(String fileType){
		this.fileType = fileType;
	}
	public String getFileType(){
		return this.fileType;
	}
	public void setFileCompression(String fileCompression){
		this.fileCompression = fileCompression;
	}
	public String getFileCompression(){
		return this.fileCompression;
	}
	public void setDateFormatLenient(boolean dateFormatLenient){
		this.dateFormatLenient = dateFormatLenient;
	}
	public boolean getDateFormatLenient(){
		return this.dateFormatLenient;
	}
	public void setDateFormatLocale(String dateFormatLocale){
		this.dateFormatLocale = dateFormatLocale;
	}
	public String getDateFormatLocale(){
		return this.dateFormatLocale;
	}

}
