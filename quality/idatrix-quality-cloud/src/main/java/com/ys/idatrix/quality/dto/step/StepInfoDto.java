/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step;

import io.swagger.annotations.ApiModel;

/**
 * step info
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("转换步骤信息")
public class StepInfoDto {
	
	private String name;
    private String type;
    private String description;
    private String clusterSchema;
    private boolean distributes =  false ;
    private boolean supportsErrorHandling =  false ;
    private StepGuiDto gui;
    
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

    public void setClusterSchema(String clusterSchema) {
        this.clusterSchema = clusterSchema;
    }
    public String getClusterSchema() {
        return clusterSchema;
    }

    public void setGui(StepGuiDto gui) {
        this.gui = gui;
    }
    public StepGuiDto getGui() {
        return gui;
    }
    
	/**
	 * @return the distributes
	 */
	public boolean isDistributes() {
		return distributes;
	}
	/**
	 * @param  设置 distributes
	 */
	public void setDistributes(boolean distributes) {
		this.distributes = distributes;
	}
	/**
	 * @return the supportsErrorHandling
	 */
	public boolean isSupportsErrorHandling() {
		return supportsErrorHandling;
	}
	/**
	 * @param  设置 supportsErrorHandling
	 */
	public void setSupportsErrorHandling(boolean supportsErrorHandling) {
		this.supportsErrorHandling = supportsErrorHandling;
	}
	/* 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "StepInfo [name=" + name + ", type=" + type + ", description=" + description + ", clusterSchema="
				+ clusterSchema + ", gui=" + gui + "]";
	}
    
}
