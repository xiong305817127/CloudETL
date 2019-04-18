/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.parts;

/**
 * SPTextFileInput 的  additionalOutputFields 域,等效 org.pentaho.di.trans.steps.fileinput.BaseFileInputStepMeta.AdditionalOutputFields
 * @author JW
 * @since 2017年5月13日
 *
 */
public class AdditionalOutputFieldsDto {

	private String shortFilenameField = "short_filename";

	private String pathField;

	private String hiddenField;

	private String lastModificationField;

	private String uriField;

	private String rootUriField;

	private String extensionField;

	private String sizeField;

	public void setShortFilenameField(String shortFilenameField){
		this.shortFilenameField = shortFilenameField;
	}
	public String getShortFilenameField(){
		return this.shortFilenameField;
	}
	public void setPathField(String pathField){
		this.pathField = pathField;
	}
	public String getPathField(){
		return this.pathField;
	}
	public void setHiddenField(String hiddenField){
		this.hiddenField = hiddenField;
	}
	public String getHiddenField(){
		return this.hiddenField;
	}
	public void setLastModificationField(String lastModificationField){
		this.lastModificationField = lastModificationField;
	}
	public String getLastModificationField(){
		return this.lastModificationField;
	}
	public void setUriField(String uriField){
		this.uriField = uriField;
	}
	public String getUriField(){
		return this.uriField;
	}
	public void setRootUriField(String rootUriField){
		this.rootUriField = rootUriField;
	}
	public String getRootUriField(){
		return this.rootUriField;
	}
	public void setExtensionField(String extensionField){
		this.extensionField = extensionField;
	}
	public String getExtensionField(){
		return this.extensionField;
	}
	public void setSizeField(String sizeField){
		this.sizeField = sizeField;
	}
	public String getSizeField(){
		return this.sizeField;
	}

}
