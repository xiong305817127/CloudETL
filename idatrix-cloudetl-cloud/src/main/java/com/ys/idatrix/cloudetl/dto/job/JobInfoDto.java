/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.job;

import java.util.Map;

import org.pentaho.di.core.util.Utils;

import com.google.common.collect.Maps;
import com.ys.idatrix.cloudetl.ext.CloudSession;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 转换信息
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("调度任务信息")
public class JobInfoDto {
	
	@ApiModelProperty("拥有者")
	private String owner;

	@ApiModelProperty("调度任务名")
	private String name;
	
	@ApiModelProperty("调度组名")
	private String group;

	@ApiModelProperty("调度任务新名称")
	private String newName;
	
	@ApiModelProperty("调度新组名")
	private String newGroup;

	@ApiModelProperty("调度任务描述")
	private String description;
	
    private Map<String,String> params;

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
		if(Utils.isEmpty(group) && !Utils.isEmpty(name) && name.contains("/")) {
			group = name.split("/", 2 )[0];
			name = name.split("/", 2 )[1];
		}
		return name;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}
	public String getNewName() {
		if(Utils.isEmpty(newGroup) && !Utils.isEmpty(newName) && newName.contains("/")) {
			newGroup = newName.split("/", 2 )[0];
			newName = newName.split("/", 2 )[1];
		}
		return newName;
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
	 * @return the newGroup
	 */
	public String getNewGroup() {
		if(Utils.isEmpty(newGroup) && !Utils.isEmpty(newName) && newName.contains("/")) {
			newGroup = newName.split("/", 2 )[0];
			newName = newName.split("/", 2 )[1];
		}
		return newGroup;
	}
	/**
	 * @param  设置 newGroup
	 */
	public void setNewGroup(String newGroup) {
		this.newGroup = newGroup;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDescription() {
		return description;
	}
	public Map<String, String> addParams(String key ,String value) {
		if(params == null) {
			params =  Maps.newHashMap();
		}
		params.put(key, value) ;
		return params;
	}
    
	/**
	 * @return the params
	 */
	public Map<String, String> getParams() {
		return params;
	}
	/**
	 * @param  设置 params
	 */
	public void setParams(Map<String, String> params) {
		this.params = params;
	}
	/* 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "JsonTransInfo [name=" + name + ", description=" + description + "]";
	}

}
