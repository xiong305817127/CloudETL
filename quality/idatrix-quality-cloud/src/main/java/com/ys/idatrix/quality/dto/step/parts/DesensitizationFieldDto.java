package com.ys.idatrix.quality.dto.step.parts;

/**
 * SPDesensitization 的 脱敏域
 *
 * @author XH
 * @since 2019年1月8日
 *
 */
public class DesensitizationFieldDto {

	private String fieldInStream;
	private String fieldOutStream;

	private String ruleType; // mask(掩码) , truncation(截断)
	private int startPositon; // 替换开始位置 ,0为第一位, -1为倒数第一位(反向替换),-2为倒数第二位(反向替换) ,...
	private int length; // 替换的长度 , 0为不替换,-1为替换到最后一位
	private String replacement; // 替换的字符串, [掩码]为* , [截断]时设置为空(相当于替换为空)
	private Boolean ignoreSpace; // 替换时忽略空格
	
	
	public String getFieldInStream() {
		return fieldInStream;
	}
	public void setFieldInStream(String fieldInStream) {
		this.fieldInStream = fieldInStream;
	}
	public String getFieldOutStream() {
		return fieldOutStream;
	}
	public void setFieldOutStream(String fieldOutStream) {
		this.fieldOutStream = fieldOutStream;
	}
	public String getRuleType() {
		return ruleType;
	}
	public void setRuleType(String ruleType) {
		this.ruleType = ruleType;
	}
	public int getStartPositon() {
		return startPositon;
	}
	public void setStartPositon(int startPositon) {
		this.startPositon = startPositon;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public String getReplacement() {
		return replacement;
	}
	public void setReplacement(String replacement) {
		this.replacement = replacement;
	}
	public Boolean getIgnoreSpace() {
		return ignoreSpace;
	}
	public void setIgnoreSpace(Boolean ignoreSpace) {
		this.ignoreSpace = ignoreSpace;
	}
	
}
