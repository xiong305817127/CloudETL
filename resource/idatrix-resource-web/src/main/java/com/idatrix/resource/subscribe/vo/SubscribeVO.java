package com.idatrix.resource.subscribe.vo;

import com.idatrix.resource.catalog.vo.ResourceColumnVO;
import io.swagger.annotations.ApiModel;
import lombok.Data;

import java.util.List;

/**
 * 订阅详情和配置订阅信息时候使用
 */
@Data
@ApiModel
public class SubscribeVO {
    /*主键*/
    private Long id;

    /*订阅编号*/
    private String subNo;

    /*资源id*/
    private Long resourceId;

    /*订阅方名称*/
    private String deptName;

    /*申请人姓名*/
    private String subscribeUserName;

    /*申请时间*/
    private String subscribeTime;

    /*订阅事由*/
    private String subscribeReason;

    /*订阅截至日期 yyyy-mm-dd*/
    private String endDate;

    /*共享方式：1数据库，2文件下载，3webservice服务*/
    private int shareMethod;

    /*数据库分享方式：0 非数据库资源， 1数据库-数据库分享， 2数据库-服务分享*/
    private int dbShareMethod;

    /*当前状态：wait_approve待审核，success 订阅成功，failed 已拒绝   */
    private String status;

    /*审批人帐号*/
    private String approver;

    /*审批人姓名*/
    private String approverName;

    /*审批意见*/
    private String suggestion;

    /*审批时间*/
    private String approveTime;

    /*调用的服务url*/
    private String serviceUrl;

    /*UUID，服务参数之一*/
    private String subKey;

    /*订阅方前置机 名称*/
    private String terminalName;

    /*订阅方数据库*/
    private String ternimalDbName;

    /*订阅信息项*/
    private List<ResourceColumnVO> inputDbioList;

    /*查询条件*/
    private List<ResourceColumnVO> outputDbioList;

   }
