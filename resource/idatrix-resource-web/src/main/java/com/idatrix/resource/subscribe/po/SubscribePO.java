package com.idatrix.resource.subscribe.po;

import lombok.Data;

import java.util.Date;

/**
 * Created by Administrator on 2018/7/16.
 */
@Data
public class SubscribePO {

    /*主键*/
    private Long id;

    /*编号*/
    private Long seq;

    /*订阅编号为： SUB+8位数字*/
    private String subNo;

    /*资源id*/
    private Long resourceId;

    /*订阅方部门ID*/
    private Long deptId;

    /*订阅方名称*/
    private String deptName;

    /*申请人姓名*/
    private String subscribeUserName;

    /*订阅事由*/
    private String subscribeReason;

    /*订阅截至日期 yyyy-mm-dd*/
    private Date endDate;

    /*共享方式：1数据库，2文件下载，3webservice服务*/
    private int shareMethod;

    /*当前状态：wait_approve待审核，success 订阅成功，failed 已拒绝   */
    private String status;

    /*审批人帐号*/
    private String approver;

    /*审批人姓名*/
    private String approverName;

    /*审批意见*/
    private String suggestion;

    /*审批时间*/
    private Date approveTime;

    /*调用的服务url*/
    private String serviceUrl;

    /*UUID，服务参数之一*/
    private String subKey;

    /*租户ID，用于租户隔离*/
    private Long rentId;

    /*创建人*/
    private String creator;

    /*创建时间*/
    private Date createTime;

    /*修改人*/
    private String modifier;

    /*修改时间*/
    private Date modifyTime;

}
