package com.ys.idatrix.metacube.common.helper;


import com.ys.idatrix.graph.service.api.def.DatabaseType;
import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;

/**
 * 转换数据地图数据库类型
 *
 * @author wzl
 */
public final class GraphDatabaseTypeConvert {

    public static DatabaseType getGraphDatabaseType(int type) {
        if (type == DatabaseTypeEnum.MYSQL.getCode()) {
            return DatabaseType.MySQL;
        }
        if (type == DatabaseTypeEnum.ORACLE.getCode()) {
            return DatabaseType.Oracle;
        }
        if (type == DatabaseTypeEnum.POSTGRESQL.getCode()) {
            return DatabaseType.PostgreSQL;
        }
        if (type == DatabaseTypeEnum.HDFS.getCode()) {
            return DatabaseType.HDFS;
        }
        if (type == DatabaseTypeEnum.HIVE.getCode()) {
            return DatabaseType.Hive;
        }
        if (type == DatabaseTypeEnum.HBASE.getCode()) {
            return DatabaseType.Hbase;
        }
        return null;
    }
}
