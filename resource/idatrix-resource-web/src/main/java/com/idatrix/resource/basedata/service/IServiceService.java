package com.idatrix.resource.basedata.service;

import com.idatrix.resource.basedata.vo.ServiceQueryVO;
import com.idatrix.resource.basedata.vo.ServiceVO;
import com.idatrix.resource.common.utils.ResultPager;

import java.util.List;
import java.util.Map;

public interface IServiceService {
	String saveOrUpdateService(Long rentId, String user, ServiceVO serviceVO);

	String deleteServiceByIds(List<Long> ids);

	ServiceVO getServiceById(Long id, String username);

	List<ServiceVO> getAllService(Long rentId);

	ResultPager<ServiceVO> getServicesByCondition(ServiceQueryVO queryVO);
}
