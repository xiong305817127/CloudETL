/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.job;

import io.swagger.annotations.ApiModel;

/**
 * 转换执行步骤状态
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("调度执行节点状态信息")
public class JobExecEntryStatusDto {

	private String entryName;
	private int errCount;
	private String logText;
	private boolean result;

	/**
	 * @return entryName
	 */
	public String getEntryName() {
		return entryName;
	}

	/**
	 * @param  设置 entryName
	 */
	public void setEntryName(String entryName) {
		this.entryName = entryName;
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

	/**
	 * @return result
	 */
	public boolean isResult() {
		return result;
	}

	/**
	 * @param  设置 result
	 */
	public void setResult(boolean result) {
		this.result = result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JobExecEntryStatusDto other = (JobExecEntryStatusDto) obj;
		if (entryName == null) {
			if (other.entryName != null)
				return false;
		} else if (!entryName.equals(other.entryName))
			return false;
		return true;
	}
	
	

}
