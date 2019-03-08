/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.parts;

/**
 * SPScriptValueMod 的 jsScripts域, 等效 org.pentaho.di.trans.steps.scriptvalues_mod.ScriptValuesScript
 * @author JW
 * @since 05-12-2017
 *
 */
public class JsScriptDto {
	
	private String name;

	private String type;

	private String value;

	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return this.name;
	}
	
	public void setType(String type){
		this.type = type;
	}
	public String getType(){
		return this.type;
	}
	
	public void setValue(String value){
		this.value = value;
	}
	public String getValue(){
		return this.value;
	}

}
