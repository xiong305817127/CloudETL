/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.trans;

import org.pentaho.di.core.util.Utils;

import com.ys.idatrix.quality.ext.CloudSession;

import io.swagger.annotations.ApiModel;

/**
 * 转换名
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("转换名")
public class TransNameDto {
	
	private String owner;
	private String name;
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
    
	
    public void setName(String name) {
	    this.name = name;
	}
	public String getName() {
	    return name;
	}
	
	/**
	 * @return the path
	 */
	public String getPath() {
		return path;
	}
	/**
	 * @param  设置 path
	 */
	public void setPath(String path) {
		this.path = path;
	}
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TransNameDto [name=" + name + "]";
	}

}
