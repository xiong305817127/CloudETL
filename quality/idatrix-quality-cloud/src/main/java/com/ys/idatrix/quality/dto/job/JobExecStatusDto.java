/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.job;

import io.swagger.annotations.ApiModel;

/**
 * 转换执行状态
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("调度任务执行状态")
public class JobExecStatusDto {
	
	private String status;
    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }

}
