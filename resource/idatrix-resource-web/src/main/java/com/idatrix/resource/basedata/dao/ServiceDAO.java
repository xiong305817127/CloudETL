package com.idatrix.resource.basedata.dao;


import com.idatrix.resource.basedata.po.ServicePO;

import java.util.List;
import java.util.Map;

public interface ServiceDAO {
	List<ServicePO> getAllService();

	ServicePO getServiceById(Long id);

	ServicePO getServiceByServiceCode(String serviceCode);

	ServicePO getServiceByServiceName(String serviceName);

	List<ServicePO> getOccupiedServicePOList(List<Long> idList);

	void deleteByIds(List<Long> idList);

	void insert(ServicePO servicePO);

	int updateService(ServicePO servicePO);

	List<ServicePO> getServicesByCondition(Map<String, String> conditionMap);
}
