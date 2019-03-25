package com.idatrix.resource.subscribe.service;

import com.idatrix.resource.common.utils.ResultPager;
import com.idatrix.resource.subscribe.vo.SubscribeOverviewVO;
import com.idatrix.resource.subscribe.vo.SubscribeVO;
import com.idatrix.resource.subscribe.vo.SubscribeWebServiceVO;
import com.idatrix.resource.subscribe.vo.request.SubscribeApproveRequestVO;

import java.util.List;
import java.util.Map;


public interface ISubscribeService {

    /*
     * 获取订阅初始化信息
     */
    SubscribeVO getInitConfig(Long resourcdId)throws Exception;

    /*
    * 获取订阅详情
    */
    SubscribeVO getSubscribeById(Long id)throws Exception;

    /*
     * 增加订阅
     */
    void addSubscribe(String user, SubscribeVO subscribeVO)throws Exception;

    /*
     * 获取某人订阅成功订阅信息概览
     */
    ResultPager<SubscribeOverviewVO> queryApproverSuccessOverview(Map<String, String> con, Integer pageNum,
                                                   Integer pageSize);



    /*
	 * 获取订阅信息概览
	 */
    ResultPager<SubscribeOverviewVO> queryOverview(Map<String, String> con, Integer pageNum,
                                                 Integer pageSize);

    /*
    * 获取订阅审批情况概览
    */
    ResultPager<SubscribeOverviewVO> queryWaitApproveOverview(Map<String, String> con, Integer pageNum,
                                                   Integer pageSize);

    /*
    * 获取已经评审的资源概览
    */
    ResultPager<SubscribeOverviewVO> queryProcessedApproveOverview(Map<String, String> con, Integer pageNum,
                                                              Integer pageSize);


    /*处理注册/发布审批*/
    void processApprove(String user, Long subscribeId, String action, String suggestion) throws Exception;


    void processApprove(String user, SubscribeApproveRequestVO subscribeVO)throws Exception;

    /*批量处理 注册/发布审批*/
    void batchProcessApprove(String user, List<Long> ids) throws Exception;

    /*停止订阅*/
    void stopSubscribe(Long subscirbeId) throws Exception;

    /*恢复订阅*/
    void resumeSubscibe(Long subscribeId) throws Exception;

    /*获取webservice服务描述*/
    SubscribeWebServiceVO getWebserviceDescription(Long subscribeId) throws Exception;

}
