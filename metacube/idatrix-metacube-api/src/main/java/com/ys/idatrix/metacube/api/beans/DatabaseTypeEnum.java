package com.ys.idatrix.metacube.api.beans;

/**
 * 数据库类型枚举
 *
 * @author wzl
 */
public enum DatabaseTypeEnum {

    /*1.mysql,2.oracle,3.dm,4.postgreSQL,5.hive,6.base,7.hdfs,8.ElasticSearch*/
    MYSQL(1, "MYSQL", "open"),
    ORACLE(2, "ORACLE", "open"),
    DM(3, "DM", "unknown"),
    POSTGRESQL(4, "POSTGRESQL", "unknown"),
    HIVE(5, "HIVE", "close"),
    HBASE(6, "HBASE", "close"),
    HDFS(7, "HDFS", "close"),
    ELASTICSEARCH(8, "ELASTICSEARCH", "close");

    private int code;
    private String name;

    /**
     * 状态 open: 开放给用户新建或注册 close: 不开放 unknown: 待定
     */
    private String status;

    DatabaseTypeEnum(int code, String name, String status) {
        this.code = code;
        this.name = name;
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public static String getName(int code) {
        for (DatabaseTypeEnum dsTypeEnum : DatabaseTypeEnum.values()) {
            if (dsTypeEnum.getCode() == code) {
                return dsTypeEnum.name;
            }
        }
        return null;
    }

    public static DatabaseTypeEnum getInstance(int code) {
        for (DatabaseTypeEnum dsTypeEnum : DatabaseTypeEnum.values()) {
            if (dsTypeEnum.getCode() == code) {
                return dsTypeEnum;
            }
        }
        return null;
    }

    public boolean isOpen() {
        return "open".equals(this.status);
    }
}
