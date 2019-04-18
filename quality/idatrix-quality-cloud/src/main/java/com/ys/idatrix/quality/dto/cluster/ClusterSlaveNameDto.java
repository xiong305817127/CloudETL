/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.cluster;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * slave server name
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("集群服务器信息")
public class ClusterSlaveNameDto {
	
	@ApiModelProperty("服务器名称")
	private String name;
	
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }
    
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ClusterSlaveNameDto [name=" + name + "]";
	}

}
