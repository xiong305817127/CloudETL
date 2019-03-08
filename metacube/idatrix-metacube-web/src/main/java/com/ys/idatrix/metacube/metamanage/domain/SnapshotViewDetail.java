package com.ys.idatrix.metacube.metamanage.domain;

import lombok.Data;

@Data
public class SnapshotViewDetail {
    private Long id;

    private Integer version;

    private Long viewDetailId;

    private String viewSql;

    private String arithmetic;

    private String definiens;

    private String security;

    private String checkOption;

    private Long viewId;
}