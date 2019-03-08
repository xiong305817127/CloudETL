package com.ys.idatrix.metacube.api.beans;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: MetaDatabaseDTO
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/1/29
 */
@Data
public class MetaDatabaseDTO implements Serializable {

    private static final long serialVersionUID = 7629241075218786095L;

    /**
     * 存储系统类型
     */
    private String type;

    /**
     * ip地址
     */
    private String ip;

    /**
     * 端口
     */
    private String port;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 数据库名-模式名称/服务名（Oracle）
     */
    private String dbName;

}
