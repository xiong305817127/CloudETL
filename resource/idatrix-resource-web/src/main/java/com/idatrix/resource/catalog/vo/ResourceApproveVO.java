package com.idatrix.resource.catalog.vo;

/**
 * Created by Robin Wing on 2018-6-9.
 */
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

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getNextStatus() {
        return nextStatus;
    }

    public void setNextStatus(String nextStatus) {
        this.nextStatus = nextStatus;
    }

    public String getApproveAction() {
        return approveAction;
    }

    public void setApproveAction(String approveAction) {
        this.approveAction = approveAction;
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

    public Boolean getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(Boolean activeFlag) {
        this.activeFlag = activeFlag;
    }
}
