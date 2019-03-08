package com.ys.idatrix.metacube.api.beans;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: MetadataDTO
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/1/29
 */
@Data
public class MetadataDTO implements Serializable {

    private static final long serialVersionUID = 6230841012005825634L;

    /**表ID 或 视图ID 或 目录ID**/
    private Integer metaId;

    /**表名 或 视图名**/
    private String metaName;

    // ================
    // ================
    // 以下字段属于数据地图,etl调用时为null
    // ================
    // ================

    /**模式ID**/
    private Integer schemaId;

    /**模式名**/
    private String schemaName;

    /** 数据库ID**/
    private Long databaseId;

    /**服务器ID**/
    private Long serverId;
}
