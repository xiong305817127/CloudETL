package com.idatrix.resource.catalog.vo;

import lombok.Data;

/**
 * Created by Robin Wing on 2018-6-9.
 */
@Data
public class ResourceApproveVO {

    /*主键*/
    private Long id;

    /*资源Id*/
    private Long resourceId;

    /*审批人帐号*/
    private String approver;

    /*审批人姓名*/
    private String approverName;

    /*当前状态*/
    private String currentStatus;

    /*下一状态状态*/
    private String nextStatus;

    /*审批动作:agree,reject*/
    private String approveAction;

    /*审批意见*/
    private String suggestion;

    /*审批时间*/
    private String approveTime;

    /*是否当前审批：0否，1是*/
    private Boolean activeFlag;

}
