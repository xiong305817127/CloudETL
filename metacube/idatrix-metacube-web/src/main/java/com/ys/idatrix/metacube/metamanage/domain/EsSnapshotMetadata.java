package com.ys.idatrix.metacube.metamanage.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @ClassName: EsSnapshotMetadata
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/1/23
 */
@Data
@Accessors(chain = true)
public class EsSnapshotMetadata {

    /**
     * 快照id
     */
    private Long id;

    /**
     * 快照版本
     */
    private Integer version;

    /**
     * 快照详情
     */
    private String details;

    /**
     * EsMetadata 表Id
     */
    private Long metaId;

    /**
     * 索引类型名称。固定值:default_type
     */
    private String name;

    /**
     * 描叙
     */
    private String identification;

    /**
     * 公开状态
     */
    private Integer publicStatus;

    /**
     * 主题id
     */
    private Long themeId;

    /**
     * 标签，可能多个，以，隔开
     */
    private String tags;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 是否开启 0:否 1:是
     */
    private Boolean isOpen = true;

    /**
     * 关联模式id
     */
    private Long schemaId;

    /**
     * 租户id
     */
    private Long renterId;

    /**
     * 创建人
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

    /**
     * 最大版本号。冗余
     */
    private Integer maxVersion;

    /**
     * 最大字段位置值。冗余
     */
    private Integer maxLocation;
}