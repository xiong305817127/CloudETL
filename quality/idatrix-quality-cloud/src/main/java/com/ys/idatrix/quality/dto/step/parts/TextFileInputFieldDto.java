/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step.parts;

/**
 * SPTextFileInput 的 fields 域,等效 org.pentaho.di.trans.steps.fileinput.BaseFileInputStepMeta.InputFiles<BaseFileInputField>
 * @author JW
 * @since 2017年5月13日
 *
 */
public class TextFileInputFieldDto {

	private String name;
	private String type;
	private String format;
	private String currency;
	private String decimal;
	private String group;
	private String nullif;
	private String ifnull; // By default
	private int position=-1;
	private int length=-1;
	private int precision=-1;
	private String trimType;
	private boolean repeat;

	private String path ;
	
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return this.name;
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public void setType(String type){
		this.type = type;
	}
	public String getType(){
		return this.type;
	}
	
	public void setFormat(String format){
		this.format = format;
	}
	public String getFormat(){
		return this.format;
	}
	
	public void setCurrency(String currency){
		this.currency = currency;
	}
	public String getCurrency(){
		return this.currency;
	}
	
	public void setDecimal(String decimal){
		this.decimal = decimal;
	}
	public String getDecimal(){
		return this.decimal;
	}
	
	public void setGroup(String group){
		this.group = group;
	}
	public String getGroup(){
		return this.group;
	}
	
	public void setNullif(String nullif){
		this.nullif = nullif;
	}
	public String getNullif(){
		return this.nullif;
	}
	
	public void setIfnull(String ifnull){
		this.ifnull = ifnull;
	}
	public String getIfnull(){
		return this.ifnull;
	}
	
	public void setPosition(int position){
		this.position = position;
	}
	public int getPosition(){
		return this.position;
	}
	
	public void setLength(int length){
		this.length = length;
	}
	public int getLength(){
		return this.length;
	}
	
	public void setPrecision(int precision){
		this.precision = precision;
	}
	public int getPrecision(){
		return this.precision;
	}
	
	public void setTrimType(String trimType){
		this.trimType = trimType;
	}
	public String getTrimType(){
		return this.trimType;
	}
	
	public void setRepeat(boolean repeat){
		this.repeat = repeat;
	}
	public boolean getRepeat(){
		return this.repeat;
	}

}
