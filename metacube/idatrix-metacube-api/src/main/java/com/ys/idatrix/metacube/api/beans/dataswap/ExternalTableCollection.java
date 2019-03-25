package com.ys.idatrix.metacube.api.beans.dataswap;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: ExternalTableCollection
 * @Description:
 * @Author: ZhouJian
 * @Date: 2018/11/8
 */
@Data
public class ExternalTableCollection implements Serializable {

    private static final long serialVersionUID = -831983415387732769L;

    /**数据库：用户**/
    private String userName;

    /**数据库：IP地址**/
    private String dbIp;

    /**数据库：端口**/
    private Long dbPort;

    /**数据库：类型**/
    private String dbType;

    /**数据库：用户**/
    private String dbUser;

    /**数据库：密码**/
    private String dbPassword;

    /**数据库名称**/
    private String dbName;

    /**数据库：模式**/
    private String dbSchemaName;

    /**数据库：表名**/
    private String dbTableName;

}
