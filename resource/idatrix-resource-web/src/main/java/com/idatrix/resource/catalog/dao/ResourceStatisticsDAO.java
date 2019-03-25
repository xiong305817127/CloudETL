package com.idatrix.resource.catalog.dao;

import com.idatrix.resource.catalog.po.ResourceStatisticsPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Robin Wing on 2018-5-29.
 */
public interface ResourceStatisticsDAO {

    void insert(ResourceStatisticsPO rsPO);

    void deleteById(Long id);

    /*根据三大类基本类型查找所有月度统计数据*/
//    List<ResourceStatisticsPO> getByCatalogCode(String catalogCode);

    /*根据三大类和月度时间段获取数据*/
//    void getByTypeAndTime(Long type, Date startTime, Date endTime);

    /*获取所有类的注册量、发布量、订阅量各项之和*/
//    ResourceStatisticsPO getAllCount();

    int updateById(ResourceStatisticsPO rsPO);

    ResourceStatisticsPO getLatestByResourceId(Long resourceId);

    List<ResourceStatisticsPO> getLatestListByResourceId(Long resourceId);

    List<ResourceStatisticsPO> getLatestByRentId(@Param("rentId") Long rentId,
                                                 @Param("count") Long count);

}
