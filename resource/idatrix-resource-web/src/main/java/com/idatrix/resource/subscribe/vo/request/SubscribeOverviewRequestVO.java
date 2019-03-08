package com.idatrix.resource.subscribe.vo.request;

/**
 * Created by Administrator on 2018/7/17.
 */
public class SubscribeOverviewRequestVO {

    /*资源名称*/
    private String name;

    /*资源代码*/
    private String code;

    /*file/db/service/all 交换方式*/
    private String shareMethod;

    /*资源提供方名称*/
    private String deptName;

    /*资源提供方名称*/
    private String deptCode;

    /*格式：20121222 申请访问开始时间*/
    private String applyStartTime;

    /*格式:20121212 申请范围结束时间*/
    private String applyEndTime;

    /*订阅状态：wait_approve待审核/success 订阅成功/failed 已拒绝/stop 订阅终止状态/ all*/
    private String subStatus;

    /*资源订阅方部门*/
    private String subDeptName;

    /*审批开始时间*/
    private String approveStartTime;

    /*审批结束时间*/
    private String approveEndTime;

    /*当前页码 (必填)*/
    private int page;

    /*每页显示数量 (必填)*/
    private int pageSize;

    SubscribeOverviewRequestVO(){
        super();
        this.page=1;
        this.pageSize=10;
    }


    public String getSubDeptName() {
        return subDeptName;
    }

    public void setSubDeptName(String subDeptName) {
        this.subDeptName = subDeptName;
    }

    public String getApproveStartTime() {
        return approveStartTime;
    }

    public void setApproveStartTime(String approveStartTime) {
        this.approveStartTime = approveStartTime;
    }

    public String getApproveEndTime() {
        return approveEndTime;
    }

    public void setApproveEndTime(String approveEndTime) {
        this.approveEndTime = approveEndTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getShareMethod() {
        return shareMethod;
    }

    public void setShareMethod(String shareMethod) {
        this.shareMethod = shareMethod;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getApplyStartTime() {
        return applyStartTime;
    }

    public void setApplyStartTime(String applyStartTime) {
        this.applyStartTime = applyStartTime;
    }

    public String getApplyEndTime() {
        return applyEndTime;
    }

    public void setApplyEndTime(String applyEndTime) {
        this.applyEndTime = applyEndTime;
    }

    public String getSubStatus() {
        return subStatus;
    }

    public void setSubStatus(String subStatus) {
        this.subStatus = subStatus;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
