package com.ys.idatrix.metacube.metamanage.domain;

import lombok.Data;

import java.util.Date;

/**
 * mc_snapshot_md_hive_field
 * @author robin
 */
@Data
public class McSnapshotMdHiveFieldPO {
    /**
     * 主键
     */
    private Long id;

    /**
     * 当前快照版本
     */
    private Integer version;

    /**
     * 版本变更详情
     */
    private String details;

    /**
     * 快照表主键
     */
    private Long originId;

    /**
     * 是否外表
     */
    private Boolean isExternalTable;

    /**
     * hdfs路径
     */
    private String location;

    /**
     * 每列之间的分隔符
     */
    private String fieldsTerminated;

    /**
     * 每行之间的分隔符
     */
    private String linesTerminated;

    /**
     * 空值处理
     */
    private String nullDefined;

    /**
     * 存储格式，TEXTFILE,SEQUENCEFILE,PARQUET,AVRO
     */
    private String storeFormat;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String modifier;

    /**
     * 修改时间
     */
    private Date modifyTime;

}