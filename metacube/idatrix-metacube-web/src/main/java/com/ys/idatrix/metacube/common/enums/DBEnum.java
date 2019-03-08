package com.ys.idatrix.metacube.common.enums;

import lombok.Getter;

/**
 * @ClassName DBEnum
 * @Description
 * @Author ouyang
 * @Date
 */
public enum DBEnum {

    ;

    public enum DBType {
        MYSQL("MYSQL"),
        ORACLE("ORACLE"),;

        private String name;

        DBType(String name) {
            this.name = name;
        }
    }

    /**
     * 字符集
     */
    public enum CharSet {

        latin1, utf8
    }


    // ====================================
    // ====================================
    // ====================================
    //                  mysql
    // ====================================
    // ====================================
    // ====================================

    /**
     * mysql数据引擎类型
     */
    public enum MysqlEngineType {
        INNODB, MYISAM
    }

    // mysql 表字段数据类型
    @Getter
    public enum MysqlTableDataType {
        // 数值类型
        TINYINT("TINYINT", "TINYINT(M)", "有符号.最大255字节.单个1字节.M<=255.非必须指定长度,默认4"),
        SMALLINT("SMALLINT", "SMALLINT(M)", "有符号.最大65535字节.单个2字节.M<=255.非必须指定长度,默认6"),
        NUMERIC("NUMERIC", "NUMERIC(P,S)", "P<=65,S<=30,且P>=S.非必须指定精度和小数位,默认10,0"),
        DECIMAL("DECIMAL", "DECIMAL(P,S)", "P<=65,S<=30,且P>=S.非必须指定精度和小数位,默认10,0"),
        DOUBLE("DOUBLE", "DOUBLE(P,S)", "双精度浮点.单个8字节.其中P<=255,S<=30,且P>=S.非必须指定长度"),
        FLOAT("FLOAT", "FLOAT(P,S)", "单精度浮点.单个4字节.P<=255,S<=30,且P>=S.非必须指定长度"),
        BIGINT("BIGINT", "BIGINT(M)", "单个8字节.非必须指定长度,默认M=20"),
        INTEGER("INTEGER", "INTEGER(M)", "有符号.最大4294967295(2^32-1)个字节.单个4字节.M<=255.非必须指定长度,默认11"),
        INT("INT", "INT(M)", "有符号.最大4294967295(2^32-1)个字节.单个4字节.M<=255.非必须指定长度,默认11"),
        MEDIUMINT("MEDIUMINT(M)", "MEDIUMINT", "M<=255.非必须指定长度,默认9"),
        REAL("REAL", "REAL(P,S)", "实数类型。NUMBER(63)精度.P<=255,S<=30,且P>=S.非必须指定长度"),

        // 字符串类型
        CHAR("CHAR", "CHAR(N)", "定长字符串.N<=255.非必须指定长度,默认1"),
        VARCHAR("VARCHAR", "VARCHAR(N)", "不定长字符串.最大65532字节.N<21845,需要指定长度"),
        ENUM("ENUM", "ENUM('boy','girl','secret')", "单选字符串"),
        SET("SET", "SET('a', 'b', 'c')", "多选字符串"),
        TINYTEXT("TINYTEXT", "TINYTEXT", "不用设置长度"),
        TEXT("TEXT", "TEXT", "最大长度为65535(2^16-1)个字符.无需指定长度"),
        MEDIUMTEXT("MEDIUMTEXT", "MEDIUMTEXT", "最大长度为16777215(2^24-1)个字符.无需指定长度"),
        LONGTEXT("LONGTEXT", "LONGTEXT", "最大长度为4294967295(2^32-1)个字符.无需指定长度"),

        // 二进制
        BIT("BIT", "BIT(N)", "N<=64.非必须指定长度,默认1"),
        BINARY("BINARY", "BINARY(N)", "N<=255.非必须指定长度,默认1"),
        VARBINARY("VARBINARY", "VARBINARY(N)", "M<=65535,需要指定长度"),
        TINYBLOB("TINYBLOB", "TINYBLOB", "最大255B.无需指定长度"),
        BLOB("BLOB", "BLOB", "mysql:最大65K;oracle:二进制大队象,最大4G.无需指定长度"),
        MEDIUMBLOB("MEDIUMBLOB", "MEDIUMBLOB", "最大16M.无需指定长度"),
        LONGBLOB("LONGBLOB", "LONGBLOB", "最大4G.无需指定长度"),

        // 时间类型
        YEAR("YEAR", "YEAR or YEAR(N)", "N只能是4.非必须指定长度,默认4"),
        DATE("DATE", "DATE", "单个3字节.无需指定长度"),
        DATETIME("DATETIME", "DATETIME", "单个4字节"),
        TIME("TIME", "TIME or TIME(N)", "0<=N<=6.非必须指定长度"),
        TIMESTAMP("TIMESTAMP", "TIMESTAMP or TIMESTAMP(N)", "0<=N<=6.可保留到纳秒"),

        // 几何类型（不常用）
        GEOMETRY("GEOMETRY", "GEOMETRY", "无需指定长度"),
        POINT("POINT", "POINT", "无需指定长度"),
        LINESTRING("LINESTRING", "LINESTRING", "无需指定长度"),
        POLYGON("POLYGON", "POLYGON", "无需指定长度"),
        MULTIPOINT("MULTIPOINT", "MULTIPOINT", "无需指定长度"),
        MULTILINESTRING("MULTILINESTRING", "MULTILINESTRING", "无需指定长度"),
        MULTIPOLYGON("MULTIPOLYGON", "MULTIPOLYGON", "无需指定长度"),
        GEOMETRYCOLLECTION("GEOMETRYCOLLECTION", "GEOMETRYCOLLECTION", "无需指定长度");

        private String name;
        private String format;
        private String desc;

        MysqlTableDataType(String name, String format, String desc) {
            this.name = name;
            this.format = format;
            this.desc = desc;
        }
    }

    // mysql 外键 删除or修改时触事件
    @Getter
    public enum MysqlFKTriggerAffairEnum {
        RESTRICT("RESTRICT", "同no action, 都是立即检查外键约束，默认时这个事件"),
        NO_ACTION("NO ACTION", "如果子表中有匹配的记录,则不允许对父表对应候选键进行update/delete操作"),
        CASCADE("CASCADE", "在父表上update/delete记录时，同步update/delete掉子表的匹配记录 "),
        SET_NULL("SET NULL", "在父表上update/delete记录时，将子表上匹配记录的列设为null, 要注意子表的外键列不能为not null");

        private String name;
        private String description;

        MysqlFKTriggerAffairEnum(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }

    // mysql 索引类型
    @Getter
    public enum MysqlIndexTypeEnum {
        NORMAL("INDEX", "普通索引，默认的索引类型"),
        UNIQUE("UNIQUE", "唯一索引"),
        FULLTEXT("FULLTEXT", "全文索引"),;

        private String name;
        private String description;

        MysqlIndexTypeEnum(String name, String description) {
            this.name = name;
            this.description = description;
        }

    }

    // mysql 索引方法类型
    @Getter
    public enum MysqlIndexMethodEnum {
        BTREE("BTREE", "用于对等比较，如\"=\"和\" <=>\"）   //<=> 安全的比对，用与对null值比较，语义类似is null（），默认的索引方法"),
        HASH("HASH", "（用于非对等比较，比如范围查询）>，>=，<，<=、BETWEEN、Like");

        private String name;
        private String description;

        MysqlIndexMethodEnum(String name, String description) {
            this.name = name;
            this.description = description;
        }

    }


    //=========== mysql view

    // mysql视图算法
    @Getter
    public enum MysqlViewAlgorithm {
        UNDEFINED("UNDEFINED", "默认算法是UNDEFINED(未定义的)：MySQL自动选择要使用的算法"),
        MERGE("MERGE", "合并"),
        TEMPTABLE("TEMPTABLE", "临时表");

        private String name;
        private String description;

        MysqlViewAlgorithm(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }

    // mysql视图安全性
    @Getter
    public enum MysqlViewSecurity {
        DEFINER("DEFINER", "定义(创建)视图的用户必须对视图所访问的表具有select权限，也就是说将来其他用户访问表的时候以定义者的身份，此时其他用户并没有访问权限。"),
        INVOKER("INVOKER", "访问视图的用户必须对视图所访问的表具有select权限。");

        private String name;
        private String description;

        MysqlViewSecurity(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }

    // mysql视图检查选项
    @Getter
    public enum MysqlViewCheckOption {
        CASCADE("CASCADE", "cascade是默认值，表示更新视图的时候，要满足视图和表的相关条件"),
        LOCAL("LOCAL", "local表示更新视图的时候，要满足该视图定义的一个条件即可");

        private String name;
        private String description;

        MysqlViewCheckOption(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }


    // ====================================
    // ====================================
    // ====================================
    //                  oracle
    // ====================================
    // ====================================
    // ====================================


    // oracle 表字段数据类型
    @Getter
    public enum OracleTableDataType {
        // 数值类型
        NUMBER("NUMBER", "NUMBER(P,S)", "NUMBER(P,S)最常见的数字类型，P<=38.非必须指定长度,精度"),
        NUMERIC("NUMERIC", "NUMERIC(P,S)", "与NUMBER相同,最终也是显示NUMBER"),
        FLOAT("FLOAT", "FLOAT(N)", "NUMBER的子类型.数 N 指示位的精度，可以存储的值的数目。1<=N<=126.非必须指定长度,默认126"),
        BINARY_DOUBLE("BINARY_DOUBLE", "BINARY_DOUBLE", "64位,双精度浮点数字数据类型。每个 BINARY_DOUBLE 的值需要 9 个字节，包括长度字节.无需指定长度"),
        BINARY_FLOAT("BINARY_FLOAT", "BINARY_FLOAT", "32位,单精度浮点数字数据类型。可以支持至少6位精度,每个 BINARY_FLOAT 的值需要 5 个字节，包括长度字节.无需指定长度"),

        // 字符串類型
        CHAR("CHAR", "CHAR(N)", "定长字符串.1<=N<=2000.非必须指定长度,默认1"),
        VARCHAR2("VARCHAR2", "VARCHAR2(N)", "不定长字符串.最多可以存储4,000字节,1<=N<=4000.需要指定长度"),
        NCHAR("NCHAR", "NCHAR(N)", "UNICODE格式字符。最多可以存储2,000字节,1<=N<=1000.非必须指定长度,默认1"),
        NVARCHAR2("NVARCHAR2", "NVARCHAR2(N)", "UNICODE格式字符。最多可以存储4,000字节,1<=N<=2000.需要指定长度"),
        LONG("LONG", "LONG", "不定长字符串.最大2G.无需指定长度"),
        CLOB("CLOB", "CLOB", "字符大型对象.最大4G.无需指定长度"),
        NCLOB("NCLOB", "NCLOB", "UNICODE格式字符.最大4G.无需指定长度"),

        // 二进制
        BLOB("BLOB", "BLOB", "二进制大对象,最大4G.不用设置长度.无需指定长度"),
        RAW("RAW", "RAW(N)", "变长二进制数据类型.最大2KB二进制数据,1<=N<=2000.需要指定长度"),
        LONG_RAW("LONG RAW", "LONG RAW", "最大2G二进制数据.无需指定长度"),
        BFILE("BFILE", "BFILE", "二进制.大于4G存在数据库外部的操作系统中;小于4G存储在数据库内部的操作系统文件中.只读"),

        // 时间类型
        DATE("DATE", "DATE", "日期值.保留到秒.无需指定长度"),
        TIMESTAMP("TIMESTAMP", "TIMESTAMP(P)", "时间戳数据类型，年月日时分秒字段，精度P指定了秒的精度，P<=6.非必须指定长度,默认6"),
        TIMESTAMP_WITH_TIME_ZONE("TIMESTAMP WITH TIME ZONE", "TIMESTAMP(6) WITH TIME ZONE", "TIMESTAMP类型的变种，它包含了时区偏移量的值"),
        TIMESTAMP_WITH_LOCAL_TIME_ZONE("TIMESTAMP WITH LOCAL TIME ZONE", "TIMESTAMP(6) WITH LOCAL TIME ZONE", "将数据库中存储的时间数据转换为客户端session时区的时间数据后返回给客户端"),
        INTERVAL_DAY_TO_SECOND("INTERVAL DAY TO SECOND", "INTERVAL DAY(2) TO SECOND(6)", "存储单位为天和秒的时间间隔"),
        INTERVAL_YEAR_TO_MONTH("INTERVAL YEAR TO MONTH", "INTERVAL YEAR(2) TO MONTH", "作为年和月的时间间隔存储"),

        //行标识符
        ROWID("ROWID", "ROWID", "每行的地址.伪列.物理地址.无需指定长度"),
        UROWID("UROWID", "UROWID", "每行的地址.伪列.逻辑地址.无需指定长度");

        private String name;
        private String format;
        private String desc;

        OracleTableDataType(String name, String format, String desc) {
            this.name = name;
            this.format = format;
            this.desc = desc;
        }
    }


    // oracle 索引类型
    @Getter
    public enum OracleIndexType {
        NON_UNIQUE("NON-UNIQUE", "非唯一索引"),
        UNIQUE("UNIQUE", "唯一索引"),
        BITMAP("BITMAP", "位图索引"),;
        private String name;
        private String desc;

        OracleIndexType(String name, String desc) {
            this.name = name;
            this.desc = desc;
        }
    }

    // oracle 外键删除时触发事件
    @Getter
    public enum OracleFKTriggerAffairEnum {
        CASCADE("CASCADE", "当主表数据删除时，对应的子表数据同时删除"),
        SET_NULL("SET NULL", "当主表数据删除时，对应的子表数据同时删除");

        private String name;
        private String description;

        OracleFKTriggerAffairEnum(String name, String description) {
            this.name = name;
            this.description = description;
        }
    }

    // 约束类型
    @Getter
    public enum ConstraintTypeEnum {
        PRIMARY_KEY(1, "主键约束"),
        FOREIGN_key(2, "外键约束"),
        UNIQUE(3, "唯一约束"),
        CHECK(4, "检查约束"),
        ;
        private int code;
        private String name;

        ConstraintTypeEnum(int code, String name) {
            this.name = name;
            this.code = code;
        }
    }

}

