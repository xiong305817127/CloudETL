package com.idatrix.resource.subscribe.vo.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Created by Administrator on 2018/7/17.
 */
@Data
@ApiModel("订阅概览请求")
public class SubscribeOverviewRequestVO {

    /*资源名称*/
    @ApiModelProperty("资源名称")
    private String name;

    /*资源代码*/
    @ApiModelProperty("资源代码")
    private String code;

    /*file/db/service/all 交换方式*/
    @ApiModelProperty("交换方式 包含 file/db/service/all")
    private String shareMethod;

    /*资源提供方名称*/
    @ApiModelProperty("源提供方名称")
    private String deptName;

    /*资源提供方名称*/
    @ApiModelProperty("资源提供方名称")
    private String deptCode;

    /*格式：20121222 申请访问开始时间*/
    @ApiModelProperty("申请访问开始时间，格式：20121222 ")
    private String applyStartTime;

    /*格式:20121212 申请范围结束时间*/
    @ApiModelProperty("申请范围结束时间，格式：20121222 ")
    private String applyEndTime;

    /*订阅状态：wait_approve待审核/success 订阅成功/failed 已拒绝/stop 订阅终止状态/ all*/
    @ApiModelProperty("订阅状态：wait_approve待审核/success 订阅成功/failed 已拒绝/stop 订阅终止状态/ all")
    private String subStatus;

    /*资源订阅方部门*/
    @ApiModelProperty("资源订阅方部门")
    private String subDeptName;

    /*审批开始时间*/
    @ApiModelProperty("审批开始时间")
    private String approveStartTime;

    /*审批结束时间*/
    @ApiModelProperty("审批结束时间")
    private String approveEndTime;

    /*当前页码 (必填)*/
    @ApiModelProperty("当前页码")
    private int page;

    /*每页显示数量 (必填)*/
    @ApiModelProperty("每页显示数量")
    private int pageSize;

    SubscribeOverviewRequestVO(){
        super();
        this.page=1;
        this.pageSize=10;
    }

}
