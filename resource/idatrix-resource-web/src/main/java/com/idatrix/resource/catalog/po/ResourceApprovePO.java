package com.idatrix.resource.catalog.po;

import lombok.Data;

import java.util.Date;

/**
 * 资源审批表
 */
@Data
public class ResourceApprovePO {

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
    private Date approveTime;

    /*是否当前审批：0否，1是*/
    private Boolean activeFlag=true;

    /*租户ID,用于租户隔离*/
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
