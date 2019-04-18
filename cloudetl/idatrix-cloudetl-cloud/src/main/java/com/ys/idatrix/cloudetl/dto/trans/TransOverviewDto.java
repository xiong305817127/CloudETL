/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.trans;

import java.util.List;

import com.ys.idatrix.cloudetl.dto.hop.HopInfoDto;
import com.ys.idatrix.cloudetl.dto.step.StepInfoDto;

import io.swagger.annotations.ApiModel;

/**
 * 转换覆盖
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("转换详细信息")
public class TransOverviewDto {
	
	private TransInfoDto info;
    private List<StepInfoDto> stepList;
    private List<HopInfoDto> hopList;
    
    public void setInfo(TransInfoDto info) {
        this.info = info;
    }
    public TransInfoDto getInfo() {
        return info;
    }

    public void setStepList(List<StepInfoDto> stepList) {
        this.stepList = stepList;
    }
    public List<StepInfoDto> getStepList() {
        return stepList;
    }

    public void setHopList(List<HopInfoDto> hopList) {
        this.hopList = hopList;
    }
    public List<HopInfoDto> getHopList() {
        return hopList;
    }
    
	/* 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JsonTransOverview [info=" + info + ", stepList=" + stepList + ", hopList=" + hopList + "]";
	}
    
}
