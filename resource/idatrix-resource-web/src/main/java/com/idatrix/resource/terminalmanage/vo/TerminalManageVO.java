package com.idatrix.resource.terminalmanage.vo;

public class TerminalManageVO {
    /*主键*/
    private Long id;

    /*部门ID*/
    private String deptId;

    /*部门最后一个ID*/
    private String deptFinalId;

    /*部门编码*/
    private String deptCode;

    /*部门名称*/
    private String deptName;

    /*前置机名称*/
    private String tmName;

    /*前置机IP*/
    private String tmIP;

    /*schema名称*/
    private String schemaName;

    /*schema ID*/
    private String schemaId;

    /*数据库ID*/
    private String tmDBId;

    /*数据库名称*/
    private String tmDBName;

    /*数据库端口*/
    private String tmDBPort;

    /*数据库类型*/
    private String tmDBType;

    /*交换文件存放根目录*/
    private String sftpSwitchRoot;

    /*sftp对应的hdfs根目录 防止元数据改变目录后, 重新定位导致文件丢失*/
    private String hdfsSwitchRoot;

    /*sftp端口*/
    private String sftpPort;

    /*sftp用户名*/
    private String sftpUsername;

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(String schemaId) {
        this.schemaId = schemaId;
    }

    public String getDeptFinalId() {
        return deptFinalId;
    }

    public void setDeptFinalId(String deptFinalId) {
        this.deptFinalId = deptFinalId;
    }

    public String getTmDBId() {
        return tmDBId;
    }

    public void setTmDBId(String tmDBId) {
        this.tmDBId = tmDBId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDeptId() {
        return deptId;
    }

    public void setDeptId(String deptId) {
        this.deptId = deptId;
    }

    public String getDeptCode() {
        return deptCode;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getTmName() {
        return tmName;
    }

    public void setTmName(String tmName) {
        this.tmName = tmName;
    }

    public String getTmIP() {
        return tmIP;
    }

    public void setTmIP(String tmIP) {
        this.tmIP = tmIP;
    }

    public String getTmDBName() {
        return tmDBName;
    }

    public void setTmDBName(String tmDBName) {
        this.tmDBName = tmDBName;
    }

    public String getTmDBPort() {
        return tmDBPort;
    }

    public void setTmDBPort(String tmDBPort) {
        this.tmDBPort = tmDBPort;
    }

    public String getTmDBType() {
        return tmDBType;
    }

    public void setTmDBType(String tmDBType) {
        this.tmDBType = tmDBType;
    }

    public String getSftpSwitchRoot() {
        return sftpSwitchRoot;
    }

    public void setSftpSwitchRoot(String sftpSwitchRoot) {
        this.sftpSwitchRoot = sftpSwitchRoot;
    }

    public String getHdfsSwitchRoot() {
        return hdfsSwitchRoot;
    }

    public void setHdfsSwitchRoot(String hdfsSwitchRoot) {
        this.hdfsSwitchRoot = hdfsSwitchRoot;
    }

    public String getSftpPort() {
        return sftpPort;
    }

    public void setSftpPort(String sftpPort) {
        this.sftpPort = sftpPort;
    }

    public String getSftpUsername() {
        return sftpUsername;
    }

    public void setSftpUsername(String sftpUsername) {
        this.sftpUsername = sftpUsername;
    }

    @Override
    public String toString() {
        return "TerminalManageVO{" +
                "id=" + id +
                ", deptId=" + deptId +
                ", deptCode=" + deptCode +
                ", deptName=" + deptName +
                ", tmName='" + tmName + '\'' +
                ", tmIP='" + tmIP + '\'' +
                ", tmDBName='" + tmDBName + '\'' +
                ", tmDBPort='" + tmDBPort + '\'' +
                ", sftpSwitchRoot='" + sftpSwitchRoot + '\'' +
                ", hdfsSwitchRoot='" + hdfsSwitchRoot + '\'' +
                ", sftpPort='" + sftpPort + '\'' +
                '}';
    }
}
