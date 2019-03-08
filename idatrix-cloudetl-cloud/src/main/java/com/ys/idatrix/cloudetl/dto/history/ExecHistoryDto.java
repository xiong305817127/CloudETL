/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.history;

import java.util.List;

import io.swagger.annotations.ApiModel;

/**
 * TransHistoryDto.java
 * @author JW
 * @since 2017年7月31日
 *
 */
@ApiModel("执行历史信息")
public class ExecHistoryDto {
	
	private String retCode;
	private String message;
	private List<?> records;
	
	/**
	 * @return retCode
	 */
	public String getRetCode() {
		return retCode;
	}
	/**
	 * @param retCode 要设置的 retCode
	 */
	public void setRetCode(String retCode) {
		this.retCode = retCode;
	}
	/**
	 * @return message
	 */
	public String getMessage() {
		return message;
	}
	/**
	 * @param message 要设置的 message
	 */
	public void setMessage(String message) {
		this.message = message;
	}
	/**
	 * @return records
	 */
	public List<?> getRecords() {
		return records;
	}
	/**
	 * @param records 要设置的 records
	 */
	public void setRecords(List<?> records) {
		this.records = records;
	}
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ExecHistoryDto [retCode=" + retCode + ", message=" + message + ", records=" + records + "]";
	} 
	
}
