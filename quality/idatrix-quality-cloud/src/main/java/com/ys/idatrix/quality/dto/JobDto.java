/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto;

import org.pentaho.di.core.util.Utils;

import com.ys.idatrix.quality.ext.CloudSession;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * DTO - 调度信息，用于主页调度列表信息展示<br/>
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("调度信息")
public class JobDto {
	
	@ApiModelProperty("拥有者")
	private String owner;
	
	@ApiModelProperty("调度名称")
	private String name;
	
	@ApiModelProperty("调度组名称")
	private String group;
	
	@ApiModelProperty("调度状态")
    private String status;
	
	@ApiModelProperty("调度描述")
    private String description;
	
	@ApiModelProperty("调度最近一次执行时间")
    private String lastExecTime;
	
	@ApiModelProperty("调度涉及的服务器数")
    private int servers;
	
	@ApiModelProperty("调度涉及的集群数")
    private int clusters;
	
	@ApiModelProperty("最近一次调度执行时长")
    private String execTime;
	
	@ApiModelProperty("最后修改时间")
	private String modifiedTime;
    
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
	public void setStatus(String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }

    public void setLastExecTime(String lastExecTime) {
        this.lastExecTime = lastExecTime;
    }
    public String getLastExecTime() {
        return lastExecTime;
    }
    
    /**
	 * @return servers
	 */
	public int getServers() {
		return servers;
	}
	/**
	 * @param servers 要设置的 servers
	 */
	public void setServers(int servers) {
		this.servers = servers;
	}
	
	/**
	 * @return clusters
	 */
	public int getClusters() {
		return clusters;
	}
	/**
	 * @param clusters 要设置的 clusters
	 */
	public void setClusters(int clusters) {
		this.clusters = clusters;
	}
	
	/**
	 * @return execTime
	 */
	public String getExecTime() {
		return execTime;
	}
	/**
	 * @param execTime 要设置的 execTime
	 */
	public void setExecTime(String execTime) {
		this.execTime = execTime;
	}
	
	/**
	 * @return description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description 要设置的 description
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getModifiedTime() {
		return modifiedTime;
	}
	public void setModifiedTime(String modifiedTime) {
		this.modifiedTime = modifiedTime;
	}
	@Override
	public String toString() {
		return "JobDto [name=" + name + ", group=" + group + ", status=" + status + ", description=" + description
				+ ", lastExecTime=" + lastExecTime + ", servers=" + servers + ", clusters=" + clusters + ", execTime="
				+ execTime + ", modifiedTime=" + modifiedTime + "]";
	}
	
}
