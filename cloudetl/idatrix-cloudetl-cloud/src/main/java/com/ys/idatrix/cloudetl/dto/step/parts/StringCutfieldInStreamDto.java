/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.parts;

/**
 * SPStringCut 的 fieldInStream 域 
 * @author JW
 * @since 2017年6月12日
 *
 */
public class StringCutfieldInStreamDto {
	String fieldInStream;
	String fieldOutStream;
	String cutFrom;
	String cutTo;
	/**
	 * @return fieldInStream
	 */
	public String getFieldInStream() {
		return fieldInStream;
	}
	/**
	 * @param fieldInStream 要设置的 fieldInStream
	 */
	public void setFieldInStream(String fieldInStream) {
		this.fieldInStream = fieldInStream;
	}
	/**
	 * @return fieldOutStream
	 */
	public String getFieldOutStream() {
		return fieldOutStream;
	}
	/**
	 * @param fieldOutStream 要设置的 fieldOutStream
	 */
	public void setFieldOutStream(String fieldOutStream) {
		this.fieldOutStream = fieldOutStream;
	}
	/**
	 * @return cutFrom
	 */
	public String getCutFrom() {
		return cutFrom;
	}
	/**
	 * @param cutFrom 要设置的 cutFrom
	 */
	public void setCutFrom(String cutFrom) {
		this.cutFrom = cutFrom;
	}
	/**
	 * @return cutTo
	 */
	public String getCutTo() {
		return cutTo;
	}
	/**
	 * @param cutTo 要设置的 cutTo
	 */
	public void setCutTo(String cutTo) {
		this.cutTo = cutTo;
	}
	
}
