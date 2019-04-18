/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.db;

import org.pentaho.di.core.util.Utils;

import com.ys.idatrix.cloudetl.ext.CloudSession;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * DTO for common request name wrapper.
 * @author JW
 * @since 2017年7月4日
 *
 */
@ApiModel("数据库公共Dto")
public class DBIdDto {
	
	@ApiModelProperty("拥有者")
	private String owner;
	
	@ApiModelProperty("ID,通用属性,可能是databaseId,schemaId,tableId 等")
	private Long id;

	@ApiModelProperty("名称,扩展属性,本地数据库时有效")
	private String name;
	
	@ApiModelProperty("是否只有只读权限, 扩展属性, true:读权限数据库, false:写权限数据库,  为空:忽略权限(读写一起)")
	private Boolean isRead;
	
	 public String getOwner() {
		 if( Utils.isEmpty( owner )) {
			 owner = CloudSession.getResourceUser() ;
		 }
		 return owner;
	 }
	 public void setOwner(String owner) {
		 this.owner = owner;
	 }
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Boolean getIsRead() {
		return isRead;
	}
	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}
	
	@Override
	public String toString() {
		return "DBIdDto [owner=" + owner + ", id=" + id + ", name=" + name + ", isRead=" + isRead  + "]";
	}

}
