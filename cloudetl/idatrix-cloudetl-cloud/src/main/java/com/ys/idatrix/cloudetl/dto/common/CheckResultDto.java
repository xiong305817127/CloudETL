/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Cluster 服务检查结果Dto
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("服务检查结果Dto")
public class CheckResultDto {
	
	@ApiModelProperty("名称")
	private String name;
	
	@ApiModelProperty("结果")
    private boolean result;
    
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
    public boolean getResult() {
        return result;
    }
    
	/**
	 * 
	 */
	public CheckResultDto() {
		
	}
	
	/**
	 * @param name
	 * @param result
	 */
	public CheckResultDto(String name, boolean result) {
		this.name = name;
		this.result = result;
	}
	
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CheckResultDto [name=" + name + ", result=" + result + "]";
	}
    
}
