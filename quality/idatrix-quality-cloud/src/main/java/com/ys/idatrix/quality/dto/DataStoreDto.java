/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.quality.dto;
import org.pentaho.di.core.util.Utils;
import com.ys.idatrix.quality.ext.CloudSession;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * DataStoreDto <br/>
 * @author JW
 * @since 2017年10月24日
 * 
 */
@ApiModel(description="数据存储对象")
public class DataStoreDto {
	
	@ApiModelProperty("拥有者")
	private String owner;
	
	@ApiModelProperty(value="存储类型")
	private String type;
	@ApiModelProperty(value="存储路径")
	private String path;
	
	public String getOwner() {
    	if( Utils.isEmpty( owner )) {
    		owner = CloudSession.getResourceUser() ;
    	}
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}	
	/**
	 * @return type
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type 要设置的 type
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * @return path
	 */
	public String getPath() {
		return path;
	}
	/**
	 * @param path 要设置的 path
	 */
	public void setPath(String path) {
		this.path = path;
	}
	
	/*
	 * 覆盖方法：toString
	 */
	@Override
	public String toString() {
		return "DataStoreDto [type=" + type + ", path=" + path + "]";
	}

}
