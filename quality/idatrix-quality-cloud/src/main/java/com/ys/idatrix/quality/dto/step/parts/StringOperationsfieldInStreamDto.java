/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step.parts;

/**
 * SPStringOperations 的 fieldInStream 域
 * @author JW
 * @since 2017年6月13日
 *
 */
public class StringOperationsfieldInStreamDto {
	String fieldInStream;
	String fieldOutStream;
	String trimType = "none";
	int lowerUpper;
	int padding_type;
	String padChar;
	String padLen;
	int initCap;
	int maskXML;
	int digits;
	int removeSpecialCharacters;

	/**
	 * @return fieldInStream
	 */
	public String getFieldInStream() {
		return fieldInStream;
	}

	/**
	 * @param fieldInStream
	 *            要设置的 fieldInStream
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
	 * @param fieldOutStream
	 *            要设置的 fieldOutStream
	 */
	public void setFieldOutStream(String fieldOutStream) {
		this.fieldOutStream = fieldOutStream;
	}


	public String getTrimType() {
		return trimType;
	}

	public void setTrimType(String trimType) {
		this.trimType = trimType;
	}

	/**
	 * @return lowerUpper
	 */
	public int getLowerUpper() {
		return lowerUpper;
	}

	/**
	 * @param lowerUpper
	 *            要设置的 lowerUpper
	 */
	public void setLowerUpper(int lowerUpper) {
		this.lowerUpper = lowerUpper;
	}

	/**
	 * @return padding_type
	 */
	public int getPadding_type() {
		return padding_type;
	}

	/**
	 * @param padding_type
	 *            要设置的 padding_type
	 */
	public void setPadding_type(int padding_type) {
		this.padding_type = padding_type;
	}

	/**
	 * @return padChar
	 */
	public String getPadChar() {
		return padChar;
	}

	/**
	 * @param padChar
	 *            要设置的 padChar
	 */
	public void setPadChar(String padChar) {
		this.padChar = padChar;
	}

	/**
	 * @return padLen
	 */
	public String getPadLen() {
		return padLen;
	}

	/**
	 * @param padLen
	 *            要设置的 padLen
	 */
	public void setPadLen(String padLen) {
		this.padLen = padLen;
	}

	/**
	 * @return initCap
	 */
	public int getInitCap() {
		return initCap;
	}

	/**
	 * @param initCap
	 *            要设置的 initCap
	 */
	public void setInitCap(int initCap) {
		this.initCap = initCap;
	}

	/**
	 * @return maskXML
	 */
	public int getMaskXML() {
		return maskXML;
	}

	/**
	 * @param maskXML
	 *            要设置的 maskXML
	 */
	public void setMaskXML(int maskXML) {
		this.maskXML = maskXML;
	}

	/**
	 * @return digits
	 */
	public int getDigits() {
		return digits;
	}

	/**
	 * @param digits
	 *            要设置的 digits
	 */
	public void setDigits(int digits) {
		this.digits = digits;
	}

	/**
	 * @return removeSpecialCharacters
	 */
	public int getRemoveSpecialCharacters() {
		return removeSpecialCharacters;
	}

	/**
	 * @param removeSpecialCharacters
	 *            要设置的 removeSpecialCharacters
	 */
	public void setRemoveSpecialCharacters(int removeSpecialCharacters) {
		this.removeSpecialCharacters = removeSpecialCharacters;
	}

}
