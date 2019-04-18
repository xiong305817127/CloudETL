/**
 * 云化数据集成系统
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.service.cluster;

import java.util.List;
import java.util.Map;

import org.pentaho.di.cluster.ClusterSchema;
import com.ys.idatrix.quality.dto.cluster.ClusterDetailsDto;
import com.ys.idatrix.quality.dto.cluster.ClusterDto;
import com.ys.idatrix.quality.dto.common.CheckResultDto;
import com.ys.idatrix.quality.dto.common.PaginationDto;
import com.ys.idatrix.quality.dto.common.ReturnCodeDto;

/**
 * Cluster schema service interfaces.
 * @author JW
 * @since 2017年5月24日
 *
 */
public interface CloudClusterService {
	
	/**
	 * 服务方法 - 根据名字查找指定的服务器集群
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	ClusterSchema findClusterSchema(String owner ,String name) throws Exception;

	/**
	 * 服务方法 - 查询服务器集群列表
	 * @return
	 * @throws Exception 
	 */
	 Map<String, List<ClusterDto>> getCloudClusterList(String owner ) throws Exception;
	
	/**
	 * 服务方法 - 分页查询服务器集群列表
	 * @return
	 * @throws Exception 
	 */
	 Map<String,PaginationDto<ClusterDto>>  getCloudClusterList(String owner,boolean isMap ,int page,int pageSize,String search) throws Exception;
	
	/**
	 * 服务方法 - 检查服务器集群名是否存在
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	CheckResultDto checkClusterName(String owner ,String name) throws Exception;
	
	/**
	 * 服务方法 - 编辑服务器集群配置信息
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	ClusterDetailsDto editCluster(String owner ,String name) throws Exception;
	
	/**
	 * 服务方法 - 保存服务器集群配置信息
	 * @param details
	 * @return
	 * @throws Exception 
	 * @throws Exception 
	 */
	ReturnCodeDto saveCluster( ClusterDetailsDto details) throws  Exception;
	
	/**
	 * 服务方法 - 删除服务器集群配置
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	ReturnCodeDto deleteCluster(String owner ,String name) throws Exception;

}