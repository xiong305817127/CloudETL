/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.deploy;

import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 系统部署级别：<br/>
 * （1）元数据接口类型<br/>
 * （2）系统过程存储类型<br/>
 * （3）TransEngine类型<br/>
 * @author JW
 * @since 2017年6月28日
 *
 */
@Service
public class CloudDeployLevel {
	
	@Value("${metaStore.category}")
	String metaStoreCategory;
	
	@Value("${metaCube.category}")
	String metaCubeCategory;
	
	@Value("${idatrix.trans.engine.deployment}")
	String transEngine;
	
	@Bean
	public MetaStoreCategory getMetaStoreCategory() {
		if(StringUtils.isEmpty(metaStoreCategory)){
			metaStoreCategory="Local";
		}
		return MetaStoreCategory.getMetaStoreCategoryByCategory(metaStoreCategory);
	}
	
	@Bean
	public MetaCubeCategory getMetaCubeCategory() {
		if(StringUtils.isEmpty(metaCubeCategory)){
			metaCubeCategory="Pentaho";
		}
		//当为 iDatrix 时,必须开启dubbo
		if( "iDatrix".equalsIgnoreCase(metaCubeCategory) && !"true".equalsIgnoreCase( IdatrixPropertyUtil.getProperty("dubbo.deployment") )) {
			throw new RuntimeException("连接元数据系统时必须开始dubbo服务(即设置 dubbo.deployment = true )"); 
		}
		return MetaCubeCategory.getMetaCubeCategoryByCategory(metaCubeCategory);
	}
	
	@Bean
	public TransEngineCategory getTransEngine() {
		if(StringUtils.isEmpty(transEngine)){
			transEngine="false";
		}
		return TransEngineCategory.getTransEngineCategoryByCategory(transEngine);
	}

}
