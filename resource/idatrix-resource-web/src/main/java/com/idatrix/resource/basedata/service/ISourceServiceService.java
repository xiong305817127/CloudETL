package com.idatrix.resource.basedata.service;

import com.idatrix.resource.basedata.vo.SourceServiceVO;
import com.idatrix.resource.common.utils.ResultPager;

import java.util.List;
import java.util.Map;

/**
 * 源服务相关增删改查功能
 */
public interface ISourceServiceService {
	String saveOrUpdateSourceService(String user, SourceServiceVO sourceServiceVO);

	SourceServiceVO getSourceServiceById(Long id);

	void deleteSourceServiceByIds(List<Long> ids);

	List<SourceServiceVO> getAllSourceService();

	ResultPager<SourceServiceVO> getSourceServicesByCondition(Map<String, String> conditionMap,
																	 Integer pageNum, Integer pageSize);
}
