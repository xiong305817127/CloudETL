package com.idatrix.resource.catalog.service;


import com.idatrix.resource.catalog.vo.ResourceConfigVO;
import com.idatrix.resource.catalog.vo.ResourceHistoryVO;
import com.idatrix.resource.catalog.vo.ResourceOverviewVO;
import com.idatrix.resource.catalog.vo.ResourcePubVO;
import com.idatrix.resource.common.utils.ResultPager;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 资源配置
 * @author : wangbin
 * @version : v1.0
 *
 */
public interface IResourceConfigService {

	/**
	 * 增加资源信息
	 */
	Long addResourceInfo(Long rentId, String user, ResourceConfigVO resourceConfigVO) throws Exception;


    /**
     * 删除资源信息,用户只能删除自己创建的数据
     */
    int deleteResourceInfo(String user, Long id) throws Exception;

	/**
	 * 查找所有信息资源
	 */
//    List<ResourceConfigVO> getAllResourceInfo();

	/**
	 * 查找某个用户下信息资源
	 */
	List<ResourceOverviewVO> getResourceInfoByUser(String user);

	/**
	 * 查找某个节点详细信息
	 */
	ResourceConfigVO getResourceInfoById(Long id) throws Exception;

    /**
     * 查找某个节点详细信息
     */
    ResourceConfigVO getResourceInfoById(String user, Long id) throws Exception;


	/*
	 * 资源查询：可以按照资源名称、资源代码、提供方名称、提供方代码等方式进行查询
	 * 查询对象: 所有库、三大库各个库里面信息。
	 */
	ResultPager<ResourceOverviewVO> queryByCondition(Map<String, String> conditionMap, Integer pageNum,
															Integer pageSize);

	/*获取资源操作历史记录*/
	List<ResourceHistoryVO> getHistory(Long id);

	/*获取用户配置已经全部发布*/
	List<ResourcePubVO> getPubResourceByCondition(Map<String, String> con);

	/*处理批量资源信息目录导入文件:返回文件名称*/
	String saveBatchImport(String user, CommonsMultipartFile file) throws Exception;

    /*
     *  处理批量上传 节点信息
     */
    void processExcel(Long rentId, String user,String fileName) throws Exception;
}
