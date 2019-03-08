package com.ys.idatrix.db.api.rdb.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @ClassName: RdbCreateTable
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Getter
@Setter
@ToString
@Accessors(chain = true)
public class RdbCreateTable extends RdbTable implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 字符集latin1和utf8, 默认utf8(只有mysql设置)(可不设置)
     */
    private RdbEnum.CharSet charSet = RdbEnum.CharSet.utf8;

    /**
     * 数据库引擎类型INNODB和MYISAM,默认INNODB(只有mysql设置)(可不设置)
     */
    private RdbEnum.MysqlEngineType mysqlEngineType = RdbEnum.MysqlEngineType.INNODB;


    public RdbCreateTable() {
        super();
    }


    public RdbCreateTable(String tableName, String schema, String comment, RdbPrimaryKey primaryKey,
                          ArrayList<RdbColumn> rdbColumns, ArrayList<RdbIndex> indices,
                          RdbEnum.CharSet charSet, RdbEnum.MysqlEngineType mysqlEngineType) {
        super(tableName, schema, comment, primaryKey, rdbColumns, indices);
        this.charSet = charSet;
        this.mysqlEngineType = mysqlEngineType;
    }

}
