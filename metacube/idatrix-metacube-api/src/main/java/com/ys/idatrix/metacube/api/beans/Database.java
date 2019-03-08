package com.ys.idatrix.metacube.api.beans;

import java.io.Serializable;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 数据库传输对象
 *
 * @author wzl
 */
@Data
@Accessors(chain = true)
public class Database implements Serializable {

    private static final long serialVersionUID = -1677650766925152313L;

    /**
     * 数据库id
     */
    private Long databaseId;

    /**
     * 数据库类型
     */
    private DatabaseTypeEnum databaseType;

    /**
     * 服务器id
     */
    private Long serverId;

    /**
     * 服务器ip
     */
    private String ip;

    /**
     * 数据库端口
     */
    private Integer port;

    /**
     * 模式列表
     */
    private List<Schema> schemaList;
}
