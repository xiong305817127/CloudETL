/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.step;

import java.util.Map;

import org.pentaho.di.core.util.Utils;

import com.ys.idatrix.quality.ext.CloudSession;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 转换和步骤信息
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("转换步骤详细信息")
public class TransStepDto {
	
	
	@ApiModelProperty("拥有者")
	private String owner;
    private String transName;
    private String group;
    private String stepName;
    
    private String detailType;
    private Map<String,Object> detailParam;
    
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
	/**
	 * @return detailType
	 */
	public String getDetailType() {
		return detailType;
	}
	/**
	 * @param detailType 要设置的 detailType
	 */
	public void setDetailType(String detailType) {
		this.detailType = detailType;
	}
	/**
	 * @return detailParam
	 */
	public Map<String, Object> getDetailParam() {
		return detailParam;
	}
	/**
	 * @param detailParam 要设置的 detailParam
	 */
	public void setDetailParam(Map<String, Object> detailParam) {
		this.detailParam = detailParam;
	}
    
    

}
