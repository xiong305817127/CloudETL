package com.idatrix.resource.catalog.dao;


import com.idatrix.resource.catalog.po.ResourceApprovePO;
import com.idatrix.resource.catalog.po.StatisticsPO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by Robin Wing on 2018-6-9.
 */
public interface ResourceApproveDAO {

    void insert(ResourceApprovePO resourceApprovePO);

    void deleteById(Long id);

    void deleteByResource(Long resourceId);

    int updateById(ResourceApprovePO resourceApprovePO);

    ResourceApprovePO getApproveById(Long id);

    /*根据资源号获取资源待审批记录*/
    ResourceApprovePO getWaitApproveByResourceId(Long resourceId);

    /*根据资源号所有审批记录*/
    List<ResourceApprovePO> getApproveHistoryByResourceId(Long resourceId);

    /*根据审批人获取资源审批列表*/
    List<ResourceApprovePO> getApproveByCondition(Map<String, String> condition);

    /*根据审批人获取资源审批列表*/
    List<ResourceApprovePO> getMaintainResourceByCondition(Map<String, String> condition);

    /*获取是固定某人发布资源才具有资源维护权限*/
    ResourceApprovePO getMaintainResource(Map<String, String> condition);

    List<StatisticsPO> getStatisticsByStatusAndNums(@Param("num")int num ,
                                                    @Param("status")String status);

    /**************************多租户隔离增加的接口**************************/


}
