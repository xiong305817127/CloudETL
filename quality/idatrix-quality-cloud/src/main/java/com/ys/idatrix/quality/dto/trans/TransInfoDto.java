/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.trans;

import java.util.Map;

import org.pentaho.di.core.util.Utils;

import com.google.common.collect.Maps;
import com.ys.idatrix.quality.ext.CloudSession;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 转换信息
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("转换信息")
public class TransInfoDto {
	
	@ApiModelProperty("拥有者")
	private String owner;
	private String name;
	private String group;
	
	private String newName;
	private String newGroup;
	
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
        return name;
    }
    
    public void setNewName(String newName) {
        this.newName = newName;
    }
    public String getNewName() {
        return newName;
    }

    /**
	 * @return the group
	 */
	public String getGroup() {
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
    
	/**
	 * @return the params
	 */
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
