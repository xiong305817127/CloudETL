package com.ys.idatrix.metacube.api.beans;

import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 模式详情 聚合了Server和Database的一些信息
 *
 * @author wzl
 */
@Accessors(chain = true)
@Data
public class SchemaDetails implements Serializable {

    private static final long serialVersionUID = 5347380648025817217L;

    /**
     * 数据库id
     */
    private Long databaseId;

    /**
     * 数据库类型
     */
    private DatabaseTypeEnum databaseType;

    /**
     * 服务器ip
     */
    private String ip;

    /**
     * 数据库端口
     */
    private Integer port;

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
}
