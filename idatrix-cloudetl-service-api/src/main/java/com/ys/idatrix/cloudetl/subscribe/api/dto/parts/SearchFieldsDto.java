/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.subscribe.api.dto.parts;

import java.io.Serializable;

/**
 * SPInsertUpdate 的 searchFields 域,
 * @author JW
 * @since 2017年6月7日
 *
 */
public class SearchFieldsDto  implements Serializable{
	
	private static final long serialVersionUID = -1444337547408775374L;
	
	String outputField; //输出表的列名
	String condition; //比较符，可选 =,<>,<,<=,>,>=,= ~NULL(相等或者为空),LIKE,BETWEEN,IS NULL,IS NOT NULL
	String inputField; //输入的流名
	String inputField2;//BETWEEN操作符时的第二个流名
	
	public SearchFieldsDto() {
		super();
	}
	public SearchFieldsDto(String outputField, String condition) {
		super();
		this.outputField = outputField;
		this.condition = condition;
	}
	public SearchFieldsDto(String outputField, String condition, String inputField) {
		super();
		this.outputField = outputField;
		this.condition = condition;
		this.inputField = inputField;
	}
	public SearchFieldsDto(String outputField, String condition, String inputField, String inputField2) {
		super();
		this.outputField = outputField;
		this.condition = condition;
		this.inputField = inputField;
		this.inputField2 = inputField2;
	}
	public String getOutputField() {
		return outputField;
	}
	public void setOutputField(String outputField) {
		this.outputField = outputField;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public String getInputField() {
		return inputField;
	}
	public void setInputField(String inputField) {
		this.inputField = inputField;
	}
	public String getInputField2() {
		return inputField2;
	}
	public void setInputField2(String inputField2) {
		this.inputField2 = inputField2;
	}
	@Override
	public String toString() {
		return "SearchFieldsDto [outputField=" + outputField + ", condition=" + condition + ", inputField=" + inputField
				+ ", inputField2=" + inputField2 + "]";
	}
	
}
