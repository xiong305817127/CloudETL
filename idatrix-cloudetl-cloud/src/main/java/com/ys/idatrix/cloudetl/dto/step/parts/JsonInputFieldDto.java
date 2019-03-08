/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.parts;

/**
 * JsonInput 组件的 inputFields Dto 等效
 * org.pentaho.di.trans.steps.jsoninput.JsonInputField
 * 
 * @author FBZ
 * @since 11-28-2017
 *
 */
public class JsonInputFieldDto {
	private String name; // 名称

	private String path; // 路径

	// 类型, 0: "", 1:Number, 2:String, 3:Date, 4:Boolean, 5:Integer,
	// 6:BigNumber, 7:Serializable, 8:Binary, 9:Timestamp, 10:Internet Address
	private String type;

	private String format; // 格式

	private String length = "-1"; // 长度

	private String precision = "-1"; // 精度

	private String currency; // 货币

	private String decimal; // 十进制

	private String group; // 组

	String trimType = "none";; // 去除空字符的方式; 0: 不去掉空格，1: 去掉左空格, 2:去掉右空格, 3:去掉左右两端空格

	private Boolean repeat = Boolean.FALSE; // 重复, 默认为 否, 可为空

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
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

	public String getLength() {
		return length;
	}

	public void setLength(String length) {
		this.length = length;
	}

	public String getPrecision() {
		return precision;
	}

	public void setPrecision(String precision) {
		this.precision = precision;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getDecimal() {
		return decimal;
	}

	public void setDecimal(String decimal) {
		this.decimal = decimal;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getTrimType() {
		return trimType;
	}

	public void setTrimType(String trimType) {
		this.trimType = trimType;
	}

	public Boolean getRepeat() {
		return repeat;
	}

	public void setRepeat(Boolean repeat) {
		this.repeat = repeat;
	}

}
