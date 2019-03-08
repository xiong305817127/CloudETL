package com.idatrix.resource.exchange.vo.request;

/**
 * 交换订阅数据: 神马订阅交换请求数据
 *
 * @auther robin
 * @date   2018/11/07
 */
public class ExchangeSubscribeVO {

    /*订阅ID,用于异步查询结果*/
    private String subscribeId;

    /*信息资源编码*/
    private String resourceCode;

    /*资源订阅细项*/
    private String resourceColumnIds;

    /*资源类型 db/file*/
    private String resourceType;

    /*部门信息 暂定为部门统一社会信用编码*/
    private String subscribeDeptInfo;

    /*部门信息 部门名称*/
    private String subscribeDeptInfoName;

    /*订阅截止日志 一锤子买卖的订阅，暂时无用*/
    private String endTime;

    /*数据库：IP地址*/
    private String dbIp;

    /*数据库：端口*/
    private Long dbPort;

    /*数据库：类型*/
    private String dbType;

    /*数据库：用户*/
    private String dbUser;

    /*数据库：密码*/
    private String dbPassword;

    /*数据库名称*/
    private String dbName;

    /*数据库：模式*/
    private String dbSchemaName;

    /*数据库：表名*/
    private String dbTableName;

    public String getSubscribeId() {
        return subscribeId;
    }

    public void setSubscribeId(String subscribeId) {
        this.subscribeId = subscribeId;
    }

    public String getResourceCode() {
        return resourceCode;
    }

    public void setResourceCode(String resourceCode) {
        this.resourceCode = resourceCode;
    }

    public String getResourceColumnIds() {
        return resourceColumnIds;
    }

    public void setResourceColumnIds(String resourceColumnIds) {
        this.resourceColumnIds = resourceColumnIds;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getSubscribeDeptInfo() {
        return subscribeDeptInfo;
    }

    public void setSubscribeDeptInfo(String subscribeDeptInfo) {
        this.subscribeDeptInfo = subscribeDeptInfo;
    }

    public String getSubscribeDeptInfoName() {
        return subscribeDeptInfoName;
    }

    public void setSubscribeDeptInfoName(String subscribeDeptInfoName) {
        this.subscribeDeptInfoName = subscribeDeptInfoName;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDbIp() {
        return dbIp;
    }

    public void setDbIp(String dbIp) {
        this.dbIp = dbIp;
    }

    public Long getDbPort() {
        return dbPort;
    }

    public void setDbPort(Long dbPort) {
        this.dbPort = dbPort;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getDbUser() {
        return dbUser;
    }

    public void setDbUser(String dbUser) {
        this.dbUser = dbUser;
    }

    public String getDbPassword() {
        return dbPassword;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbSchemaName() {
        return dbSchemaName;
    }

    public void setDbSchemaName(String dbSchemaName) {
        this.dbSchemaName = dbSchemaName;
    }

    public String getDbTableName() {
        return dbTableName;
    }

    public void setDbTableName(String dbTableName) {
        this.dbTableName = dbTableName;
    }

    @Override
    public String toString() {
        return "ExchangeSubscribeVO{" +
                "subscribeId=" + subscribeId +
                ", resourceCode='" + resourceCode + '\'' +
                ", resourceColumnIds='" + resourceColumnIds + '\'' +
                ", resourceType='" + resourceType + '\'' +
                ", subscribeDeptInfo='" + subscribeDeptInfo + '\'' +
                ", subscribeDeptInfoName='" + subscribeDeptInfoName + '\'' +
                ", endTime='" + endTime + '\'' +
                ", dbIp='" + dbIp + '\'' +
                ", dbPort=" + dbPort +
                ", dbType='" + dbType + '\'' +
                ", dbUser='" + dbUser + '\'' +
                ", dbPassword='" + dbPassword + '\'' +
                ", dbName='" + dbName + '\'' +
                ", dbSchemaName='" + dbSchemaName + '\'' +
                ", dbTableName='" + dbTableName + '\'' +
                '}';
    }
}
