/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.reference.metacube;

import java.util.ArrayList;
import java.util.List;
import org.pentaho.di.core.util.Utils;
import org.springframework.stereotype.Service;
import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.collect.Lists;
import com.ys.idatrix.cloudetl.logger.CloudLogger;
import com.ys.idatrix.cloudetl.reference.metacube.dto.MetaCubeHadoopDto;
import com.ys.idatrix.cloudetl.repository.xml.metastore.meta.HadoopClusterMeta;
import com.ys.idatrix.metacube.api.beans.AmbariClusterInfoDTO;
import com.ys.idatrix.metacube.api.beans.ResultBean;
import com.ys.idatrix.metacube.api.service.AmbariPlatformService;
import com.ys.idatrix.metacube.api.service.MetadataServiceProvide;

/**
 * Synchronize hadoop cluster data from MetaCube system
 * by calling its RPC APIs exported as dubbo services.
 * 
 * @author JW
 * @since 2017年5月24日
 *
 */
@Service
public class MetaCubeHadoop extends MetaCubeBase {

	// "HDFS" : "ysdbsitfc-s01:8020"
	protected final String META_SPLITTER = ":";
	
	protected final String META_HDFS_NAME = "idatrix-hdfs" ;
	protected final String META_KEY_HDFS = "HDFS";
	protected final String META_KEY_HDFS_URL_HEAD = "hdfs://";
	
	@Reference(check=false )
	private AmbariPlatformService ambariPlatformService;
	
	@Reference(check=false )
	private MetadataServiceProvide metadataServiceProvider;
	 
	/**
	 * Get hadoop cluster list from MetaCube.
	 * @return
	 */
	public MetaCubeHadoopDto getDefaultMetaCubeHadoop(String owner) {
		MetaCubeHadoopDto dto = new MetaCubeHadoopDto();
		dto.setName( META_HDFS_NAME );
		dto.setType(META_KEY_HDFS);
		dto.setStatus(0);
		return dto ;
	}

	/**
	 * Get ETL hadoop cluster meta list from MetaCube.
	 * @return
	 */
	public List<HadoopClusterMeta> getHadoopClusterList(String owner) {
		CloudLogger.getInstance(owner).info(this,"getHadoopClusterList("+owner+")...");

		List<HadoopClusterMeta> metaList = new ArrayList<>();;
		try {
			ResultBean<AmbariClusterInfoDTO> resultBean = ambariPlatformService.getClusterInfo() ;
			if( resultBean != null  && resultBean.isSuccess() && resultBean.getData() != null ) {
				AmbariClusterInfoDTO result = resultBean.getData() ;
				CloudLogger.getInstance(owner).info(this,"getHadoopClusterList result: ", result.getHdfs());
				
				//"HDFS" : "ysdbsitfc-s01:8020"
				if(!Utils.isEmpty(result.getHdfs())) {
					metaList  = Lists.newArrayList( parseHadoopDetails(owner, result.getHdfs()) ) ;
				}
			}
		} catch (Exception ex) {
			CloudLogger.getInstance(owner).error(this,"getHadoopClusterList 获取Hadoop连接列表失败",ex);
		}
		return metaList;
	}



	/**
	 * Get hadoop cluster configuration from MetaCube.
	 * @param name
	 * @return
	 */
	public HadoopClusterMeta getMetaCubeHadoop(String owner , String name) {
		CloudLogger.getInstance(owner).info(this,"getMetaCubeHadoopDetails("+name+")...");

		try {
			ResultBean<AmbariClusterInfoDTO> resultBean = ambariPlatformService.getClusterInfo() ;
			if(resultBean != null  && resultBean.isSuccess() && resultBean.getData() != null ) {
				AmbariClusterInfoDTO result = resultBean.getData() ;
				CloudLogger.getInstance(owner).info(this,"getHadoopClusterList result: ", result.getHdfs());
				
				//"HDFS" : "ysdbsitfc-s01:8020"
				if(!Utils.isEmpty(result.getHdfs())) {
					return parseHadoopDetails(owner, result.getHdfs()) ;
				}
			}
		} catch (Exception ex) {
			CloudLogger.getInstance(owner).error(this,"getHadoopClusterList("+name+")获取Hadoop详细信息失败",ex);
		}

		return null;
	}
	
	/**
	 * Parse meta cube hadoop cluster informations.
	 * @param info
	 * @return
	 */
	private HadoopClusterMeta parseHadoopDetails(String owner , String info) {
		
		//"HDFS" : "ysdbsitfc-s01:8020"
		String[] h = info.split( META_SPLITTER );
		String  host = h[0].trim();
		String  port = h[1].trim();
		if (host.toLowerCase().startsWith( META_KEY_HDFS_URL_HEAD )) {
			host = host.substring(META_KEY_HDFS_URL_HEAD.length());
		}
		
		HadoopClusterMeta hadoopMeta = new HadoopClusterMeta();
		hadoopMeta.setName( META_HDFS_NAME );
		hadoopMeta.setOwner(owner);
		
		hadoopMeta.setHostname(host);
		hadoopMeta.setPort(port);

		hadoopMeta.setUsername("");
		hadoopMeta.setPassword("");

		hadoopMeta.setStatus(0);
		hadoopMeta.setStorage(META_KEY_HDFS);

		return hadoopMeta;
	}

	//########################################## Hdfs Directory ###########################################################
	
	/**
	 * Get hadoop root path from MetaCube.
	 * @param name
	 * @return
	 */
	public List<String> getHadoopUserRoots(String owner ,Boolean isRead) {
		CloudLogger.getInstance(owner).info(this,"getHadoopRootPath("+owner+","+isRead+")...");
		List<String> result = null ;
		try {
			
			ResultBean<List<String>> resultBean = metadataServiceProvider.findHDFSFolderByUser( owner, readOrWriteAction(isRead) );
			if( resultBean != null  && resultBean.isSuccess() && resultBean.getData() != null ) {
				result = resultBean.getData() ;
			}
			CloudLogger.getInstance(owner).info(this,"getHadoopRootPath result: ", result);
		} catch (Exception ex) {
			CloudLogger.getInstance(owner).error(this,"getHadoopRootPath("+owner+")获取Hadoop详细信息失败",ex);
		}

		return result;
	}
	
}
