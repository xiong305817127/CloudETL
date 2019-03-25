package com.idatrix.resource.servicelog.service;

import com.idatrix.resource.common.utils.ResultPager;
import com.idatrix.resource.servicelog.po.ServiceLogPO;
import com.idatrix.resource.servicelog.vo.ServiceLogDetailVO;
import com.idatrix.resource.servicelog.vo.ServiceLogVO;

import java.util.List;
import java.util.Map;

public interface IServiceLogService {
    ResultPager<ServiceLogVO> getServiceLogInfoByCondition(Map<String, Object> condition,
                                                           Integer pageNum, Integer pageSize);

    ServiceLogDetailVO getServiceLogDetailById(Long id);

    /**
     * 获取最新几个被调用的服务信息
     * @param num
     * @return
     */
    List<ServiceLogPO> getLastestCalledService(Long num);
}
