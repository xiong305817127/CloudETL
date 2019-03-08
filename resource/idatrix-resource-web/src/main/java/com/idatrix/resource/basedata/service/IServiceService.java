package com.idatrix.resource.basedata.service;

import com.idatrix.resource.basedata.vo.ServiceVO;
import com.idatrix.resource.common.utils.ResultPager;

import java.util.List;
import java.util.Map;

public interface IServiceService {
	String saveOrUpdateService(String user, ServiceVO serviceVO);

	String deleteServiceByIds(List<Long> ids);

	ServiceVO getServiceById(Long id);

	List<ServiceVO> getAllService();

	ResultPager<ServiceVO> getServicesByCondition(Map<String, String> conditionMap,
														 Integer pageNum, Integer pageSize);
}
