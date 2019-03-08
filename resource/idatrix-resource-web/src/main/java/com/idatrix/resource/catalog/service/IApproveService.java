package com.idatrix.resource.catalog.service;

import com.idatrix.resource.catalog.vo.ResourceApproveVO;
import com.idatrix.resource.catalog.vo.ResourceConfigVO;
import com.idatrix.resource.catalog.vo.ResourceOverviewVO;
import com.idatrix.resource.common.utils.ResultPager;

import java.util.List;
import java.util.Map;

/**
 * Created by Robin Wing on 2018-6-9.
 */
public interface IApproveService {

    /*提交注册审批*/
    Long submitApprove(String user, Long id) throws Exception;

    /*中心管理员上架*/
    void pubResource(String user, Long[] ids) throws Exception;

    /*中心管理员下架*/
    void recallResource(String user, Long[] ids) throws Exception;

    /*中心管理员退回*/
    void backResource(String user, Long[] ids) throws Exception;

    /*获取审核历史*/
    List<ResourceApproveVO> getHistory(Long id);

    /*获取用户注册待审批 资源*/
    ResultPager<ResourceOverviewVO> queryWaitRegApprove(Map<String, String> con,
                                                                   Integer pageNum, Integer pageSize);

    /*获取用户发布待审批 资源*/
    ResultPager<ResourceOverviewVO> queryWaitPubApprove(Map<String, String> con,
                                                                     Integer pageNum, Integer pageSize);
    /*获取用户已经审批发布的*/
    ResultPager<ResourceOverviewVO> queryProcessedPubApprove(Map<String, String> con,
                                                               Integer pageNum, Integer pageSize);

    /*获取用户已经审批注册的*/
    ResultPager<ResourceOverviewVO> queryProcessedRegApprove(Map<String, String> con,
                                                                    Integer pageNum, Integer pageSize);


    ResultPager<ResourceOverviewVO> queryMaintainResource(Map<String, String> con,
                                                                Integer pageNum, Integer pageSize);

    /*处理注册/发布审批*/
    void processApprove(String user, Long resourceId, String action, String suggestion) throws Exception;

    /*批量处理 注册/发布审批*/
    void batchProcessApprove(String user, Long[] resourceIds) throws Exception;



}
