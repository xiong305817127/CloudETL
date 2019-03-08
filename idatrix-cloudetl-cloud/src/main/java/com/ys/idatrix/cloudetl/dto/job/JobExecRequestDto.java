/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.job;

import org.pentaho.di.core.util.Utils;

import com.ys.idatrix.cloudetl.dto.engine.ExecConfigurationDto;
import com.ys.idatrix.cloudetl.ext.CloudSession;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 转换执行请求
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("调度执行请求")
public class JobExecRequestDto {
	
	@ApiModelProperty("拥有者")
	private String owner;
	
	@ApiModelProperty("调度名称")
	private String name;

	@ApiModelProperty("调度组名")
	private String group;

	@ApiModelProperty("执行配置")
	private ExecConfigurationDto configuration ;
	
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
	public String getExecId() {
		return execId;
	}
	public void setExecId(String execId) {
		this.execId = execId;
	}
	

}
