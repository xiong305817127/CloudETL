package com.idatrix.resource.exchange.po;

import com.idatrix.resource.common.utils.DateTools;
import com.idatrix.resource.exchange.vo.request.ExchangeSubscribeVO;

import java.util.Date;

/**
 * Created by Administrator on 2018/11/8.
 */
public class ExchangeSubscribeInfoPO {

    //主键ID
    private Long id;

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

    /*部门信息，部门名称*/
    private String subscribeDeptName;


    /*订阅截止日志 一锤子买卖的订阅，暂时无用*/
    private Date endTime;

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

    /*交换任务状态：fail/success/wait/running*/
    private String status;

    /*交换任务执行信息*/
    private String execInfo;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date modifyTime;

    public ExchangeSubscribeInfoPO(ExchangeSubscribeVO esVO, String user, String deptName){
        super();
        this.subscribeId = esVO.getSubscribeId();
        this.resourceCode = esVO.getResourceCode();
        this.resourceType = esVO.getResourceType();
        this.subscribeDeptInfo = esVO.getSubscribeDeptInfo();
        this.subscribeDeptName = deptName;

        this.endTime = DateTools.parseDate(esVO.getEndTime());
        this.dbIp = esVO.getDbIp();
        this.dbPort = esVO.getDbPort();
        this.dbType = esVO.getDbType();
        this.dbUser = esVO.getDbUser();
        this.dbPassword = esVO.getDbPassword();
        this.dbName = esVO.getDbName();
        this.dbSchemaName = esVO.getDbSchemaName();
        this.dbTableName = esVO.getDbTableName();

        this.createTime = new Date();
        this.modifyTime = new Date();
        this.creator = user;
        this.modifier = user;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExecInfo() {
        return execInfo;
    }

    public void setExecInfo(String execInfo) {
        this.execInfo = execInfo;
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

    public String getSubscribeDeptName() {
        return subscribeDeptName;
    }

    public void setSubscribeDeptName(String subscribeDeptName) {
        this.subscribeDeptName = subscribeDeptName;
    }

    @Override
    public String toString() {
        return "ExchangeSubscribeInfoPO{" +
                "id=" + id +
                ", subscribeId=" + subscribeId +
                ", resourceCode='" + resourceCode + '\'' +
                ", resourceColumnIds='" + resourceColumnIds + '\'' +
                ", resourceType='" + resourceType + '\'' +
                ", subscribeDeptInfo='" + subscribeDeptInfo + '\'' +
                ", subscribeDeptName='" + subscribeDeptName + '\'' +
                ", endTime=" + endTime +
                ", dbIp='" + dbIp + '\'' +
                ", dbPort=" + dbPort +
                ", dbType='" + dbType + '\'' +
                ", dbUser='" + dbUser + '\'' +
                ", dbPassword='" + dbPassword + '\'' +
                ", dbName='" + dbName + '\'' +
                ", dbSchemaName='" + dbSchemaName + '\'' +
                ", dbTableName='" + dbTableName + '\'' +
                ", status='" + status + '\'' +
                ", execInfo='" + execInfo + '\'' +
                ", creator='" + creator + '\'' +
                ", createTime=" + createTime +
                ", modifier='" + modifier + '\'' +
                ", modifyTime=" + modifyTime +
                '}';
    }
}
