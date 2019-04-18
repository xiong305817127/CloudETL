/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step.parts;

/**
 *SPTextFileInput 的 errorHandling 域,等效 org.pentaho.di.trans.steps.fileinput.BaseFileInputStepMeta.ErrorHandling
 * @author JW
 * @since 2017年5月13日
 *
 */
public class TextFileInputErrorHandlingDto {

	private boolean errorIgnored;

	private boolean skipBadFiles;

	private String fileErrorField;

	private String fileErrorMessageField;

	private boolean errorLineSkipped;

	private String errorCountField;

	private String errorFieldsField;

	private String errorTextField;

	private String warningFilesDestinationDirectory;

	private String warningFilesExtension;

	private String errorFilesDestinationDirectory;

	private String errorFilesExtension;

	private String lineNumberFilesDestinationDirectory;

	private String lineNumberFilesExtension;

	public void setErrorIgnored(boolean errorIgnored){
		this.errorIgnored = errorIgnored;
	}
	public boolean getErrorIgnored(){
		return this.errorIgnored;
	}
	public void setSkipBadFiles(boolean skipBadFiles){
		this.skipBadFiles = skipBadFiles;
	}
	public boolean getSkipBadFiles(){
		return this.skipBadFiles;
	}
	public void setFileErrorField(String fileErrorField){
		this.fileErrorField = fileErrorField;
	}
	public String getFileErrorField(){
		return this.fileErrorField;
	}
	public void setFileErrorMessageField(String fileErrorMessageField){
		this.fileErrorMessageField = fileErrorMessageField;
	}
	public String getFileErrorMessageField(){
		return this.fileErrorMessageField;
	}
	public void setErrorLineSkipped(boolean errorLineSkipped){
		this.errorLineSkipped = errorLineSkipped;
	}
	public boolean getErrorLineSkipped(){
		return this.errorLineSkipped;
	}
	public void setErrorCountField(String errorCountField){
		this.errorCountField = errorCountField;
	}
	public String getErrorCountField(){
		return this.errorCountField;
	}
	public void setErrorFieldsField(String errorFieldsField){
		this.errorFieldsField = errorFieldsField;
	}
	public String getErrorFieldsField(){
		return this.errorFieldsField;
	}
	public void setErrorTextField(String errorTextField){
		this.errorTextField = errorTextField;
	}
	public String getErrorTextField(){
		return this.errorTextField;
	}
	public void setWarningFilesDestinationDirectory(String warningFilesDestinationDirectory){
		this.warningFilesDestinationDirectory = warningFilesDestinationDirectory;
	}
	public String getWarningFilesDestinationDirectory(){
		return this.warningFilesDestinationDirectory;
	}
	public void setWarningFilesExtension(String warningFilesExtension){
		this.warningFilesExtension = warningFilesExtension;
	}
	public String getWarningFilesExtension(){
		return this.warningFilesExtension;
	}
	public void setErrorFilesDestinationDirectory(String errorFilesDestinationDirectory){
		this.errorFilesDestinationDirectory = errorFilesDestinationDirectory;
	}
	public String getErrorFilesDestinationDirectory(){
		return this.errorFilesDestinationDirectory;
	}
	public void setErrorFilesExtension(String errorFilesExtension){
		this.errorFilesExtension = errorFilesExtension;
	}
	public String getErrorFilesExtension(){
		return this.errorFilesExtension;
	}
	public void setLineNumberFilesDestinationDirectory(String lineNumberFilesDestinationDirectory){
		this.lineNumberFilesDestinationDirectory = lineNumberFilesDestinationDirectory;
	}
	public String getLineNumberFilesDestinationDirectory(){
		return this.lineNumberFilesDestinationDirectory;
	}
	public void setLineNumberFilesExtension(String lineNumberFilesExtension){
		this.lineNumberFilesExtension = lineNumberFilesExtension;
	}
	public String getLineNumberFilesExtension(){
		return this.lineNumberFilesExtension;
	}

}
