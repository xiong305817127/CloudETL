/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.trans;

import io.swagger.annotations.ApiModel;

/**
 * 转换 执行id
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("执行id信息")
public class TransExecIdDto {
	
    private String executionId;
    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }
    public String getExecutionId() {
        return executionId;
    }
    
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TransExecIdDto [executionId=" + executionId + "]";
	}

}
