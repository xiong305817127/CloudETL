package com.idatrix.resource.subscribe.vo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * 订阅概览信息
 */

@Data
@ApiModel("订阅概览信息")
public class SubscribeOverviewVO {

    /*订阅ID */
    private Long id;

    /*资源ID*/
    private Long resourceId;

    /*资源类型: file,interface,db*/
    private String resourceType;

    /*订阅编号*/
    private String subNo;

    /*资源代码*/
    private String code;

    /*资源名称*/
    private String name;

    /*资源提供方名称*/
    private String deptName;

    /*资源订阅方*/
    private String subscribeDeptName;

    /*申请日期*/
    private String applyDate;

    /*申请人姓名*/
    private String subscribeUserName;

    /*订阅状态*/
    private String subscribeStatus;

    /*共享方式：1数据库，2文件下载，3webservice服务*/
    private int shareMethod;

    /*数据库分享方式：0 非数据库资源， 1数据库-数据库分享， 2数据库-服务分享*/
    private int dbShareMethod;

    /*交换状态*/
    private String exchangeStatus;

    /*截止日子*/
    private String endTime;

    /*审批动作*/
    private String status;

    /*审批意见*/
    private String suggestion;

    /*审批时间*/
    private String approveTime;

    /*审批人*/
    private String approver;

}
