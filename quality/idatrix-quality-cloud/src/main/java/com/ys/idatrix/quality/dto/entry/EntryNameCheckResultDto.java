/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.entry;

/**
 * step name检查结果
 * @author JW
 * @since 05-12-2017
 *
 */
public class EntryNameCheckResultDto {
	
	private boolean existed;
    public void setExisted(boolean existed) {
        this.existed = existed;
    }
    public boolean getExisted() {
        return existed;
    }
    
	/**
	 * @param existed
	 */
	public EntryNameCheckResultDto(boolean existed) {
		super();
		this.existed = existed;
	}
	
	/**
	 * 
	 */
	public EntryNameCheckResultDto() {
		super();
	}
    
}
