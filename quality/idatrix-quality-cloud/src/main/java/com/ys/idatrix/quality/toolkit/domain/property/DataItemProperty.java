/**
 * 云化数据集成系统 
 * iDatrxi quality
 */
package com.ys.idatrix.quality.toolkit.domain.property;

import java.io.Serializable;

/**
 * DataItemProperty <br/>
 * @author JW
 * @since 2018年1月16日
 * 
 */
public class DataItemProperty   extends BaseProperty  implements Serializable{

	private static final long serialVersionUID = 5127607241010902599L;

	// Type - 类型
	private String fieldType;

	// Length - 长度
	private long fieldLength;

	// Pricision - 精度
	private long fieldPricision;

	// Format - 格式
	private String fieldFormat;

	// Compress - 压缩
	private String compress;

	public DataItemProperty(String name) {
		super(name);
	}
	
	/**
	 * @return fieldType
	 */
	public String getFieldType() {
		return fieldType;
	}

	/**
	 * @param fieldType 要设置的 fieldType
	 */
	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	/**
	 * @return fieldLength
	 */
	public long getFieldLength() {
		return fieldLength;
	}

	/**
	 * @param fieldLength 要设置的 fieldLength
	 */
	public void setFieldLength(long fieldLength) {
		this.fieldLength = fieldLength;
	}

	/**
	 * @return fieldPricision
	 */
	public long getFieldPricision() {
		return fieldPricision;
	}

	/**
	 * @param fieldPricision 要设置的 fieldPricision
	 */
	public void setFieldPricision(long fieldPricision) {
		this.fieldPricision = fieldPricision;
	}

	/**
	 * @return fieldFormat
	 */
	public String getFieldFormat() {
		return fieldFormat;
	}

	/**
	 * @param fieldFormat 要设置的 fieldFormat
	 */
	public void setFieldFormat(String fieldFormat) {
		this.fieldFormat = fieldFormat;
	}

	/**
	 * @return compress
	 */
	public String getCompress() {
		return compress;
	}

	/**
	 * @param compress 要设置的 compress
	 */
	public void setCompress(String compress) {
		this.compress = compress;
	}

}
