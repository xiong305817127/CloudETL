/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.entry;

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
@ApiModel("调度任务节点查询头")
public class EntryHeaderDto {
	
	@ApiModelProperty("拥有者")
	private String owner;
	
	@ApiModelProperty("调度任务名")
	private String jobName;
	
	@ApiModelProperty("调度组名")
	private String group;
	
	@ApiModelProperty("调度任务节点名")
    private String entryName;
	
	@ApiModelProperty("调度任务节点类型")
    private String entryType;

	
	/**
	 * 
	 */
	public EntryHeaderDto() {
		super();
	}
	/**
	 * @param jobName
	 * @param entryName
	 * @param entryType
	 */
	public EntryHeaderDto(String owner , String jobName, String entryName, String entryType) {
		super();
		this.owner = owner;
		this.jobName = jobName;
		this.entryName = entryName;
		this.entryType = entryType;
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
	 * @return entryType
	 */
	public String getEntryType() {
		return entryType;
	}
	/**
	 * @param  设置 entryType
	 */
	public void setEntryType(String entryType) {
		this.entryType = entryType;
	}
    

}
