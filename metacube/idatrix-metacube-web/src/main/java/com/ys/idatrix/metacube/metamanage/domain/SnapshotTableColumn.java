package com.ys.idatrix.metacube.metamanage.domain;

import lombok.Data;

import java.util.Date;

@Data
public class SnapshotTableColumn {
    private Long id;

    private Integer version;

    private Long columnId;

    private String columnName;

    private String columnType;

    private String typeLength;

    private String typePrecision;

    private Boolean isPk;

    private Boolean isAutoIncrement;

    private Boolean isNull;

    private String defaultValue;

    private String description;

    private Integer location;

    private Long tableId;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date modifyTime;

    private Boolean isPartition;

    private Integer indexPartition;

    private Boolean isBucket;
}