/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.cluster;

import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.cluster.ClusterSchema;
import org.pentaho.di.cluster.SlaveServer;
import org.pentaho.di.core.util.Utils;

import com.ys.idatrix.cloudetl.ext.CloudSession;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 集群信息
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("集群信息")
public class ClusterDto {
	
	@ApiModelProperty("拥有者")
	private String owner;
	
	@ApiModelProperty("名称")
	private String name;
	
	@ApiModelProperty("是否动态集群")
    private boolean dynamic;
	
	@ApiModelProperty("集群服务列表")
    private List<ClusterSlaveNameDto> slaveServers;
	
	
	public ClusterDto() {
		super();
	}
	
	public ClusterDto(ClusterSchema cs ) {
		super();
		setName(cs.getName());
		setDynamic(cs.isDynamic());

		List<SlaveServer> slaves = cs.getSlaveServers();
		List<ClusterSlaveNameDto> jslaves = new ArrayList<>();
		if (slaves != null) {
			for (SlaveServer slave : slaves) {
				ClusterSlaveNameDto jslave = new ClusterSlaveNameDto();
				jslave.setName(slave.getName());
				jslaves.add(jslave);
			}
		}
		setSlaveServers(jslaves);
		
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
	
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }
    public boolean getDynamic() {
        return dynamic;
    }

    public void setSlaveServers(List<ClusterSlaveNameDto> slaveServers) {
        this.slaveServers = slaveServers;
    }
    public List<ClusterSlaveNameDto> getSlaveServers() {
        return slaveServers;
    }
    
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ClusterDto [name=" + name + ", dynamic=" + dynamic + ", slaveServers=" + slaveServers + "]";
	}

}
