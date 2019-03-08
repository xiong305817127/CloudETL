/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.trans;

import java.util.List;

import org.pentaho.di.core.util.Utils;

import com.ys.idatrix.cloudetl.dto.engine.ExecConfigurationDto;
import com.ys.idatrix.cloudetl.ext.CloudSession;

import io.swagger.annotations.ApiModel;

/**
 * 转换执行请求
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("批量执行转换配置信息")
public class TransBatchExecRequestDto {
	
	private String owner;
	private List<String> names;
    private ExecConfigurationDto configuration;
    
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
	/**
	 * @return the configuration
	 */
	public ExecConfigurationDto getConfiguration() {
		return configuration;
	}
	/**
	 * @param  设置 configuration
	 */
	public void setConfiguration(ExecConfigurationDto configuration) {
		this.configuration = configuration;
	}
	public List<String> getGroups() {
		return groups;
	}
	public void setGroups(List<String> groups) {
		this.groups = groups;
	}
    
}
