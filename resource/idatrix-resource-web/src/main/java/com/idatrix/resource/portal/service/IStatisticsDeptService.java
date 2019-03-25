package com.idatrix.resource.portal.service;

import com.idatrix.resource.portal.common.StatisticsDailyEnum;

/**
 * 主要记录部门订阅和交换的数据，当订阅审批成功时候会产生一条记录，交换或者下载时候会记录一条数据
 */
public interface IStatisticsDeptService {

    /**
     * 部门交互成功时候记录数据
     * @param deptId
     * @param deptName
     * @param resourceId
     * @param paramName
     * @param value
     * @throws Exception
     */
    void saveStatisticsDept(Long deptId, String deptName, Long resourceId, StatisticsDailyEnum paramName, String value) throws  Exception;


    /**
     * 记录部门分享数值
     * @param deptId
     * @param deptName
     * @param resourceId
     */
    void saveDeptShareInfo(Long deptId, String deptName, Long resourceId, Long value);

}
