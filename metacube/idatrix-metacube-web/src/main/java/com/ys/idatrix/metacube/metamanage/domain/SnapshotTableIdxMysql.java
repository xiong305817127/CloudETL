package com.ys.idatrix.metacube.metamanage.domain;

import lombok.Data;

import java.util.Date;

@Data
public class SnapshotTableIdxMysql {
    private Long id;

    private Integer version;

    private Long indexId;   // index id

    private String indexName;

    private String columnIds;

    private String subdivision;

    private String indexType;

    private String indexMethod;

    private Integer location;

    private Long tableId;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date modifyTime;
}