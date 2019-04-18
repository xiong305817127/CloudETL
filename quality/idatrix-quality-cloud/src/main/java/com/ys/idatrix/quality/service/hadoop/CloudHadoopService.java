/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.service.hadoop;

import java.util.List;
import java.util.Map;

import com.ys.idatrix.quality.dto.common.CheckResultDto;
import com.ys.idatrix.quality.dto.common.PaginationDto;
import com.ys.idatrix.quality.dto.common.ReturnCodeDto;
import com.ys.idatrix.quality.dto.hadoop.HadoopBriefDto;
import com.ys.idatrix.quality.dto.hadoop.HadoopDetailsDto;

/**
 * Service interface for cloud hadoop cluster.
 * @author JW
 * @since 2017年5月24日
 *
 */
public interface CloudHadoopService {

	/**
	 * 服务方法 - 查询Hadoop集群列表
	 * @return
	 * @throws Exception 
	 */
	Map<String,List<HadoopBriefDto>> getCloudHadoopList(String owner ) throws Exception;
	/**
	 * 服务方法 - 分页查询Hadoop集群列表
	 * @return
	 * @throws Exception 
	 */
	Map<String,PaginationDto<HadoopBriefDto>> getCloudHadoopList(String owner ,boolean isMap , int page,int pageSize,String search) throws Exception;
	
	/**
	 * 服务方法 - 检查Hadoop集群名是否存在
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	CheckResultDto checkHadoopName(String owner ,String name) throws Exception;
	
	/**
	 * 服务方法 - 编辑Hadoop集群配置信息
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	HadoopDetailsDto editHadoop(String owner ,String name) throws Exception;
	
	/**
	 * 服务方法 - 保存Hadoop集群配置信息
	 * @param details
	 * @return
	 * @throws Exception 
	 */
	ReturnCodeDto saveHadoop(HadoopDetailsDto details) throws Exception;
	
	/**
	 * 服务方法 - 删除Hadoop集群配置
	 * @param name
	 * @return
	 * @throws Exception 
	 */
	ReturnCodeDto deleteHadoop(String owner ,String name) throws Exception;



}