/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.job;

import java.util.List;

import org.pentaho.di.core.util.Utils;

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
public class JobBatchStopDto {
	
	@ApiModelProperty("调度拥有者")
	private String owner;
	
	@ApiModelProperty("调度名列表")
	private List<String> jobNames;
	
	public String getOwner() {
		 if( Utils.isEmpty( owner )) {
			 owner = CloudSession.getResourceUser() ;
		 }
		 return owner;
	 }
	 public void setOwner(String owner) {
		 this.owner = owner;
	 }
	 
	public List<String> getJobNames() {
		return jobNames;
	}
	public void setJobNames(List<String> jobNames) {
		this.jobNames = jobNames;
	}

	 
}
