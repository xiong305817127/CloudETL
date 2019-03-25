package com.idatrix.resource.catalog.po;

import lombok.Data;

import java.util.Date;

/**
 * 政务信息资源变更历史表
 * @Author: Wangbin
 * @Date: 2018/5/23
 */
@Data
public class ResourceHistoryPO {

    /*主键*/
    private Long id;

    /*资源id*/
    private Long resourceId;

    /*变更动作:
    *add,update,delete,reg_submit申请注册,
    * reg_agree注册审批同意,
    * reg_reject,pub_submit申请发布,
    * pub_agree发布审批通过,
    * pub_reject发布审批拒绝,recall下架，forupdate退回更新 */
    private String action;

    /*变更动作展示名称*/
    private String actionName;

    /*审批意见*/
//    private String suggestion;

    private String creator;

    private Date createTime;

}
