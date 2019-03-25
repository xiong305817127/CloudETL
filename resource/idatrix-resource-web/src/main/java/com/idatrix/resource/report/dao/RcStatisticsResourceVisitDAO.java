package com.idatrix.resource.report.dao;

import com.idatrix.resource.report.po.RcStatisticsResourceVisitPO;
import org.apache.ibatis.annotations.Param;

/**
 * 每个不同资源日为单位访问统计数据
 */
public interface RcStatisticsResourceVisitDAO {
    int deleteByPrimaryKey(Long id);

    int insert(RcStatisticsResourceVisitPO record);

    int insertSelective(RcStatisticsResourceVisitPO record);

    RcStatisticsResourceVisitPO selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(RcStatisticsResourceVisitPO record);

    int updateByPrimaryKey(RcStatisticsResourceVisitPO record);

    RcStatisticsResourceVisitPO getStatisticsByDayTime(@Param("resourceId")Long resourceId,
                                                       @Param("dayTime")String dayTime);
}