package com.idatrix.resource.portal.service;

import com.idatrix.resource.portal.vo.*;

import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2018/12/18.
 */
public interface IStatisticsService {

    /**
     * 获取目录日访问统计
     * @param rentId
     * @param startTime
     * @param endTime
     * @return
     */
    List<VisitStatisticsVO> getVisitStatisticsByDay(Long rentId, Date startTime,
                                                    Date endTime);

    /**
     * 获取资源分享类型统计数据
     * @return
     */
    ShareTypeStatisticsVO getShareTypeStatistics(Long rentId);

    /**
     * 资源录入填报情况统计
     * @param rentId
     * @return
     */
    TypeInStatisticsVO getTypeInStatistics(Long rentId);

    /**
     * 资源录入填报情况统计
     * @param rentId
     * @return
     */
    ResourceUseStatisticsVO getResourceUseStatistics(Long rentId);


    /**
     * 获取部门提供资源统计
     * @param rentId
     * @return
     */
    List<DeptResourceStatisticsVO> getDeptSupplyStatistics(Long rentId, Long num);


    /**
     * 获取部门调用资源情况
     * @param rentId
     * @return
     */
    List<DeptResourceStatisticsVO> getDeptUseStatistics(Long rentId, Long num);

    /**
     * 根据租户获取平台运行情况
     * @param rentId
     * @return
     */
    PlatformRunningVO getPlatformRunningStatistics(Long rentId);


    /**
     * 获取所属部门登陆情况统计
     * @param rentId
     * @param num
     * @return
     */
    List<DeptResourceStatisticsVO> getDeptLoginStatistics(Long rentId, Long num);


    /**
     * 根据租户获取每天登陆次数和登陆单位统计
     * @param rentId
     * @return
     */
    List<LoginStatisticsVO> getDailyLoginStatistics(Long rentId,Date startTime, Date endTime);
}
