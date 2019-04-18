/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.entry;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * step gui 信息
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("调度任务节点位置信息")
public class EntryGuiDto {
	
	@ApiModelProperty("调度任务节点位置x偏移量")
	private int xloc;
	
	@ApiModelProperty("调度任务节点位置y偏移量")
    private int yloc;
	
	@ApiModelProperty("")
    private int nr;
	
	@ApiModelProperty("")
    private String draw;
    
    public void setXloc(int xloc) {
        this.xloc = xloc;
    }
    public int getXloc() {
        return xloc;
    }

    public void setYloc(int yloc) {
        this.yloc = yloc;
    }
    public int getYloc() {
        return yloc;
    }

    public void setDraw(String draw) {
        this.draw = draw;
    }
    public String getDraw() {
        return draw;
    }
    
	/**
	 * @return nr
	 */
	public int getNr() {
		return nr;
	}
	/**
	 * @param  设置 nr
	 */
	public void setNr(int nr) {
		this.nr = nr;
	}
	/* 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "StepGui [xloc=" + xloc + ", yloc=" + yloc + ", draw=" + draw + "]";
	}
    
}
