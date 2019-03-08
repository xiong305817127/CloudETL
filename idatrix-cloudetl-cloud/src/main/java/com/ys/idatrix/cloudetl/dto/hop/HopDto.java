/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.hop;

import org.pentaho.di.core.util.Utils;

import com.ys.idatrix.cloudetl.ext.CloudSession;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 转换连接线Dto
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("连接线信息")
public class HopDto {
	
	@ApiModelProperty("拥有者")
	private String owner;
	
	@ApiModelProperty("名称")
	private String name;
	
	@ApiModelProperty("组别")
	private String group;
	
	@ApiModelProperty("上一步骤/节点 名称")
    private String from;
	
	@ApiModelProperty("下一步骤/节点名称")
    private String to;
	
	@ApiModelProperty("是否可用")
    private boolean enabled=true;
    
	@ApiModelProperty("是否是调度节点")
    boolean isJob= false;
	
	@ApiModelProperty("当 unconditional 为false(有条件)时,是否成功才执行")
    private boolean evaluation = true;
	
	@ApiModelProperty("是否无条件执行")
    private boolean unconditional = true;
    
	
	
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
    	if(Utils.isEmpty(group) && !Utils.isEmpty(name) && name.contains("/")) {
			group = name.split("/", 2 )[0];
			name = name.split("/", 2 )[1];
		}
        return name;
    }

    /**
	 * @return the group
	 */
	public String getGroup() {
		if(Utils.isEmpty(group) && !Utils.isEmpty(name) && name.contains("/")) {
			group = name.split("/", 2 )[0];
			name = name.split("/", 2 )[1];
		}
		return group;
	}
	/**
	 * @param  设置 group
	 */
	public void setGroup(String group) {
		this.group = group;
	}
	public void setFrom(String from) {
        this.from = from;
    }
    public String getFrom() {
        return from;
    }

    public void setTo(String to) {
        this.to = to;
    }
    public String getTo() {
        return to;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public boolean getEnabled() {
        return enabled;
    }
    
	/**
	 * @return isJob
	 */
	public boolean isJob() {
		return isJob;
	}
	/**
	 * @param  设置 isJob
	 */
	public void setJob(boolean isJob) {
		this.isJob = isJob;
	}
	/**
	 * @param  设置 isJob
	 */
	public void setIsJob(boolean isJob) {
		this.isJob = isJob;
	}

	/**
	 * @return evaluation
	 */
	public boolean isEvaluation() {
		return evaluation;
	}
	/**
	 * @param  设置 evaluation
	 */
	public void setEvaluation(boolean evaluation) {
		this.evaluation = evaluation;
	}
	/**
	 * @return unconditional
	 */
	public boolean isUnconditional() {
		return unconditional;
	}
	/**
	 * @param  设置 unconditional
	 */
	public void setUnconditional(boolean unconditional) {
		this.unconditional = unconditional;
	}
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "HopDto [name=" + name + ", from=" + from + ", to=" + to + ", enabled=" + enabled + ", isJob=" + isJob
				+ ", evaluation=" + evaluation + ", unconditional=" + unconditional + "]";
	}

    
    
}
