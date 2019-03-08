package com.idatrix.resource.servicelog.dao;

import com.idatrix.resource.servicelog.po.ServiceLogDetailPO;
import com.idatrix.resource.servicelog.po.ServiceLogPO;

import java.util.List;
import java.util.Map;

public interface ServiceLogDetailDAO {
    ServiceLogDetailPO getServiceLogDetailByParentId(Long parentId);
}
