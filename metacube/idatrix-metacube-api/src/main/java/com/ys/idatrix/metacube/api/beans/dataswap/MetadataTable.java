package com.ys.idatrix.metacube.api.beans.dataswap;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: MetadataTable
 * @Description:
 * @Author: ZhouJian
 * @Date: 2018/8/7
 */
@Data
public class MetadataTable implements Serializable {

    private static final long serialVersionUID = 2979538139625428831L;

    /**
     * 订阅对象源元数据表ID
     */
    private int metaid;

    /**
     * 同一张表过有效期后重复订阅传递参数：上一轮订阅的元数据id。默认-1 表示没有。
     */
    private int previousMetaid = -1;

    /**
     * 所属部门.格式：["191"]
     */
    //private String dept;  新的元数据是基于表格，创建时候不需要存在表所属部门权限一说

    /**
     * 拥有者
     */
    private String owner;


    /**
     * 新创建数据库的数据源ID
     */
    private Long schemeId;

    /**
     * 模式
     */
    private String schema;

    /**
     * 创建者
     */
    private String creator;

    /**
     * 生成实体表状态 0 未生效 1 生效
     */
    private Integer status = 0;

    /**
     * 首次生成状态（0-首次生成状态,1-生成实体表）
     */
    private Integer firstStatus = 0;

    /**
     * 公开状态
     */
    private String publicStats = "1";

    /**
     * 来源类型 0-非直采、1-直采
     */
    private Integer sourceType = 0;

}
