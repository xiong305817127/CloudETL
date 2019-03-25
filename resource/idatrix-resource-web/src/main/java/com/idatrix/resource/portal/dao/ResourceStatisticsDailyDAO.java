package com.idatrix.resource.portal.dao;

import com.idatrix.resource.portal.po.ResourceStatisticsDailyPO;
import org.apache.ibatis.annotations.Param;

public interface ResourceStatisticsDailyDAO {
    int deleteByPrimaryKey(Integer id);

    int insert(ResourceStatisticsDailyPO record);

    int insertSelective(ResourceStatisticsDailyPO record);

    ResourceStatisticsDailyPO selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ResourceStatisticsDailyPO record);

    int updateByPrimaryKey(ResourceStatisticsDailyPO record);

    /*手动增加DAO处理*/

    /**
     * 根据租户、资源ID、日时间查询表中是否有相同结构
     * @param rentId
     * @param dayTime
     * @param resourceId
     * @return
     */
    ResourceStatisticsDailyPO getStatisticsDailyByDayTime(@Param("rentId")Long rentId,
               @Param("dayTime")String dayTime, @Param("resourceId")Long resourceId);
}