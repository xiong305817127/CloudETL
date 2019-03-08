package com.idatrix.resource.catalog.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import java.util.List;

/**
 * Created by Robin Wing on 2018-6-7.
 */
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

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    public int getSubscribeFlag() {
        return subscribeFlag;
    }

    public void setSubscribeFlag(int subscribeFlag) {
        this.subscribeFlag = subscribeFlag;
    }

    public String getApproveAction() {
        return approveAction;
    }

    public void setApproveAction(String approveAction) {
        this.approveAction = approveAction;
    }

    public String getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(String approveTime) {
        this.approveTime = approveTime;
    }

    public String getApproveSuggestion() {
        return approveSuggestion;
    }

    public void setApproveSuggestion(String approveSuggestion) {
        this.approveSuggestion = approveSuggestion;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCatalogCode() {
        return catalogCode;
    }

    public void setCatalogCode(String catalogCode) {
        this.catalogCode = catalogCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLibType() {
        return libType;
    }

    public void setLibType(String libType) {
        this.libType = libType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public String getResourceCode() {
        return resourceCode;
    }

    public void setResourceCode(String resourceCode) {
        this.resourceCode = resourceCode;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
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

    public Long getDataCount() {
        return dataCount;
    }

    public void setDataCount(Long dataCount) {
        this.dataCount = dataCount;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public List<String> getHighlight() {
        return highlight;
    }

    public void setHighlight(List<String> highlight) {
        this.highlight = highlight;
    }
}
