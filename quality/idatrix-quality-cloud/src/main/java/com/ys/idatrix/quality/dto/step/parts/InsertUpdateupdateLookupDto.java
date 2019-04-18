/**
 * 云化数据集成系统 iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step.parts;

/**
 * SPInsertUpdate 的 updateFields 域,
 * @author JW
 * @since 2017年6月7日
 *
 */
public class InsertUpdateupdateLookupDto {

	String updateLookup;
	String updateStream;
	boolean update =false;
	/**
	 * @return updateLookup
	 */
	public String getUpdateLookup() {
		return updateLookup;
	}
	/**
	 * @param updateLookup 要设置的 updateLookup
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
	 * @param updateStream 要设置的 updateStream
	 */
	public void setUpdateStream(String updateStream) {
		this.updateStream = updateStream;
	}
	/**
	 * @return update
	 */
	public Boolean getUpdate() {
		return update;
	}
	/**
	 * @param update 要设置的 update
	 */
	public void setUpdate(Boolean update) {
		this.update = update;
	}
	
	
	
}
