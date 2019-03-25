package com.ys.idatrix.metacube.api.beans.dataswap;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName: FrontEndServer
 * @Description:
 * @author: zhoujian
 * @date: 2018/9/14
 */
@Data
public class FrontEndServer implements Serializable {

    /**
     * 前置机编号
     */
    private Integer id;

    /**
     * 前置机名称
     */
    private String serverName;

    /**
     * 服务器IP
     */
    private String serverIp;

    /**
     * 端口号
     */
    private String serverPort;

    /**
     * 对接来源单位
     */
    private String organization;

    /**
     * 管理员手机
     */
    private String phone;

    /**
     * 管理员邮箱
     */
    private String mail;

    /**
     * 租户编号
     */
    private String renterId;

    /**
     * 数据库管理员用户名(非必须，如果是前置机用来注册操作) modified by zhoujian on 20180907
     */
    private String dbUser;

    /**
     * 数据库管理员密码(非必须，如果是前置机用来注册操作) modified by zhoujian on 20180907
     */
    private String dbPassword;

    /**
     * 数据库端口号
     */
    private String dbPort;

    /**
     * 端口
     */
    private String ftpPort;

    /**
     * 机房位置
     */
    private String positionInfo;

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
     * 状态字段
     */
    private int status;

    /**
     * 前置机数据库类型
     */
    private Integer dsType;

    /**
     * 数据源类型
     */
    private String type;


}