package com.idatrix.resource.exchange.po;

import com.idatrix.resource.common.utils.DateTools;
import com.idatrix.resource.exchange.vo.request.ExchangeSubscribeVO;
import lombok.Data;

import java.util.Date;

/**
 * Created by Administrator on 2018/11/8.
 */
@Data
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
}
