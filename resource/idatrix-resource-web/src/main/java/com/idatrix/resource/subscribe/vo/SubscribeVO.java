package com.idatrix.resource.subscribe.vo;

import com.idatrix.resource.catalog.vo.ResourceColumnVO;

import java.util.List;

/**
 * 订阅详情和配置订阅信息时候使用
 */
public class SubscribeVO {
    /*主键*/
    private Long id;

    /*订阅编号*/
    private String subNo;

    /*资源id*/
    private Long resourceId;

    /*订阅方名称*/
    private String deptName;

    /*申请人姓名*/
    private String subscribeUserName;

    /*申请时间*/
    private String subscribeTime;

    /*订阅事由*/
    private String subscribeReason;

    /*订阅截至日期 yyyy-mm-dd*/
    private String endDate;

    /*共享方式：1数据库，2文件下载，3webservice服务*/
    private int shareMethod;

    /*数据库分享方式：0 非数据库资源， 1数据库-数据库分享， 2数据库-服务分享*/
    private int dbShareMethod;

    /*当前状态：wait_approve待审核，success 订阅成功，failed 已拒绝   */
    private String status;

    /*审批人帐号*/
    private String approver;

    /*审批人姓名*/
    private String approverName;

    /*审批意见*/
    private String suggestion;

    /*审批时间*/
    private String approveTime;

    /*调用的服务url*/
    private String serviceUrl;

    /*UUID，服务参数之一*/
    private String subKey;

    /*订阅方前置机 名称*/
    private String terminalName;

    /*订阅方数据库*/
    private String ternimalDbName;

    /*订阅信息项*/
    private List<ResourceColumnVO> inputDbioList;

    /*查询条件*/
    private List<ResourceColumnVO> outputDbioList;

    public String getSubNo() {
        return subNo;
    }

    public void setSubNo(String subNo) {
        this.subNo = subNo;
    }

    public int getDbShareMethod() {
        return dbShareMethod;
    }

    public void setDbShareMethod(int dbShareMethod) {
        this.dbShareMethod = dbShareMethod;
    }

    public String getSubscribeTime() {
        return subscribeTime;
    }

    public void setSubscribeTime(String subscribeTime) {
        this.subscribeTime = subscribeTime;
    }

    public String getTerminalName() {
        return terminalName;
    }

    public void setTerminalName(String terminalName) {
        this.terminalName = terminalName;
    }

    public String getTernimalDbName() {
        return ternimalDbName;
    }

    public void setTernimalDbName(String ternimalDbName) {
        this.ternimalDbName = ternimalDbName;
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

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
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

    public String getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(String approveTime) {
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

    public List<ResourceColumnVO> getInputDbioList() {
        return inputDbioList;
    }

    public void setInputDbioList(List<ResourceColumnVO> inputDbioList) {
        this.inputDbioList = inputDbioList;
    }

    public List<ResourceColumnVO> getOutputDbioList() {
        return outputDbioList;
    }

    public void setOutputDbioList(List<ResourceColumnVO> outputDbioList) {
        this.outputDbioList = outputDbioList;
    }
}
