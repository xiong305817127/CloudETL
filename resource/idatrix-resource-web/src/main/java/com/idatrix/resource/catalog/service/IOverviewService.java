package com.idatrix.resource.catalog.service;

import com.idatrix.resource.catalog.vo.MonthStatisticsVO;
import com.idatrix.resource.catalog.vo.ResourceOverviewVO;
import com.idatrix.resource.catalog.vo.ResourceStatisticsVO;
import com.idatrix.resource.catalog.vo.request.ResourceCatalogSearchVO;
import com.idatrix.resource.common.utils.ResultPager;
import java.util.List;
import java.util.Map;

/**
 * Created by Robin Wing on 2018-5-29.
 */
public interface IOverviewService {

    /**
     * 资源总体情况查询
     */
    MonthStatisticsVO getOverall();

    /**
     * 查找最新的几个资源信息
     */
    List<ResourceStatisticsVO> getLatestResourceInfo(Long num);

    /**
     * 获取每月(基础库,部门库,主题库)三库合计的注册量,发布量,订阅量概览
     */
    List<MonthStatisticsVO> getMonthlyTotalAmount(int months);

    /**
     * 资源查询：可以按照资源名称、资源代码、提供方名称、提供方代码等方式查询已上架的资源,按照发布日期倒序进行排序 查询对象: 所有库、三大库各个库里面信息。
     *
     * @param catalogSearchVO 资源目录搜索条件
     * @return ResultPager<ResourceOverviewVO> 资源目录分页结果
     */
    ResultPager<ResourceOverviewVO> getPublishedResourcesByCondition(
            ResourceCatalogSearchVO catalogSearchVO);


    /**
     * 资源查询：可以按照资源名称、资源代码、提供方名称、提供方代码等方式查询已上架的资源,按照发布日期倒序进行排序 查询对象: 所有库、三大库各个库里面信息。
     */
    ResultPager<ResourceOverviewVO> getLibResourcesByCondition(String status,
            Map<String, String> conditionMap,
            Integer pageNum, Integer pageSize);
}
