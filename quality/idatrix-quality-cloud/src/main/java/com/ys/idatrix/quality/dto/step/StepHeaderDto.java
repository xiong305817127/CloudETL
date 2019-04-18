/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step;

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
@ApiModel("转换步骤查询信息")
public class StepHeaderDto {
	
	@ApiModelProperty("拥有者")
	private String owner;
	private String transName;
	private String group;
    private String stepName;
    private String stepType;
    
    
	public StepHeaderDto() {
		super();
	}
	/**
	 * @param transName
	 * @param group
	 * @param stepName
	 * @param stepType
	 */
	public StepHeaderDto(String owner , String transName, String group, String stepName, String stepType) {
		super();
		this.owner = owner;
		this.transName = transName;
		this.group = group;
		this.stepName = stepName;
		this.stepType = stepType;
	}
	
	 public String getOwner() {
		 if( Utils.isEmpty( owner )) {
			 owner = CloudSession.getResourceUser() ;
		 }
		 return owner;
	 }
	 public void setOwner(String owner) {
		 this.owner = owner;
	 }	
	
	public void setTransName(String transName) {
        this.transName = transName;
    }
    public String getTransName() {
    	if(Utils.isEmpty(group) && !Utils.isEmpty(transName) && transName.contains("/")) {
			group = transName.split("/", 2 )[0];
			transName = transName.split("/", 2 )[1];
		}
        return transName;
    }

    /**
	 * @return the group
	 */
	public String getGroup() {
		if(Utils.isEmpty(group) && !Utils.isEmpty(transName) && transName.contains("/")) {
			group = transName.split("/", 2 )[0];
			transName = transName.split("/", 2 )[1];
		}
		return group;
	}
	/**
	 * @param  设置 group
	 */
	public void setGroup(String group) {
		this.group = group;
	}
	public void setStepName(String stepName) {
        this.stepName = stepName;
    }
    public String getStepName() {
        return stepName;
    }
    
    public void setStepType(String stepType) {
        this.stepType = stepType;
    }
    public String getStepType() {
        return stepType;
    }

}
