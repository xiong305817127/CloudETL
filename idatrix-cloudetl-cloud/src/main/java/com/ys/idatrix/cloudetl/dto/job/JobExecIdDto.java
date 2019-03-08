/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.job;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 转换 执行id
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("调度执行id信息")
public class JobExecIdDto {
	
	@ApiModelProperty("调度执行id")
    private String executionId;
	
    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }
    public String getExecutionId() {
        return executionId;
    }

}
