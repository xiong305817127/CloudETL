/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.dto.trans;

import java.util.List;

import org.pentaho.di.core.util.Utils;

import com.ys.idatrix.cloudetl.ext.CloudSession;

import io.swagger.annotations.ApiModel;

/**
 * 转换执行请求
 * @author JW
 * @since 05-12-2017
 *
 */
@ApiModel("批量执行转换配置信息")
public class TransBatchStopDto {
	
	private String owner;
	private List<String> transNames;
	
	 public String getOwner() {
		 if( Utils.isEmpty( owner )) {
			 owner = CloudSession.getResourceUser() ;
		 }
		 return owner;
	 }
	 public void setOwner(String owner) {
		 this.owner = owner;
	 }
	public List<String> getTransNames() {
		return transNames;
	}
	public void setTransNames(List<String> transNames) {
		this.transNames = transNames;
	}

	
}
