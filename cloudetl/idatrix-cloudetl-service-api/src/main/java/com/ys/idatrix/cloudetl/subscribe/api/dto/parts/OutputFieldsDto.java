/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.subscribe.api.dto.parts;

import java.io.Serializable;

/**
 * SPInsertUpdate 的 updateFields 域,
 * @author JW
 * @since 2017年6月7日
 *
 */
public class OutputFieldsDto  implements Serializable{

	private static final long serialVersionUID = -8005828577757146524L;
	
	String outputField; //输出表的列名
	String inputField; //输入的流名
	
	//插入更新时有效
	boolean update = true;//插入更新时是否更新, false:当插入时需增加但是更新时不需要修改，如主键
	
	//脱敏规则
	DesensitizationRuleDto desensitizationRule;
	
	public OutputFieldsDto() {
		super();
	}
	public OutputFieldsDto(String outputField, String inputField) {
		super();
		this.outputField = outputField;
		this.inputField = inputField;
	}
	public OutputFieldsDto(String outputField, String inputField, boolean update) {
		super();
		this.outputField = outputField;
		this.inputField = inputField;
		this.update = update;
	}
	public String getOutputField() {
		return outputField;
	}
	public void setOutputField(String outputField) {
		this.outputField = outputField;
	}
	public String getInputField() {
		return inputField;
	}
	public void setInputField(String inputField) {
		this.inputField = inputField;
	}
	public boolean isUpdate() {
		return update;
	}
	public void setUpdate(boolean update) {
		this.update = update;
	}
	
	
	public DesensitizationRuleDto getDesensitizationRule() {
		return desensitizationRule;
	}
	public void setDesensitizationRule(DesensitizationRuleDto desensitizationRule) {
		this.desensitizationRule = desensitizationRule;
	}

	/**
	 * 建立[掩码]规则,默认掩码字符 * 
	 * @param startPositon 起始位置,0为第一位, -1为倒数第一位,-2为倒数第二位 ,...
	 * @param length 从起始位置开始,处理的长度,0为不替换,-1为替换到最后一位
	 * @return
	 */
	public OutputFieldsDto addMaskRule(int startPositon, int length ) {
		this.desensitizationRule = new DesensitizationRuleDto(startPositon, length);
		return this ;
	}
	
	/**
	 * 建立[截断]规则
	 * @param startPositon 起始位置,0为第一位, -1为倒数第一位,-2为倒数第二位 ,...
	 * @param length 从起始位置开始,处理的长度,0为不替换,-1为替换到最后一位
	 * @return
	 */
	public OutputFieldsDto addTruncationRule(int startPositon, int length ) {
		this.desensitizationRule = new DesensitizationRuleDto(startPositon, length,"");
		return this ;
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((inputField == null) ? 0 : inputField.hashCode());
		result = prime * result + ((outputField == null) ? 0 : outputField.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OutputFieldsDto other = (OutputFieldsDto) obj;
		if (inputField == null) {
			if (other.inputField != null)
				return false;
		} else if (!inputField.equals(other.inputField))
			return false;
		if (outputField == null) {
			if (other.outputField != null)
				return false;
		} else if (!outputField.equals(other.outputField))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "OutputFieldsDto [outputField=" + outputField + ", inputField=" + inputField + ", update=" + update
				+ ", desensitizationRule=" + desensitizationRule + "]";
	}
	
	
}
