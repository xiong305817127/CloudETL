package com.ys.idatrix.db.api.rdb.dto;

import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName: RDBIndex
 * @Description:
 * @Author: ZhouJian
 * @Date: 2017/12/21
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@Accessors(chain = true)
public class RdbIndex implements Serializable {

    private static final long serialVersionUID = -4176229313126747611L;

    /**
     * 索引id(唯一，必须)
     * 创建表忽略；修改、删除需要
     */
    private String id;

    /**
     * 索引名称(必须)
     */
    private String name;

    /**
     * 索引类型(必须)
     */
    private String indexType;

    /**
     * 索引方法（Mysql有。Oracle、DM7无）
     */
    private RdbEnum.MysqlIndexMethod mysqlIndexMethod;

    /**
     * 索引列（必须）
     */
    private List<String> columns;

}
