/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.parts;

/**
 * SPInsertUpdate 的 searchFields 域,
 * @author JW
 * @since 2017年6月7日
 *
 */
public class InsertUpdatekeyStreamDto {
	String keyStream1;
	String keyLookup;
	String keyCondition;
	String keyStream2;
	/**
	 * @return keyStream
	 */
	public String getKeyStream1() {
		return keyStream1;
	}
	/**
	 * @param keyStream 要设置的 keyStream
	 */
	public void setKeyStream1(String keyStream) {
		this.keyStream1 = keyStream;
	}
	/**
	 * @return keyLookup
	 */
	public String getKeyLookup() {
		return keyLookup;
	}
	/**
	 * @param keyLookup 要设置的 keyLookup
	 */
	public void setKeyLookup(String keyLookup) {
		this.keyLookup = keyLookup;
	}
	/**
	 * @return keyCondition
	 */
	public String getKeyCondition() {
		return keyCondition;
	}
	/**
	 * @param keyCondition 要设置的 keyCondition
	 */
	public void setKeyCondition(String keyCondition) {
		this.keyCondition = keyCondition;
	}
	/**
	 * @return keyStream2
	 */
	public String getKeyStream2() {
		return keyStream2;
	}
	/**
	 * @param keyStream2 要设置的 keyStream2
	 */
	public void setKeyStream2(String keyStream2) {
		this.keyStream2 = keyStream2;
	}

}
