/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.job;

import java.util.List;

import com.ys.idatrix.quality.dto.entry.EntryInfoDto;
import com.ys.idatrix.quality.dto.hop.HopInfoDto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 转换覆盖
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("调度任务详细信息")
public class JobOverviewDto {
	
	@ApiModelProperty("调度任务信息")
	private JobInfoDto info;
	
	@ApiModelProperty("调度任务节点列表")
    private List<EntryInfoDto> entryList;
	
	@ApiModelProperty("调度任务线列表")
    private List<HopInfoDto> hopList;
    
    public void setInfo(JobInfoDto info) {
        this.info = info;
    }
    public JobInfoDto getInfo() {
        return info;
    }

	/**
	 * @return entryList
	 */
	public List<EntryInfoDto> getEntryList() {
		return entryList;
	}
	/**
	 * @param  设置 entryList
	 */
	public void setEntryList(List<EntryInfoDto> entryList) {
		this.entryList = entryList;
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
		return "JsonTransOverview [info=" + info + ", stepList=" + entryList + ", hopList=" + hopList + "]";
	}
    
}
