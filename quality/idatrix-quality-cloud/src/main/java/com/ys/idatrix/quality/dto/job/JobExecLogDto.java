/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.job;

import io.swagger.annotations.ApiModel;

/**
 * 转换执行日志
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("执行日志信息")
public class JobExecLogDto {
	
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

}
