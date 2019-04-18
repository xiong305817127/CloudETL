/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.trans;

import io.swagger.annotations.ApiModel;

/**
 * 转换执行日志
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("转换执行日志信息")
public class TransExecLogDto {
	
	private String name;
    private String log;
    
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setLog(String log) {
        this.log = log;
    }
    public String getLog() {
        return log;
    }
    
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TransExecLogDto [name=" + name + ", log=" + log + "]";
	}

}
