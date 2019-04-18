/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.db;

/**
 * DTO for database connection extra options.
 * @author JW
 * @since 2017年7月17日
 *
 */
public class DbOption {
	
	private String optKey;
	private String optVal;
	
	/**
	 * @return optKey
	 */
	public String getOptKey() {
		return optKey;
	}
	/**
	 * @param optKey 要设置的 optKey
	 */
	public void setOptKey(String optKey) {
		this.optKey = optKey;
	}
	
	/**
	 * @return optVal
	 */
	public String getOptVal() {
		return optVal;
	}
	/**
	 * @param optVal 要设置的 optVal
	 */
	public void setOptVal(String optVal) {
		this.optVal = optVal;
	}
	
	/* 
	 * Build text.
	 */
	@Override
	public String toString() {
		return "DbOption [optKey=" + optKey + ", optVal=" + optVal + "]";
	}
	
}
