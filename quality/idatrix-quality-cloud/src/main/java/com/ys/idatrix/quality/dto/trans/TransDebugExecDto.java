/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.trans;

import com.ys.idatrix.quality.dto.step.parts.ConditionDto;

import io.swagger.annotations.ApiModel;

/**
 * 转换执行请求
 * (new run configuration)
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("转换debug执行配置信息")
public class TransDebugExecDto {
	
	private String stepName;
	//debug|preview
    private boolean  pausingOnBreakPoint = false; //debug
    private boolean  readingFirstRows  = false ; //preview
    private int rowCount = 10 ;
    private ConditionDto condition;
    
	/**
	 * @return the stepName
	 */
	public String getStepName() {
		return stepName;
	}
	/**
	 * @param  设置 stepName
	 */
	public void setStepName(String stepName) {
		this.stepName = stepName;
	}
	/**
	 * @return the pausingOnBreakPoint
	 */
	public boolean isPausingOnBreakPoint() {
		return pausingOnBreakPoint;
	}
	/**
	 * @param  设置 pausingOnBreakPoint
	 */
	public void setPausingOnBreakPoint(boolean pausingOnBreakPoint) {
		this.pausingOnBreakPoint = pausingOnBreakPoint;
	}
	/**
	 * @return the readingFirstRows
	 */
	public boolean isReadingFirstRows() {
		return readingFirstRows;
	}
	/**
	 * @param  设置 readingFirstRows
	 */
	public void setReadingFirstRows(boolean readingFirstRows) {
		this.readingFirstRows = readingFirstRows;
	}
	/**
	 * @return the rowCount
	 */
	public int getRowCount() {
		return rowCount;
	}
	/**
	 * @param  设置 rowCount
	 */
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}
	/**
	 * @return the condition
	 */
	public ConditionDto getCondition() {
		return condition;
	}
	/**
	 * @param  设置 condition
	 */
	public void setCondition(ConditionDto condition) {
		this.condition = condition;
	}
    

}
