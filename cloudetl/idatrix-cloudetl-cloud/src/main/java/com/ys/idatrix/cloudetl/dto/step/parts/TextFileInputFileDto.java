/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.parts;

/**
 * SPTextFileInput 的 inputFiles 域,等效 org.pentaho.di.trans.steps.fileinput.BaseFileInputField
 * @author JW
 * @since 2017年5月13日
 *
 */
public class TextFileInputFileDto {

	private String fileName;

	private String fileMask;

	private String excludeFileMask;

	private String fileRequired;

	private String includeSubFolders;
	
	
	private String sourceConfigurationName;


	public void setFileName(String fileName){
		this.fileName = fileName;
	}
	public String getFileName(){
		return this.fileName;
	}
	public void setFileMask(String fileMask){
		this.fileMask = fileMask;
	}
	public String getFileMask(){
		return this.fileMask;
	}
	public void setExcludeFileMask(String excludeFileMask){
		this.excludeFileMask = excludeFileMask;
	}
	public String getExcludeFileMask(){
		return this.excludeFileMask;
	}
	public void setFileRequired(String fileRequired){
		this.fileRequired = fileRequired;
	}
	public String getFileRequired(){
		return this.fileRequired;
	}
	public void setIncludeSubFolders(String includeSubfolders){
		this.includeSubFolders = includeSubfolders;
	}
	public String getIncludeSubFolders(){
		return this.includeSubFolders;
	}
	/**
	 * @return sourceConfigurationName
	 */
	public String getSourceConfigurationName() {
		return sourceConfigurationName;
	}
	/**
	 * @param  设置 sourceConfigurationName
	 */
	public void setSourceConfigurationName(String sourceConfigurationName) {
		this.sourceConfigurationName = sourceConfigurationName;
	}
	
	

}
