package com.idatrix.resource.subscribe.vo;

/**
 * 订阅概览信息
 */
public class SubscribeOverviewVO {

    /*订阅ID */
    private Long id;

    /*资源ID*/
    private Long resourceId;

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

    public int getDbShareMethod() {
        return dbShareMethod;
    }

    public void setDbShareMethod(int dbShareMethod) {
        this.dbShareMethod = dbShareMethod;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public String getSubscribeDeptName() {
        return subscribeDeptName;
    }

    public void setSubscribeDeptName(String subscribeDeptName) {
        this.subscribeDeptName = subscribeDeptName;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public String getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(String approveTime) {
        this.approveTime = approveTime;
    }

    public String getSubNo() {
        return subNo;
    }

    public void setSubNo(String subNo) {
        this.subNo = subNo;
    }

    public String getSubscribeUserName() {
        return subscribeUserName;
    }

    public void setSubscribeUserName(String subscribeUserName) {
        this.subscribeUserName = subscribeUserName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(String applyDate) {
        this.applyDate = applyDate;
    }

    public String getSubscribeStatus() {
        return subscribeStatus;
    }

    public void setSubscribeStatus(String subscribeStatus) {
        this.subscribeStatus = subscribeStatus;
    }

    public int getShareMethod() {
        return shareMethod;
    }

    public void setShareMethod(int shareMethod) {
        this.shareMethod = shareMethod;
    }

    public String getExchangeStatus() {
        return exchangeStatus;
    }

    public void setExchangeStatus(String exchangeStatus) {
        this.exchangeStatus = exchangeStatus;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "SubscribeOverviewVO{" +
                "id=" + id +
                ", resourceId=" + resourceId +
                ", subNo='" + subNo + '\'' +
                ", code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", deptName='" + deptName + '\'' +
                ", applyDate='" + applyDate + '\'' +
                ", subscribeUserName='" + subscribeUserName + '\'' +
                ", subscribeStatus='" + subscribeStatus + '\'' +
                ", shareMethod=" + shareMethod +
                ", exchangeStatus='" + exchangeStatus + '\'' +
                ", endTime='" + endTime + '\'' +
                ", status='" + status + '\'' +
                ", suggestion='" + suggestion + '\'' +
                ", approveTime='" + approveTime + '\'' +
                '}';
    }
}
