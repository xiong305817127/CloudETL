package com.idatrix.resource.servicelog.dao;

import com.idatrix.resource.servicelog.po.ServiceLogPO;

import java.util.List;
import java.util.Map;

public interface ServiceLogDAO {
    List<ServiceLogPO> getServiceLogInfoByCondition(Map<String, Object> condition);

    ServiceLogPO getServiceLogById(Long id);
}
