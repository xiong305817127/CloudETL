package com.idatrix.resource.portal.dao;

import com.idatrix.resource.portal.po.ResourceStatisticsVisitPO;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface ResourceStatisticsVisitDAO {
    int deleteByPrimaryKey(Long id);

    int insert(ResourceStatisticsVisitPO record);

    int insertSelective(ResourceStatisticsVisitPO record);

    ResourceStatisticsVisitPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(ResourceStatisticsVisitPO record);

    int updateByPrimaryKey(ResourceStatisticsVisitPO record);


    /*获取资源浏览日统计，根据开始和结束时间，以及租户ID*/
    List<ResourceStatisticsVisitPO> queryRcVisitByTime(@Param("rentId") Long rentId,
                                                       @Param("startTime")Date startTime,
                                                       @Param("endTime")Date endTime);

    /*在rc_statistics_visit表里面按照day_time日时间去查询*/
    ResourceStatisticsVisitPO getVisitStatisticsByDayTime(@Param("rentId")Long rentId,
                                                          @Param("dayTime")String dayTime);

}