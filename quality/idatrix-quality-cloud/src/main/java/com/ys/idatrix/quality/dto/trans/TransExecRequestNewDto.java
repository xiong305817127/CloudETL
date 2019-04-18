/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.dto.trans;

import java.util.List;

import org.pentaho.di.core.util.Utils;

import com.ys.idatrix.quality.dto.engine.ExecConfigurationDto;
import com.ys.idatrix.quality.ext.CloudSession;

import io.swagger.annotations.ApiModel;

/**
 * 转换执行请求
 * (new run configuration)
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("转换执行配置信息")
public class TransExecRequestNewDto {
	
	private String owner ;
	private String name;
	private String group;
    private ExecConfigurationDto configuration;
    
    //debug|preview
    private List<TransDebugExecDto> debugExecDtos; 
    
	//当重启服务自动重新运行时,恢复执行Id
	private String execId;
	
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
	public void setConfiguration(ExecConfigurationDto configuration) {
        this.configuration = configuration;
    }
    public ExecConfigurationDto getConfiguration() {
        return configuration;
    }
    
	/**
	 * @return the debugExecDtos
	 */
	public List<TransDebugExecDto> getDebugExecDtos() {
		return debugExecDtos;
	}
	/**
	 * @param  设置 debugExecDtos
	 */
	public void setDebugExecDtos(List<TransDebugExecDto> debugExecDtos) {
		this.debugExecDtos = debugExecDtos;
	}
	
	public String getExecId() {
		return execId;
	}
	public void setExecId(String execId) {
		this.execId = execId;
	}
	/* 
	 * Build text.
	 */
	@Override
	public String toString() {
		return "TransExecRequestNewDto [name=" + name + ", configuration=" + configuration + "]";
	}

}
