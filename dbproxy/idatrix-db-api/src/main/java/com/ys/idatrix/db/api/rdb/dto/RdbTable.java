package com.ys.idatrix.db.api.rdb.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @ClassName: RdbTable
 * @Description: 表对象
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class RdbTable implements Serializable {

    private static final long serialVersionUID = -3678082923946946032L;

    /**
     * 表名称(必须)
     */
    private String tableName;

    /**
     * 模式（非必输）- dm7、PostgreSql 必须要有
     */
    private String schema;

    /**
     * 表的注释(对应元数据的中文表名)
     */
    private String comment;

    /**
     * 对主键的描述
     */
    private RdbPrimaryKey primaryKey;

    /**
     * 每一列的描述
     */
    private ArrayList<RdbColumn> rdbColumns;

    /**
     * 索引集合
     */
    private ArrayList<RdbIndex> indices;

    public RdbTable() {
    }

    public RdbTable(String tableName, String schema, String comment, RdbPrimaryKey primaryKey) {
        this.tableName = tableName;
        this.schema = schema;
        this.comment = comment;
        this.primaryKey = primaryKey;
    }

    public RdbTable(String tableName, String comment, RdbPrimaryKey primaryKey, ArrayList<RdbColumn> rdbColumns) {
        this.tableName = tableName;
        this.comment = comment;
        this.primaryKey = primaryKey;
        this.rdbColumns = rdbColumns;
    }

    public RdbTable(String tableName, String schema, String comment, RdbPrimaryKey primaryKey, ArrayList<RdbColumn> rdbColumns) {
        this.tableName = tableName;
        this.schema = schema;
        this.comment = comment;
        this.primaryKey = primaryKey;
        this.rdbColumns = rdbColumns;
    }

    public RdbTable(String tableName, String comment, RdbPrimaryKey primaryKey, ArrayList<RdbColumn> rdbColumns, ArrayList<RdbIndex> indices) {
        this.tableName = tableName;
        this.comment = comment;
        this.primaryKey = primaryKey;
        this.rdbColumns = rdbColumns;
        this.indices = indices;
    }

    public RdbTable(String tableName, String schema, String comment, RdbPrimaryKey primaryKey, ArrayList<RdbColumn> rdbColumns, ArrayList<RdbIndex> indices) {
        this.tableName = tableName;
        this.schema = schema;
        this.comment = comment;
        this.primaryKey = primaryKey;
        this.rdbColumns = rdbColumns;
        this.indices = indices;
    }

    /**
     * DDL操作获表全名称。
     * dm = schema.tableName
     * @return
     */
    public String getFullTableName(){
        String fullTableName = this.tableName;
        if (StringUtils.isNotBlank(this.schema)) {
            fullTableName = this.schema + "." + fullTableName;
        }
        return fullTableName;
    }

}
