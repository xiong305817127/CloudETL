/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.step.parts;

/**
 * SPUpdate 的 updateFields 域
 * @author JW
 * @since 2017年6月9日
 *
 */
public class UpdateupdateLookupDto {

	String updateLookup;
	String updateStream;

	/**
	 * @return updateLookup
	 */
	public String getUpdateLookup() {
		return updateLookup;
	}

	/**
	 * @param updateLookup
	 *            要设置的 updateLookup
	 */
	public void setUpdateLookup(String updateLookup) {
		this.updateLookup = updateLookup;
	}

	/**
	 * @return updateStream
	 */
	public String getUpdateStream() {
		return updateStream;
	}

	/**
	 * @param updateStream
	 *            要设置的 updateStream
	 */
	public void setUpdateStream(String updateStream) {
		this.updateStream = updateStream;
	}

}
