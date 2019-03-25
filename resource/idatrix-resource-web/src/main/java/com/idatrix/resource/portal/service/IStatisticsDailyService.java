package com.idatrix.resource.portal.service;

import com.idatrix.resource.portal.common.StatisticsDailyEnum;

/**
 * 统计资源发布和上报信息，每天进行统计：如每天发布新的资源，或者每天上传新的数据
 */
public interface IStatisticsDailyService {

    /**
     * 更新存储 资源统计数据，每天会统计 部门的资源的发布和上传情况
     * @param resourceId
     * @param paramName  需要设置变量参数
     * @param value     配置数值
     */
    void saveStatisticsDaily(Long resourceId, StatisticsDailyEnum paramName, String value) throws  Exception;

    /**
     * 初次以及更新存储 资源统计数据
     * @param resourceId
     */
    void saveStatisticsDaily(Long resourceId) throws  Exception;


    /**资源统计数据新增存储
     * @param resourceId
     */
    void saveStatisticsDaily(Long resourceId , Long value);

    /**
     * 增加文件类型统计
     * @param resourceId
     */
    void saveFileStatisticsDaily(Long resourceId, Long value);


    /**
     * 增加数据库类型统计
     * @param resourceId
     */
    void saveDBStatisticsDaily(Long resourceId, Long value);


    /**
     * 增加接口类型统计
     * @param resourceId
     */
    void saveInterfaceStatisticsDaily(Long resourceId, Long value);
}
