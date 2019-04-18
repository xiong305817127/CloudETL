/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.engine;

import org.pentaho.di.core.util.Utils;

import com.ys.idatrix.quality.ext.CloudSession;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * spark 引擎信息
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("spark 引擎信息")
public class SparkBriefDto {
	
	@ApiModelProperty("拥有者")
	private String owner;
	
	@ApiModelProperty("名称")
	private String name;
	
	@ApiModelProperty("类型")
    private String type;
	
	@ApiModelProperty("状态")
    private String status;
    
	
	public String getOwner() {
    	if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
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
		return "SparkBriefDto [name=" + name + ", type=" + type + ", status=" + status + "]";
	}

}
