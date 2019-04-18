/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step;

import io.swagger.annotations.ApiModel;

/**
 * step gui 信息
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("转换线位置信息")
public class StepGuiDto {
	
	private int xloc;
    private int yloc;
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
    
	/* 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "StepGui [xloc=" + xloc + ", yloc=" + yloc + ", draw=" + draw + "]";
	}
    
}
