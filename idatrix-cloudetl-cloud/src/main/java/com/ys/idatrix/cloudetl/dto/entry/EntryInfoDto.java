/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.entry;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * step info
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("调度任务节点信息")
public class EntryInfoDto {
	
	@ApiModelProperty("调度任务节点名")
	private String name;
	
	@ApiModelProperty("调度任务节点类型")
    private String type;
	
	@ApiModelProperty("调度任务节点描述")
    private String description;
	
	@ApiModelProperty("调度任务位置")
    private EntryGuiDto gui;
    
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }

    public void setGui(EntryGuiDto gui) {
        this.gui = gui;
    }
    public EntryGuiDto getGui() {
        return gui;
    }
    
	/* 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "StepInfo [name=" + name + ", type=" + type + ", description=" + description + ", gui=" + gui + "]";
	}
    
}
