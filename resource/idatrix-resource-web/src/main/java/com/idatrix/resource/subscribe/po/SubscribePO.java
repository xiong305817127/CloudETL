package com.idatrix.resource.subscribe.po;

import java.util.Date;

/**
 * Created by Administrator on 2018/7/16.
 */
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

    /*创建人*/
    private String creator;

    /*创建时间*/
    private Date createTime;

    /*修改人*/
    private String modifier;

    /*修改时间*/
    private Date modifyTime;

    public Long getSeq() {
        return seq;
    }

    public void setSeq(Long seq) {
        this.seq = seq;
    }

    public String getSubNo() {
        return subNo;
    }

    public void setSubNo(String subNo) {
        this.subNo = subNo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getResourceId() {
        return resourceId;
    }

    public void setResourceId(Long resourceId) {
        this.resourceId = resourceId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getSubscribeUserName() {
        return subscribeUserName;
    }

    public void setSubscribeUserName(String subscribeUserName) {
        this.subscribeUserName = subscribeUserName;
    }

    public String getSubscribeReason() {
        return subscribeReason;
    }

    public void setSubscribeReason(String subscribeReason) {
        this.subscribeReason = subscribeReason;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endData) {
        this.endDate = endData;
    }

    public int getShareMethod() {
        return shareMethod;
    }

    public void setShareMethod(int shareMethod) {
        this.shareMethod = shareMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    public String getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(String suggestion) {
        this.suggestion = suggestion;
    }

    public Date getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(Date approveTime) {
        this.approveTime = approveTime;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }

    public String getSubKey() {
        return subKey;
    }

    public void setSubKey(String subKey) {
        this.subKey = subKey;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }

    @Override
    public String toString() {
        return "SubscribePO{" +
                "id=" + id +
                ", seq=" + seq +
                ", subNo='" + subNo + '\'' +
                ", resourceId=" + resourceId +
                ", deptId=" + deptId +
                ", deptName='" + deptName + '\'' +
                ", subscribeUserName='" + subscribeUserName + '\'' +
                ", subscribeReason='" + subscribeReason + '\'' +
                ", endDate=" + endDate +
                ", shareMethod=" + shareMethod +
                ", status='" + status + '\'' +
                ", approver='" + approver + '\'' +
                ", approverName='" + approverName + '\'' +
                ", suggestion='" + suggestion + '\'' +
                ", approveTime=" + approveTime +
                ", serviceUrl='" + serviceUrl + '\'' +
                ", subKey='" + subKey + '\'' +
                ", creator='" + creator + '\'' +
                ", createTime=" + createTime +
                ", modifier='" + modifier + '\'' +
                ", modifyTime=" + modifyTime +
                '}';
    }
}
