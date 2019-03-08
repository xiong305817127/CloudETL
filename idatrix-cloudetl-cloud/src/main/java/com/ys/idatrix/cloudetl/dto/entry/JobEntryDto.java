/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.entry;

import java.util.Map;

import org.pentaho.di.core.util.Utils;

import com.ys.idatrix.cloudetl.ext.CloudSession;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 转换和步骤信息
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("调度任务节点详情查询信息")
public class JobEntryDto {
	
	@ApiModelProperty("拥有者")
	private String owner;
	
	@ApiModelProperty("调度任务名")
    private String jobName;
	
	@ApiModelProperty("调度组名")
	private String group;
	
	@ApiModelProperty("调度任务节点名")
    private String entryName;
    
	@ApiModelProperty("详情查询类型")
    private String detailType;
	
	@ApiModelProperty("详情查询参数")
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
	    
	/**
	 * @return jobName
	 */
	public String getJobName() {
		if(Utils.isEmpty(group) && !Utils.isEmpty(jobName) && jobName.contains("/")) {
			group = jobName.split("/", 2 )[0];
			jobName = jobName.split("/", 2 )[1];
		}
		return jobName;
	}
	/**
	 * @param  设置 jobName
	 */
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}
	
	/**
	 * @return the group
	 */
	public String getGroup() {
		if(Utils.isEmpty(group) && !Utils.isEmpty(jobName) && jobName.contains("/")) {
			group = jobName.split("/", 2 )[0];
			jobName = jobName.split("/", 2 )[1];
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
	 * @return entryName
	 */
	public String getEntryName() {
		return entryName;
	}
	/**
	 * @param  设置 entryName
	 */
	public void setEntryName(String entryName) {
		this.entryName = entryName;
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
