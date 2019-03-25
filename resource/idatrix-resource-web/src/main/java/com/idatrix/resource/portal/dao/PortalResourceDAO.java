package com.idatrix.resource.portal.dao;

import com.idatrix.resource.portal.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 门户目录资源处理接口
 */
public interface PortalResourceDAO {

    List<DeptResourceStatisticsVO> getResourceStatisticsInfo(@Param("rentId")Long rentId,
            @Param("type")String type, @Param("libType")String libType);

    /*根据租户ID获取count个最新数据*/
    List<ResourcePubInfo> getLastestResourceByCount(@Param("rentId")Long rentId,
                                                    @Param("count")Long count);

    /*获取资源统计情况，由于失效要求较高，所以直接从rc_resource表获取*/
    PubCount getPubTotalCount(@Param("rentId") Long rentId);

    /*获取部门资源目录提供情况*/
    List<DeptResourceStatisticsVO> getDeptSupplyResource(@Param("rentId")Long rentId,
                                                        @Param("num")Long num);

    /*获取部门资源数据提供情况*/
    List<DeptResourceStatisticsVO> getDeptSupplyData(@Param("rentId")Long rentId,
                                                         @Param("num")Long num);

    /*获取部门资源使用情况*/
    List<DeptResourceStatisticsVO> getDeptUseResource(@Param("rentId")Long rentId,
            @Param("type")String type, @Param("num")Long num);

    /*根据租户获取共享类型分类统计*/
    ShareTypeStatisticsVO getShareStatistics(@Param("rentId") Long rentId);


    /*根据租户获取浏览总数*/
    Long getTotalVisitByRentID(@Param("rentId") Long rentId);

    /*资源录入情况统计*/
    TypeInStatisticsVO getTypeInStatisticsByRentId(@Param("rentId")Long rentId);

    /*统计资源上报数据情况*/
    ResourceUseStatisticsVO getResourceUseStatisticsByRentId(@Param("rentId")Long rentId);
}
