package com.ys.idatrix.metacube.metamanage.domain;

import lombok.Data;

import java.util.Date;

@Data
public class SnapshotTableFkMysql {
    private Long id;

    private Integer version;

    private Long fkId;

    private String name;

    private String columnIds;

    private Long referenceSchemaId;

    private Long referenceTableId;

    private String referenceColumn;

    private String deleteTrigger;

    private String updateTrigger;

    private Integer location;

    private Long tableId;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date modifyTime;
}