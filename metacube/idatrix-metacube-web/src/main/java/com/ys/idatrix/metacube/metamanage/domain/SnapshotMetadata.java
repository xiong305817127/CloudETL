package com.ys.idatrix.metacube.metamanage.domain;

import lombok.Data;

import java.util.Date;

@Data
public class SnapshotMetadata {
    private Long id; // 快照id

    private Integer version; // 快照版本

    private String details; // 快照详情

    private Long metaId;

    private String name;

    private String identification;

    private Integer publicStatus;

    private Long themeId;

    private String tags;

    private String remark;

    private String deptCodes;

    private Long renterId;

    private Boolean isGather;

    private Integer status;

    private Integer databaseType;

    private Integer resourceType;

    private Long schemaId;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date modifyTime;
}