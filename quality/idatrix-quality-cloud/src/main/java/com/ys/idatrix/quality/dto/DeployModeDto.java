/**
 * 云化数据集成系统 
 * iDatrxi CloudETL
 */
package com.ys.idatrix.quality.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * DTO - 部署模式 <br/>
 * MetaStore: <br/>
 * 		Local|Cache|Database <br/>
 * MetaCube: <br/>
 * 		iDatrix|Pentaho|Tenant <br/>
 * @author JW
 * @since 2017年9月5日
 * 
 */
@ApiModel("部署模式")
public class DeployModeDto {
	
	@ApiModelProperty("metaStore模式")
	private String metaStore;
	
	@ApiModelProperty("metaCube模式")
	private String metaCube;
	
	@ApiModelProperty("trans引擎模式")
	private String transEngine;
	
	/**
	 * @return metaStore
	 */
	public String getMetaStore() {
		return metaStore;
	}
	/**
	 * @param metaStore 要设置的 metaStore
	 */
	public void setMetaStore(String metaStore) {
		this.metaStore = metaStore;
	}
	
	/**
	 * @return metaCube
	 */
	public String getMetaCube() {
		return metaCube;
	}
	/**
	 * @param metaCube 要设置的 metaCube
	 */
	public void setMetaCube(String metaCube) {
		this.metaCube = metaCube;
	}
	
	/**
	 * @return transEngine
	 */
	public String getTransEngine() {
		return transEngine;
	}
	/**
	 * @param transEngine 要设置的 transEngine
	 */
	public void setTransEngine(String transEngine) {
		this.transEngine = transEngine;
	}
	
	/*
	 * 覆盖方法：toString
	 */
	@Override
	public String toString() {
		return "DeployModeDto [metaStore=" + metaStore + ", metaCube=" + metaCube + ", transEngine=" + transEngine
				+ "]";
	}
	
}
