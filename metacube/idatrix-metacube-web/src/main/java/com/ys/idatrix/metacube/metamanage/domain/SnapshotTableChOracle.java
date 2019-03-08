package com.ys.idatrix.metacube.metamanage.domain;

import lombok.Data;

import java.util.Date;

@Data
public class SnapshotTableChOracle {
    private Long id;

    private Integer versions;

    private Long chId;

    private String name;

    private String checkSql;

    private Boolean isEnabled;

    private Long tableId;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date modifyTime;
}