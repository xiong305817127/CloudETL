/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.parts;

/**
 * SPReplaceString 的 fieldInStream 域
 * @author JW
 * @since 2017年6月13日
 *
 */
public class ReplaceStringfieldInStreamDto {
	String fieldInStream;
	String fieldOutStream;
	int useRegEx;
	String replaceString;
	String replaceByString;
	boolean setEmptyString;
	String replaceFieldByString;
	int wholeWord;
	int caseSensitive;
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
	 * @return useRegEx
	 */
	public int getUseRegEx() {
		return useRegEx;
	}
	/**
	 * @param useRegEx 要设置的 useRegEx
	 */
	public void setUseRegEx(int useRegEx) {
		this.useRegEx = useRegEx;
	}
	/**
	 * @return replaceString
	 */
	public String getReplaceString() {
		return replaceString;
	}
	/**
	 * @param replaceString 要设置的 replaceString
	 */
	public void setReplaceString(String replaceString) {
		this.replaceString = replaceString;
	}
	/**
	 * @return replaceByString
	 */
	public String getReplaceByString() {
		return replaceByString;
	}
	/**
	 * @param replaceByString 要设置的 replaceByString
	 */
	public void setReplaceByString(String replaceByString) {
		this.replaceByString = replaceByString;
	}
	/**
	 * @return setEmptyString
	 */
	public boolean isSetEmptyString() {
		return setEmptyString;
	}
	/**
	 * @param setEmptyString 要设置的 setEmptyString
	 */
	public void setSetEmptyString(boolean setEmptyString) {
		this.setEmptyString = setEmptyString;
	}
	/**
	 * @return replaceFieldByString
	 */
	public String getReplaceFieldByString() {
		return replaceFieldByString;
	}
	/**
	 * @param replaceFieldByString 要设置的 replaceFieldByString
	 */
	public void setReplaceFieldByString(String replaceFieldByString) {
		this.replaceFieldByString = replaceFieldByString;
	}
	/**
	 * @return wholeWord
	 */
	public int getWholeWord() {
		return wholeWord;
	}
	/**
	 * @param wholeWord 要设置的 wholeWord
	 */
	public void setWholeWord(int wholeWord) {
		this.wholeWord = wholeWord;
	}
	/**
	 * @return caseSensitive
	 */
	public int getCaseSensitive() {
		return caseSensitive;
	}
	/**
	 * @param caseSensitive 要设置的 caseSensitive
	 */
	public void setCaseSensitive(int caseSensitive) {
		this.caseSensitive = caseSensitive;
	}
	
}
