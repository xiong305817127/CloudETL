/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step.parts;

/**
 * SPScriptValueMod 的 fields域,
 * @author JW
 * @since 05-12-2017
 *
 */
public class JsFieldDto {
	
	private String name;

	private String rename;

	private int type;

	private String length="-1";

	private String precision="-1";

	private boolean replace;

	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return this.name;
	}
	
	public void setRename(String rename){
		this.rename = rename;
	}
	public String getRename(){
		return this.rename;
	}
	
	public void setType(int type){
		this.type = type;
	}
	public int getType(){
		return this.type;
	}
	
	public void setLength(String length){
		this.length = length;
	}
	public String getLength(){
		return this.length;
	}
	
	public void setPrecision(String precision){
		this.precision = precision;
	}
	public String getPrecision(){
		return this.precision;
	}
	
	public void setReplace(boolean replace){
		this.replace = replace;
	}
	public boolean getReplace(){
		return this.replace;
	}

}
