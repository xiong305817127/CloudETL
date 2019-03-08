/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.entry;

import org.pentaho.di.core.util.Utils;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.job.entries.special.JobEntrySpecial;
import org.pentaho.di.job.entry.JobEntryCopy;

import com.ys.idatrix.cloudetl.dto.codec.EntryParameterCodec;
import com.ys.idatrix.cloudetl.ext.CloudSession;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * step 详细信息
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("调度任务详细信息")
public class EntryDetailsDto {
	
	@ApiModelProperty("拥有者")
	private String owner;
	
	@ApiModelProperty("调度任务名")
    private String jobName;
	
	@ApiModelProperty("调度组名")
	private String group;
	
	@ApiModelProperty("调度任务节点名")
    private String entryName;
	
	@ApiModelProperty("调度任务节点新名")
    private String newName;
	
	@ApiModelProperty("调度任务节点类型")
    private String type;
	
	@ApiModelProperty("调度任务节点描述")
    private String description;
	
	@ApiModelProperty("调度任务节点参数")
    public Object entryParams;
    
	@ApiModelProperty("当前节点的后面的节点列表")
    private String[] nextEntryNames;
	
	@ApiModelProperty("当前节点的起前面的节点列表")
    private String[] prevEntryNames;
	
	@ApiModelProperty("是否并行执行")
	private boolean parallel = false;
    
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
	 * @return newName
	 */
	public String getNewName() {
		return newName;
	}

	/**
	 * @param  设置 newName
	 */
	public void setNewName(String newName) {
		this.newName = newName;
	}

	/**
	 * @return type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param  设置 type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param  设置 description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	

	public boolean isParallel() {
		return parallel;
	}

	public void setParallel(boolean parallel) {
		this.parallel = parallel;
	}

	/**
	 * @return nextEntryNames
	 */
	public String[] getNextEntryNames() {
		return nextEntryNames;
	}

	/**
	 * @param  设置 nextEntryNames
	 */
	public void setNextEntryNames(String[] nextEntryNames) {
		this.nextEntryNames = nextEntryNames;
	}

	/**
	 * @return prevEntryNames
	 */
	public String[] getPrevEntryNames() {
		return prevEntryNames;
	}

	/**
	 * @param  设置 prevEntryNames
	 */
	public void setPrevEntryNames(String[] prevEntryNames) {
		this.prevEntryNames = prevEntryNames;
	}

	/**
     * Since step parameter will be in different type,
     *  we have to parse the request body per type.
     * @param stepParams
     */
    public void setEntryParams(Object stepParams) {
    	this.entryParams = EntryParameterCodec.parseParamObject(stepParams, this.type);
    }

	public Object getEntryParams() {
		return entryParams;
    }
    
    
    public void encodeEntryParams(JobEntryCopy entryMeta) throws Exception {
    	
    	String entryType = entryMeta.getEntry().getPluginId() ;
    	if( entryMeta.getEntry() instanceof JobEntrySpecial && ((JobEntrySpecial)entryMeta.getEntry()).isDummy()) {
    		entryType = "DUMMY";
		}
    	this.entryParams = EntryParameterCodec.encodeParamObject(entryMeta, entryType );
    	
    	if( entryMeta.getEntry() instanceof JobEntrySpecial && ((JobEntrySpecial)entryMeta.getEntry()).isDummy()) {
    		this.type = "DUMMY";
		}
    }
    
    public void decodeParameterObject(JobEntryCopy entryMeta, JobMeta jobMeta) throws Exception {
    	String entryType = entryMeta.getEntry().getPluginId() ;
    	if( entryMeta.getEntry() instanceof JobEntrySpecial && ((JobEntrySpecial)entryMeta.getEntry()).isDummy()) {
    		entryType = "DUMMY";
		}
    	EntryParameterCodec.decodeParameterObject(entryMeta, this.entryParams, jobMeta, entryType);
    }

}
