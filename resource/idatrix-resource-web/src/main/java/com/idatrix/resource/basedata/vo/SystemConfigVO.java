package com.idatrix.resource.basedata.vo;

/**
 * Created by Robin Wing on 2018-6-14.
 */
public class SystemConfigVO {

    /*主键*/
    private Long id;
    /*文件库根目录*/
    private String fileRoot;
    /*文件库根目录 列表*/
    private String fileRootIds;
    /*手工上报原始文件库根目录*/
    private String originFileRoot;
    /*列表*/
    private String originFileRootIds;
    /*数据库类型上报文件（cvs）限制大小，单位是Mb*/
    private int dbUploadSize;
    /*文件类型上报限制大小，单位Mb*/
    private int fileUploadSize;
    /*扫描上传文件时间，单位：分钟*/
    private int importInterval;
    /*部门录入人员角色*/
    private Long deptStaffRole;
    /*部门管理员角色*/
    private Long deptAdminRole;
    /*数据中心管理员角色*/
    private Long centerAdminRole;
    /*订阅审批角色*/
    private Long subApproverRole;
    /*更新时间*/
    private String updateTime;

    public Long getDeptStaffRole() {
        return deptStaffRole;
    }
    public void setDeptStaffRole(Long deptStaffRole) {
        this.deptStaffRole = deptStaffRole;
    }
    public String getFileRootIds() {
        return fileRootIds;
    }

    public void setFileRootIds(String fileRootIds) {
        this.fileRootIds = fileRootIds;
    }

    public String getOriginFileRootIds() {
        return originFileRootIds;
    }

    public void setOriginFileRootIds(String originFileRootIds) {
        this.originFileRootIds = originFileRootIds;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFileRoot() {
        return fileRoot;
    }

    public void setFileRoot(String fileRoot) {
        this.fileRoot = fileRoot;
    }

    public String getOriginFileRoot() {
        return originFileRoot;
    }

    public void setOriginFileRoot(String originFileRoot) {
        this.originFileRoot = originFileRoot;
    }

    public int getDbUploadSize() {
        return dbUploadSize;
    }

    public void setDbUploadSize(int dbUploadSize) {
        this.dbUploadSize = dbUploadSize;
    }

    public int getFileUploadSize() {
        return fileUploadSize;
    }

    public void setFileUploadSize(int fileUploadSize) {
        this.fileUploadSize = fileUploadSize;
    }

    public int getImportInterval() {
        return importInterval;
    }

    public void setImportInterval(int importInterval) {
        this.importInterval = importInterval;
    }

    public Long getDeptAdminRole() {
        return deptAdminRole;
    }

    public void setDeptAdminRole(Long deptAdminRole) {
        this.deptAdminRole = deptAdminRole;
    }

    public Long getCenterAdminRole() {
        return centerAdminRole;
    }

    public void setCenterAdminRole(Long centerAdminRole) {
        this.centerAdminRole = centerAdminRole;
    }

    public Long getSubApproverRole() {
        return subApproverRole;
    }

    public void setSubApproverRole(Long subApproverRole) {
        this.subApproverRole = subApproverRole;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
