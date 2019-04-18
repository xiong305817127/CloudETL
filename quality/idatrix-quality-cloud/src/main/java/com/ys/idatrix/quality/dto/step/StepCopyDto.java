/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step;

import org.pentaho.di.core.Const;
import org.pentaho.di.core.util.Utils;

import com.ys.idatrix.quality.ext.CloudSession;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * step header 信息
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("复制转换信息")
public class StepCopyDto {
	
	@ApiModelProperty("拥有者")
	private String fromOwner;
	private String fromTransName;
	private String fromGroup;
    private String fromStepName;
    
    private String toOwner;
	private String toTransName;
	private String toGroup;
    private String toStepName;
    
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
	 * @return the fromTransName
	 */
	public String getFromTransName() {
		if(Utils.isEmpty(fromGroup) && !Utils.isEmpty(fromTransName) && fromTransName.contains("/")) {
			fromGroup = fromTransName.split("/", 2 )[0];
			fromTransName = fromTransName.split("/", 2 )[1];
		}
		return fromTransName;
	}
	/**
	 * @param  设置 fromTransName
	 */
	public void setFromTransName(String fromTransName) {
		this.fromTransName = fromTransName;
	}
	/**
	 * @return the fromStepName
	 */
	public String getFromStepName() {
		return fromStepName;
	}
	/**
	 * @param  设置 fromStepName
	 */
	public void setFromStepName(String fromStepName) {
		this.fromStepName = fromStepName;
	}
	/**
	 * @return the toTransName
	 */
	public String getToTransName() {
		if(Utils.isEmpty(toGroup) && !Utils.isEmpty(toTransName) && toTransName.contains("/")) {
			toGroup = toTransName.split("/", 2 )[0];
			toTransName = toTransName.split("/", 2 )[1];
		}
		return toTransName;
	}
	/**
	 * @param  设置 toTransName
	 */
	public void setToTransName(String toTransName) {
		this.toTransName = toTransName;
	}
	/**
	 * @return the toStepName
	 */
	public String getToStepName() {
		return toStepName;
	}
	/**
	 * @param  设置 toStepName
	 */
	public void setToStepName(String toStepName) {
		this.toStepName = toStepName;
	}
	/**
	 * @return the fromGroup
	 */
	public String getFromGroup() {
		if(Utils.isEmpty(fromGroup) && !Utils.isEmpty(fromTransName) && fromTransName.contains("/")) {
			fromGroup = fromTransName.split("/", 2 )[0];
			fromTransName = fromTransName.split("/", 2 )[1];
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
		if(Utils.isEmpty(toGroup) && !Utils.isEmpty(toTransName) && toTransName.contains("/")) {
			toGroup = toTransName.split("/", 2 )[0];
			toTransName = toTransName.split("/", 2 )[1];
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
