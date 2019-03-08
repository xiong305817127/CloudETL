package com.ys.idatrix.db.api.sql.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;


/**
 * @ClassName: SqlExecReqDto
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Data
@Accessors(chain = true)
public class SqlExecReqDto implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 模式名称
     */
    private String schemaName;

    /**
     * 模式id -> 数据分析IDE、服务开放对外调用必须传
     */
    private Long schemaId;

    /**
     * 是否需要验证权限-command中的表
     * 需要验证权限的操作 schemaId 必须不为空
     */
    private boolean needPermission;

    /**
     * sql语句
     */
    private String command;

}
