package com.idatrix.resource.catalog.dao;

import com.idatrix.resource.catalog.po.MonthStatisticsPO;
import com.idatrix.resource.catalog.po.StatisticsPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by Robin Wing on 2018-5-31.
 */
public interface MonthStatisticsDAO {

    void insert(MonthStatisticsPO rsPO);

    void deleteById(Long id);

    int updateById(MonthStatisticsPO rsPO);

    /*获取所有类的注册量、发布量、订阅量各项之和*/
    MonthStatisticsPO getAllCount();

    /*获取每月(基础库,部门库,主题库)三库合计的注册量,发布量,订阅量概览*/
    List<MonthStatisticsPO> getMonthlyTotalAmount(int months);


    /*根据月份信息获取已经存储的信息*/
    MonthStatisticsPO getByMonth(String month);

    /**************************多租户隔离增加的接口**************************/

      /*获取所有类的注册量、发布量、订阅量各项之和*/
    MonthStatisticsPO getAllCountByRendId(Long rentId);

    /*获取每月(基础库,部门库,主题库)三库合计的注册量,发布量,订阅量概览*/
    List<MonthStatisticsPO> getMonthlyTotalAmountByRentId(@Param("rentId") Long rentId,
                                                          @Param("months") int months);

    /*获取最近月份数统计数据*/
    List<StatisticsPO> getRecentMonthByRentId(@Param("rentId") Long rentId,
                                              @Param("months") int months);

    /*根据月份信息获取已经存储的信息*/
    MonthStatisticsPO getByMonthByRentId(@Param("rentId") Long rentId,
                                         @Param("month") String month);





}
