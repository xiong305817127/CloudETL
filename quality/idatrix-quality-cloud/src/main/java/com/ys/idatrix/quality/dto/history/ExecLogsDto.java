/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.history;

import io.swagger.annotations.ApiModel;

/**
 * TransLogsDto.java
 * @author JW
 * @since 2017年7月31日
 *
 */
@ApiModel("执行日志信息")
public class ExecLogsDto {
	
	private String retCode;
	private String message;
	private String logs;
	
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
	 * @return logs
	 */
	public String getLogs() {
		return logs;
	}
	/**
	 * @param logs 要设置的 logs
	 */
	public void setLogs(String logs) {
		this.logs = logs;
	}
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ExecLogsDto [retCode=" + retCode + ", message=" + message + ", logs=" + logs + "]";
	}
	
}
