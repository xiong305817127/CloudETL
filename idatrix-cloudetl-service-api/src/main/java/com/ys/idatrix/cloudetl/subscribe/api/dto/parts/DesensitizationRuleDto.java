package com.ys.idatrix.cloudetl.subscribe.api.dto.parts;

import java.io.Serializable;

/**
 * 脱敏规则
 *
 * @author XH
 * @since 2019年1月8日
 *
 */
public class DesensitizationRuleDto implements Serializable {
	
	private static final long serialVersionUID = -3354888458309205887L;
	
	private int startPositon = 0 ; //替换开始位置 ,0为第一位, -1为倒数第一位(反向替换),-2为倒数第二位(反向替换,保留最后一位) ,...
	private int length = -1 ;  //替换的长度   , 0为不替换
	private boolean ignoreSpaces = true ; //替换时忽略空格
	
	//默认 [掩码]为* , [截断]时设置为空(相当于替换为空)
	private String replacement = "*" ; //替换的字符串

	
	public DesensitizationRuleDto() {
		super();
	}
	
	/**
	 * 建立[掩码]规则,默认掩码字符 * 
	 * @param startPositon 起始位置,0为第一位, -1为倒数第一位
	 * @param length 从起始位置开始,处理的长度
	 */
	public DesensitizationRuleDto(int startPositon, int length ) {
		super();
		this.startPositon = startPositon;
		this.length = length;
		this.replacement = "*";
		this.ignoreSpaces = true;
	}
	
	/**
	 * 
	 * @param startPositon 起始位置,0为第一位, -1为倒数第一位
	 * @param length 从起始位置开始,处理的长度
	 * @param replacement [掩码]时传 掩码字符 , [截断]时传 空
	 */
	public DesensitizationRuleDto(int startPositon, int length,  String replacement) {
		super();
		this.startPositon = startPositon;
		this.length = length;
		this.replacement = replacement;
		this.ignoreSpaces = true;
	}
	
	
	/**
	 * @param startPositon 0为第一位, -1为倒数第一位
	 * @param endPositon 0为第一位, -1为倒数第一位
	 * @param ignoreSpaces
	 * @param replacement
	 */
	public DesensitizationRuleDto(int startPositon, int length, boolean ignoreSpaces, String replacement) {
		super();
		this.startPositon = startPositon;
		this.length = length;
		this.ignoreSpaces = ignoreSpaces;
		this.replacement = replacement;
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

	public boolean isIgnoreSpaces() {
		return ignoreSpaces;
	}

	public void setIgnoreSpaces(boolean ignoreSpaces) {
		this.ignoreSpaces = ignoreSpaces;
	}

	public String getReplacement() {
		return replacement;
	}

	public void setReplacement(String replacement) {
		this.replacement = replacement;
	}

	@Override
	public String toString() {
		return "DesensitizationRuleDto [startPositon=" + startPositon + ", length=" + length + ", ignoreSpaces="
				+ ignoreSpaces + ", replacement=" + replacement + "]";
	}
	
}
