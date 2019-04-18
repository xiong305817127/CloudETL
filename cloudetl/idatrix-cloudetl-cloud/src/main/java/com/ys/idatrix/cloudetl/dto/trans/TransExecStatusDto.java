/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.trans;

import io.swagger.annotations.ApiModel;

/**
 * 转换执行状态
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("转换执行状态信息")
public class TransExecStatusDto {
	
	private String status;
    public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }
    
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TransExecStatusDto [status=" + status + "]";
	}

}
