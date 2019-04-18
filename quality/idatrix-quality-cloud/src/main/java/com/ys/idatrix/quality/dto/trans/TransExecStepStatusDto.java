/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.trans;

import io.swagger.annotations.ApiModel;

/**
 * 转换执行步骤状态
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("执行转换步骤状态信息")
public class TransExecStepStatusDto {

	private String stepName;
	private int errCount;
	private String logText;

	public void setStepName(String stepName) {
		this.stepName = stepName;
	}

	public String getStepName() {
		return stepName;
	}

	public void setErrCount(int errCount) {
		this.errCount = errCount;
	}

	public int getErrCount() {
		return errCount;
	}

	public void setLogText(String logText) {
		this.logText = logText;
	}

	public String getLogText() {
		return logText;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TransExecStepStatusDto [stepName=" + stepName + ", errCount=" + errCount + ", logText=" + logText + "]";
	}

}
