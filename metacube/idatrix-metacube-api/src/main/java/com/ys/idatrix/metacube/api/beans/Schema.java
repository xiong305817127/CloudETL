package com.ys.idatrix.metacube.api.beans;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 模式传输对象
 *
 * @author wzl
 */
@Data
@Accessors(chain = true)
public class Schema implements Serializable {

    private static final long serialVersionUID = -3126518854695447611L;

    /**
     * 模式id
     */
    private Long schemaId;

    /**
     * 模式名称
     */
    private String schemaName;

    /**
     * 模式用户名
     */
    private String username;

    /**
     * 模式密码
     */
    private String password;

    /**
     * 数据库服务名 oracle专用
     */
    private String serviceName;

    /**
     * 数据库id
     */
    private Long databaseId;
}
