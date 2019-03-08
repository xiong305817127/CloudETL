package com.ys.idatrix.metacube.api.beans;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: MetaDbResourceDTO
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/7
 */
@Accessors(chain = true)
@Data
public class MetaDbResourceDTO implements Serializable {

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
     * 模式显示名称
     */
    private String schemaShowName;

    /**
     * 模式用户名
     */
    private String username;

    /**
     * 模式密码
     */
    private String password;

    /**
     * 表、视图集合
     */
    private Map<Long, String> metadataMap;

    /**
     * schemaName 显示名称
     *
     * @return
     */
    public String getSchemaShowName() {
        switch (databaseType) {
            case MYSQL:
            case ORACLE:
            case DM:
            case POSTGRESQL:
                return this.getIp() + "-" + schemaName;
            default:
                return schemaName;
        }
    }
}
