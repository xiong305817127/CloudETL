/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.job;

import java.util.List;

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
@ApiModel("批量执行请求信息")
public class JobBatchExecRequestDto {
	
	@ApiModelProperty("调度拥有者")
	private String owner;
	
	@ApiModelProperty("调度名列表")
	private List<String> names;
	
	@ApiModelProperty("执行配置")
	private ExecConfigurationDto configuration ;
    
	private List<String> groups;
    
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
	 * @return the names
	 */
	public List<String> getNames() {
		return names;
	}
	/**
	 * @param  设置 names
	 */
	public void setNames(List<String> names) {
		this.names = names;
	}
	public void setConfiguration(ExecConfigurationDto configuration) {
        this.configuration = configuration;
    }
    public ExecConfigurationDto getConfiguration() {
        return configuration;
    }
	public List<String> getGroups() {
		return groups;
	}
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

}
