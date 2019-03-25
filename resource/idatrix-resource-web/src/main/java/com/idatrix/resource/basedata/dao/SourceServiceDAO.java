package com.idatrix.resource.basedata.dao;

import com.idatrix.resource.basedata.po.SourceServicePO;

import java.util.List;
import java.util.Map;

/**
 * Created by Robin Wing on 2018-5-23.
 */
public interface SourceServiceDAO {

    void insert(SourceServicePO sourceServicePO);

	void deleteByIds(List<Long> idList);

    void updateSourceService(SourceServicePO sourceServicePO);

    SourceServicePO getSourceServiceById(Long id);

    SourceServicePO getSourceServiceByServiceName(String serviceName);

    SourceServicePO getSourceServiceByServiceCode(String serviceCode);

    List<SourceServicePO> getAllSourceService(Long rentId);

    List<SourceServicePO> getSourceServicesByCondition(Map<String, String> conditionMap);
}
