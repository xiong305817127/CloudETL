package com.ys.idatrix.metacube.api.beans.dataswap;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: DataSource
 * @Description:TODO(数据源注册实体)
 * @author: chl
 * @date: 2017年8月4日 上午10:46:24
 */
@Data
public class DataSource implements Serializable {

    /**
     * 数据源编号
     */
    private Integer dsId;

    /**
     * 数据系统名称惟一
     */
    private String dsName;

    /**
     * 主机名称或ip
     */
    private String dbHostname;

    /**
     * 数据库类型
     */
    private String dsType;

    /**
     * 数据库用户名称
     */
    private String dbUsername;

    /**
     * 数据库用户密码
     */
    private String dbPassword;

    /**
     * 数据库端口
     */
    private String dbPort;

    /**
     * 数据库名称（oracle/dm 数据库实例名）
     */
    private String dbDatabasename;

    /**
     * 数据库实例
     */
    private String dbInstancename;

    /**
     * mysql 所在资源名称
     */
    private String dbResource;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date modifyTime;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 所在前置机编号
     */
    private Integer serverId;

    /**
     * 部门编号
     */
    private String departmentId;

    /**
     * 部门名称
     */
    private String departmentName;


    /**
     * 来源编号
     */
    private Integer sourceId;

    /**
     * 平台服务器编号
     */
    private String platformServerId;

    /**
     * 租户信息
     */
    private String renterId;

    /**
     * 存储数据源
     */
    private String storageDsName;

    /**
     * 存储数据源类型
     */
    private Integer storageDsType;

    /**
     * 存储数据源id
     */
    private Integer destinationId;

    /**
     * 创建类型 注册-register，创建-create
     */
    private String type = "";

}
