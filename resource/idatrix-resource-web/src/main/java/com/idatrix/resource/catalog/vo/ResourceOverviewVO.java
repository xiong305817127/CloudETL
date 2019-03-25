package com.idatrix.resource.catalog.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

import java.util.List;

/**
 * Created by Robin Wing on 2018-6-7.
 */
@Data
public class ResourceOverviewVO {

    /*资源ID（自动增加）*/
    private Long id;

    /*基本库类型*/
    private String libType;

    /*资源分类名称*/
    private String catalogName;

    /*资源分类代码 格式为： XXXX/XX*/
    private String catalogCode;

    /*信息资源代码*/
    private String resourceCode;

    /*资源名称*/
    private String resourceName;

    /*资源提供方*/
    private String deptName;

    /*资源提供方代码*/
    private String deptCode;

    /*资源数据量*/
    private Long dataCount;

    /*数据更新时间*/
    private String updateTime;

    /*资源状态*/
    private String status;

    /*创建人*/
    private String creator;

    /*审批时间*/
    private String approveTime;

    /*审批意见*/
    private String approveSuggestion;

    /*审批动作*/
    private String approveAction;

    /*审批人*/
    private String approverName;

    /*老版本含义：能够订阅: true表示可订阅，false 已订阅
     * 后修改为： 0表示没有订阅权限，1表示可以订阅，2，表示已经订阅*/
    private int subscribeFlag;

    /**
     * ES查询高亮内容
     */
    @JsonInclude(Include.NON_NULL)
    private List<String> highlight;


}
