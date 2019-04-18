/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.entry;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.util.Utils;

import com.ys.idatrix.cloudetl.ext.CloudSession;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * step header 信息
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("复制调度任务节点信息")
public class EntryCopyDto {
	
	@ApiModelProperty("拥有者")
	private String fromOwner;
	private String fromJobName;
	private String fromGroup;
    private String fromEntryName;
    
	@ApiModelProperty("拥有者")
	private String toOwner;
	private String toJobName;
	private String toGroup;
    private String toEntryName;
    
    @ApiModelProperty("拥有者,当from和to的拥有者相同时,可以只设置此值,优先 toOwner 和 fromOwner")
    private String owner;
    
	public String getOwner() {
		 if( Utils.isEmpty( owner )) {
			 owner = CloudSession.getResourceUser() ;
		 }
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getFromOwner() {
		 return Const.NVL(fromOwner, getOwner());
	}
	public void setFromOwner(String fromOwner) {
		this.fromOwner = fromOwner;
	}
	public String getToOwner() {
		return Const.NVL(toOwner, getOwner());
	}
	public void setToOwner(String toOwner) {
		this.toOwner = toOwner;
	}
	/**
	 * @return the fromJobName
	 */
	public String getFromJobName() {
		if(Utils.isEmpty(fromGroup) && !Utils.isEmpty(fromJobName) && fromJobName.contains("/")) {
			fromGroup = fromJobName.split("/", 2 )[0];
			fromJobName = fromJobName.split("/", 2 )[1];
		}
		return fromJobName;
	}
	/**
	 * @param  设置 fromJobName
	 */
	public void setFromJobName(String fromJobName) {
		this.fromJobName = fromJobName;
	}
	/**
	 * @return the fromEntryName
	 */
	public String getFromEntryName() {
		return fromEntryName;
	}
	/**
	 * @param  设置 fromEntryName
	 */
	public void setFromEntryName(String fromEntryName) {
		this.fromEntryName = fromEntryName;
	}
	/**
	 * @return the toJobName
	 */
	public String getToJobName() {
		if(Utils.isEmpty(toGroup) && !Utils.isEmpty(toJobName) && toJobName.contains("/")) {
			toGroup = toJobName.split("/", 2 )[0];
			toJobName = toJobName.split("/", 2 )[1];
		}
		return toJobName;
	}
	/**
	 * @param  设置 toJobName
	 */
	public void setToJobName(String toJobName) {
		this.toJobName = toJobName;
	}
	/**
	 * @return the toEntryName
	 */
	public String getToEntryName() {
		return toEntryName;
	}
	/**
	 * @param  设置 toEntryName
	 */
	public void setToEntryName(String toEntryName) {
		this.toEntryName = toEntryName;
	}
	/**
	 * @return the fromGroup
	 */
	public String getFromGroup() {
		if(Utils.isEmpty(fromGroup) && !Utils.isEmpty(fromJobName) && fromJobName.contains("/")) {
			fromGroup = fromJobName.split("/", 2 )[0];
			fromJobName = fromJobName.split("/", 2 )[1];
		}
		return fromGroup;
	}
	/**
	 * @param  设置 fromGroup
	 */
	public void setFromGroup(String fromGroup) {
		this.fromGroup = fromGroup;
	}
	/**
	 * @return the toGroup
	 */
	public String getToGroup() {
		if(Utils.isEmpty(toGroup) && !Utils.isEmpty(toJobName) && toJobName.contains("/")) {
			toGroup = toJobName.split("/", 2 )[0];
			toJobName = toJobName.split("/", 2 )[1];
		}
		return toGroup;
	}
	/**
	 * @param  设置 toGroup
	 */
	public void setToGroup(String toGroup) {
		this.toGroup = toGroup;
	}

   

}
