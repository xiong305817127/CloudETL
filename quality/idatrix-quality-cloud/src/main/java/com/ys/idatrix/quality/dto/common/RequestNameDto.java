/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.common;

import org.pentaho.di.core.util.Utils;

import com.ys.idatrix.quality.ext.CloudSession;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * DTO for common request name wrapper.
 * @author JW
 * @since 2017年7月4日
 *
 */
@ApiModel("公共名称Dto")
public class RequestNameDto {
	
	@ApiModelProperty("拥有者")
	private String owner;
	
	@ApiModelProperty("名称")
	private String name;

	@ApiModelProperty("组名")
	private String group;
	
	@ApiModelProperty("文件路径")
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
	
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		if(Utils.isEmpty(group) && !Utils.isEmpty(name) && name.contains("/")) {
			group = name.split("/", 2 )[0];
			name = name.split("/", 2 )[1];
		}
		return this.name;
	}
	
	/**
	 * @return the group
	 */
	public String getGroup() {
		if(Utils.isEmpty(group) && !Utils.isEmpty(name) && name.contains("/")) {
			group = name.split("/", 2 )[0];
			name = name.split("/", 2 )[1];
		}
		return group;
	}
	/**
	 * @param  设置 group
	 */
	public void setGroup(String group) {
		this.group = group;
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
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RequestNameDto [name=" + name + ", path=" + path + "]";
	}

	
}
