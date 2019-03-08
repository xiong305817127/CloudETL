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
public class InputFieldsDto  implements Serializable {

	private static final long serialVersionUID = -1000244452192563317L;
	
	private String fieldName; //域名
	//"Number", "String", "Date", "Boolean", "Integer", "BigNumber", "Serializable", "Binary", "Timestamp",   "Internet Address"
	private String type = "String"; //域类型
	
	private String format;//域格式
	private int length =-1;
	private int precision=-1;
	
	public InputFieldsDto() {
		super();
	}
	
	public InputFieldsDto(String fieldName) {
		super();
		this.fieldName = fieldName;
	}

	public InputFieldsDto(String fieldName, String type) {
		super();
		this.fieldName = fieldName;
		this.type = type;
	}

	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public int getPrecision() {
		return precision;
	}
	public void setPrecision(int precision) {
		this.precision = precision;
	}

	@Override
	public String toString() {
		return "InputFieldsDto [fieldName=" + fieldName + ", type=" + type + ", format=" + format + ", length=" + length
				+ ", precision=" + precision + "]";
	}
	
}
