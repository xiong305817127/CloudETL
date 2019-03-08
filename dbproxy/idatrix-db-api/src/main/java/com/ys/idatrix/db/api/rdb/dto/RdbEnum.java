package com.ys.idatrix.db.api.rdb.dto;

import java.io.Serializable;

public class RdbEnum implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;


    /**
     * 数据类型
     *
     * @author lijie@gdbigdata.com
     * @version 1.0
     * @date 创建时间：2017年4月24日 上午9:48:35
     * @parameter
     * @return
     */
    public enum DataType_Bak {
        // 数字类型
        TINYINT("TINYINT", "MYSQL,ORACLE,DM7", "有符号.最大255字节.单个1字节.mysql:M<=255,默认4"),
        SMALLINT("SMALLINT", "MYSQL,ORACLE,DM7", "有符号.最大65535字节.单个2字节.M<=255,默认6"),
        NUMERIC("NUMERIC", "MYSQL", "M<=65,D<=30,且M>=D,默认10,0"),
        DECIMAL("DECIMAL", "MYSQL,ORACLE,DM7", "mysql:M<=65,D<=30,且M>=D,默认10,0"),
        REAL("REAL", "MYSQL,ORACLE,DM7", "实数类型。NUMBER(63)精度.mysql:M<=255,D<=30,且M>=D"),
        DOUBLE("DOUBLE", "MYSQL,DM7", "双精度浮点.单个8字节.其中M<=255,D<=30,且M>=D"),
        FLOAT("FLOAT", "MYSQL,ORACLE,DM7", "单精度浮点.单个4字节.oracle:NUMBER(38)精度.mysql:M<=255,D<=30,且M>=D"),
        BIGINT("BIGINT", "MYSQL,DM7", "单个8字节.默认M=20"),
        INTEGER("INTEGER", "MYSQL,ORACLE,DM7", "有符号.最大4294967295(2^32-1)个字节.单个4字节.M<=255,默认11"),
        INT("INT", "MYSQL,ORACLE,DM7", "有符号.最大4294967295(2^32-1)个字节.单个4字节.M<=255,默认11"),
        MEDIUMINT("MEDIUMINT", "MYSQL,ORACLE", "mysql:M<=255,默认9"),
        NUMBER("NUMBER", "ORACLE", ""),
        BINARY_DOUBLE("BINARY_DOUBLE", "ORACLE", "64位,双精度浮点数字数据类型。每个 BINARY_DOUBLE 的值需要 9 个字节，包括长度字节"),
        BINARY_FLOAT("BINARY_FLOAT", "ORACLE", "32位,单精度浮点数字数据类型。可以支持至少6位精度,每个 BINARY_FLOAT 的值需要 5 个字节，包括长度字节"),

        // 时间类型
        YEAR("YEAR", "MYSQL", "单个3字节"),
        DATE("DATE", "MYSQL,ORACLE,DM7", "单个3字节.mysql:日期值;oracle:保留到秒"),
        DATETIME("DATETIME", "MYSQL", "单个4字节"),
        TIME("TIME", "MYSQL,DM7", "单个3字节"),
        TIMESTAMP("TIMESTAMP", "MYSQL,ORACLE,DM7", "单个8字节.可保留到纳秒"),

        // 字符串類型
        CHAR("CHAR", "MYSQL,ORACLE,DM7", "定长字符串.oracle:最大2000字节;mysql:M<=255,默认1"),
        VARCHAR("VARCHAR", "MYSQL,DM7", "不定长字符串.最大65532字节.M<21845,必须设置长度"),
        ENUM("ENUM", "MYSQL", "单选字符串.如:ENUM('boy','girl','secret')"),
        SET("SET", "MYSQL", "多选字符串.如:SET('a', 'b', 'c')"),
        VARCHAR2("VARCHAR2", "ORACLE", "不定长字符串.最大4000字节"),
        LONG("LONG", "ORACLE", "不定长字符串.最大2G"),
        CLOB("CLOB", "ORACLE,DM7", "字符大型对象.最大4G"),
        NCLOB("NCLOB", "ORACLE", "最大4G"),
        TINYTEXT("TINYTEXT", "MYSQL", "不用设置长度"),
        TEXT("TEXT", "MYSQL,DM7", "最大长度为65535(2^16-1)个字符.不用设置长度"),
        MEDIUMTEXT("MEDIUMTEXT", "MYSQL", "最大长度为16777215(2^24-1)个字符.不用设置长度"),
        LONGTEXT("LONGTEXT", "MYSQL", "最大长度为4294967295(2^32-1)个字符.不用设置长度"),
        NVARCHAR2("NVARCHAR2", "ORACLE", "UNICODE格式字符。最多可以存储4,000字节"),
        NCHAR("NCHAR2", "ORACLE", "UNICODE格式字符。最多可以存储2,000字节"),

        // 二进制
        BIT("BIT", "MYSQL,DM7", "M<=64,默认1"),
        BINARY("BINARY", "MYSQL,DM7", "M<=255,默认1"),
        VARBINARY("VARBINARY", "MYSQL,DM7", "M<=65535,必须输入长度"),
        TINYBLOB("TINYBLOB", "MYSQL", "最大255B.不用设置长度"),
        BLOB("BLOB", "MYSQL,ORACLE,DM7", "mysql:最大65K;oracle:二进制大队象,最大4G.不用设置长度"),
        MEDIUMBLOB("MEDIUMBLOB", "MYSQL", "最大16M.不用设置长度"),
        LONGBLOB("LONGBLOB", "MYSQL", "最大4G.不用设置长度"),
        RAW("RAW", "ORACLE", "最大4KB二进制数据"),
        LONG_RAW("LONG RAW", "ORACLE", "最大2G二进制数据"),
        BFILE("BFILE", "ORACLE", "二进制.大于4G存在数据库外部的操作系统中;小于4G存储在数据库内部的操作系统文件中."),

        // 几何类型
        GEOMETRY("GEOMETRY", "MYSQL", "不用设置长度"),
        POINT("POINT", "MYSQL", "不用设置长度"),
        LINESTRING("LINESTRING", "MYSQL", "不用设置长度"),
        POLYGON("POLYGON", "MYSQL", "不用设置长度"),
        MULTIPOINT("MULTIPOINT", "MYSQL", "不用设置长度"),
        MULTILINESTRING("MULTILINESTRING", "MYSQL", "不用设置长度"),
        MULTIPOLYGON("MULTIPOLYGON", "MYSQL", "不用设置长度"),
        GEOMETRYCOLLECTION("GEOMETRYCOLLECTION", "MYSQL", "不用设置长度");

        /**
         * 类型名称
         */
        private String name;

        /**
         * 数据库类型
         */
        private String dbType;

        /**
         * 数据类型描叙
         */
        private String desc;

        DataType_Bak(String name, String dbType, String desc) {
            this.name = name;
            this.dbType = dbType;
            this.desc = desc;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDbType() {
            return dbType;
        }

        public void setDbType(String dbType) {
            this.dbType = dbType;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

    /**
     * MySQL 数据类型
     */
    public enum MysqlDataType {
        // 数值类型
        TINYINT("TINYINT", "TINYINT", "有符号.最大255字节.单个1字节.M<=255.非必须指定长度,默认4"),
        SMALLINT("SMALLINT", "SMALLINT", "有符号.最大65535字节.单个2字节.M<=255.非必须指定长度,默认6"),
        NUMERIC("NUMERIC", "NUMERIC(P,S)", "P<=65,S<=30,且P>=S.非必须指定精度和小数位,默认10,0"),
        DECIMAL("DECIMAL", "DECIMAL(P,S)", "P<=65,S<=30,且P>=S.非必须指定精度和小数位,默认10,0"),
        DOUBLE("DOUBLE", "DOUBLE(P,S)", "双精度浮点.单个8字节.其中P<=255,S<=30,且P>=S.非必须指定长度"),
        FLOAT("FLOAT", "FLOAT(P,S)", "单精度浮点.单个4字节.P<=255,S<=30,且P>=S.非必须指定长度"),
        BIGINT("BIGINT", "BIGINT", "单个8字节.非必须指定长度,默认M=20"),
        INTEGER("INTEGER", "INTEGER", "有符号.最大4294967295(2^32-1)个字节.单个4字节.M<=255.非必须指定长度,默认11"),
        INT("INT", "INT", "有符号.最大4294967295(2^32-1)个字节.单个4字节.M<=255.非必须指定长度,默认11"),
        MEDIUMINT("MEDIUMINT", "MEDIUMINT", "M<=255.非必须指定长度,默认9"),
        REAL("REAL", "REAL(P,S)", "实数类型。NUMBER(63)精度.P<=255,S<=30,且P>=S.非必须指定长度"),

        // 字符串類型
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

        /**
         * 类型名称
         */
        private String name;

        /**
         * 类型格式
         */
        private String format;

        /**
         * 数据类型描叙
         */
        private String desc;

        MysqlDataType(String name, String format, String desc) {
            this.name = name;
            this.format = format;
            this.desc = desc;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

    /**
     * Oracle 数据类型
     */
    public enum OracleDataType {
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


        /**
         * 类型名称
         */
        private String name;

        /**
         * 类型格式
         */
        private String format;

        /**
         * 数据类型描叙
         */
        private String desc;

        OracleDataType(String name, String format, String desc) {
            this.name = name;
            this.format = format;
            this.desc = desc;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }


    /**
     * 达梦 数据类型
     */
    public enum DM7DataType {
        // 数值类型
        TINYINT("TINYINT", "TINYINT", "精度为3，刻度为0的有符号精确数字，取值范围-128…127.无需指定长度"),
        SMALLINT("SMALLINT", "SMALLINT", "精度为5，刻度为0的有符号精确数字，取值范围-32,768…32,767.无需指定长度"),
        BIGINT("BIGINT", "BIGINT", "精度为19，刻度为0的有符号精确数字值，取值范围-2[63]…2[63]-1.无需指定长度"),
        INTEGER("INTEGER", "INTEGER", "精度为10，刻度为0的有符号精确数字，取值范围-2[31]…2[31]-1.无需指定长度"),
        INT("INT", "INT", "精度为10，刻度为0的有符号精确数字，取值范围-2[31]…2[31]-1.无需指定长度"),
        PLS_INTEGER("PLS_INTEGER", "PLS_INTEGER", "精度为10，刻度为0的有符号精确数字，取值范围-2[31]…2[31]-1.无需指定长度"),
        BYTE("BYTE", "BYTE", "与TINYINT相似，精度为3，刻度为0.无需指定长度"),
        DECIMAL("DECIMAL", "DECIMAL(P,S)", "精度为P，刻度为S的有符号精确数字值，1≤P≤38，S≤P.非必须指定长度,精度"),
        DEC("DEC", "DEC(P,S)", "精度为P，刻度为S的有符号精确数字值，1≤P≤38，S≤P.非必须指定长度,精度"),
        NUMERIC("NUMERIC", "NUMERIC(P,S)", "精度为P，刻度为S的有符号精确数字值，1≤P≤38，S≤P.非必须指定长度,精度"),
        NUMBER("NUMBER", "NUMBER(P,S)", "精度为P，刻度为S的有符号精确数字值，1≤P≤38，S≤P.非必须指定长度,精度"),
        REAL("REAL", "REAL", "二进制精度为24的有符号近似数字值，取值范围0或者绝对值为：10[-38]…10[38].无需指定长度"),
        FLOAT("FLOAT", "FLOAT(P)", "二进制精度为53的有符号近似数字值，取值范围0或者绝对值为：10[-308]…10[308].非必须指定长度,P<=126"),
        DOUBLE("DOUBLE", "DOUBLE(P)", "二进制精度为53的有符号近似数字值，取值范围0或者绝对值为：10[-308]…10[308].非必须指定长度,P<=126"),
        DOUBLE_PRECISION("DOUBLE PRECISION(P)", "DOUBLE PRECISION", "二进制精度为53，十进制精度为15。取值范围-1.7E + 308 ～ 1.7E + 308。非必须指定长度,P<=126"),

        // 字符串類型
        CHAR("CHAR", "CHAR(N)", "定长字符串.固定串长度为N的字符串，N<=8188.非必须指定长度,默认1"),
        CHARACTER("CHARACTER", "CHARACTER(N)", "定长字符串.固定串长度为N的字符串，N<=8188.非必须指定长度,默认1"),
        VARCHAR("VARCHAR", "VARCHAR(N)", "最大字符串长度为N的可变长度字符串，N<=8188.必须设置长度"),
        LONGVARCHAR("LONGVARCHAR","LONGVARCHAR","最大字符长度为2147483647，无需指定长度"),
        VARCHAR2("VARCHAR2", "VARCHAR2(N)", "最大字符串长度为N的可变长度字符串，N<=8188.必须设置长度"),
        CLOB("CLOB", "CLOB", "字符串大对象，可变长度的字符数据，最大长度为2G-1.最大4G"),
        TEXT("TEXT", "TEXT", "文本数据类型，可变长度的字符数据，最大长度为2G-1.不用设置长度"),

        // 二进制
        BIT("BIT", "BIT", "单个二进制数据.无需指定长度"),
        BINARY("BINARY", "BINARY(N)", "固定长度为N的二进制数据，N<=8188.非必须指定长度,默认1"),
        VARBINARY("VARBINARY", "VARBINARY(N)", "最大长度为N的可变长度二进制数据，N<=8188.非必须指定长度,默认8188"),
        LONGVARBINARY("LONGVARBINARY","LONGVARBINARY","最大长度为N的可变长度二进制数据，无须指定长度"),
        BLOB("BLOB", "BLOB", "二进制大对象,最大4G.不用设置长度"),
        IMAGE("IMAGE", "IMAGE", "影像数据类型，可变长度的二进制数据，最大长度为2G-1"),
        BFILE("BFILE", "BFILE", "二进制.大于4G存在数据库外部的操作系统中;小于4G存储在数据库内部的操作系统文件中.只读"),

        // 时间类型
        DATE("DATE", "DATE", "单个3字节保留到秒"),
        TIME("TIME", "TIME(P)", "时间数据类型，时分秒字段，精度P指定了秒的精度，P<=6.非必须指定长度,默认0"),
        DATETIME("DATETIME", "DATETIME(P)", "时间数据类型，时分秒字段，精度P指定了秒的精度，P<=6.非必须指定长度,默认6"),
        TIMESTAMP("TIMESTAMP", "TIMESTAMP(P)", "时间戳数据类型，年月日时分秒字段，精度P指定了秒的精度，P<=6.非必须指定长度,默认6"),
        TIME_WITH_TIME_ZONE("TIME WITH TIME ZONE", "TIME WITH TIME ZONE", "TIME类型的变种，它包含了时区偏移量的值"),
        TIMESTAMP_WITH_TIME_ZONE("TIMESTAMP WITH TIME ZONE", "TIMESTAMP(P) WITH TIME ZONE", "TIMESTAMP类型的变种，它包含了时区偏移量的值.P<=6.非必须指定长度,默认6"),
        TIMESTAMP_WITH_LOCAL_TIME_ZONE("TIMESTAMP WITH LOCAL TIME ZONE", "TIMESTAMP(P) WITH LOCAL TIME ZONE", "将数据库中存储的时间数据转换为客户端session时区的时间数据后返回给客户端.P<=6.非必须指定长度,默认6"),
        DATETIME_WITH_TIME_ZONE("DATETIME WITH TIME ZONE", "DATETIME(P) WITH TIME ZONE", "DATETIME类型的变种，它包含了时区偏移量的值.P<=6.非必须指定长度,默认6"),
        INTERVAL_YEAR("INTERVAL YEAR", "INTERVAL YEAR(P)", "时间间隔数据类型，年间隔，即两个日期之间的年数字，P为时间间隔的首项字段精度(后面简称为：首精度).P<=9.非必须指定长度,默认2"),
        INTERVAL_MONTH("INTERVAL MONTH", "INTERVAL MONTH(P)", "时间间隔数据类型，月间隔，即两个日期之间的月数字，P为时间间隔的首精度.P<=9.非必须指定长度,默认2"),
        INTERVAL_DAY("INTERVAL DAY", "INTERVAL DAY(P)", "时间间隔数据类型，日间隔，即为两个日期/时间之间的日数字，P为时间间隔的首精度.P<=9.非必须指定长度,默认2"),
        INTERVAL_HOUR("INTERVAL HOUR", "INTERVAL HOUR(P)", "时间间隔数据类型，时间隔，即为两个日期/时间之间的时数字，P为时间间隔的首精度.P<=9.非必须指定长度,默认2"),
        INTERVAL_MINUTE("INTERVAL MINUTE", "INTERVAL MINUTE(P)", "时间间隔数据类型，分间隔，即为两个日期/时间之间的分数字，P为时间间隔的首精度.P<=9.非必须指定长度,默认2"),
        INTERVAL_SECOND("INTERVAL SECOND", "INTERVAL SECOND(P,Q)", "时间间隔数据类型，秒间隔，即为两个日期/时间之间的秒数字，P为时间间隔的首精度，Q为时间间隔秒精度.非必须指定长度和精度:P<=9,默认2;Q<=6,默认6"),
        INTERVAL_YEAR_TO_MONTH("INTERVAL YEAR TO MONTH", "INTERVAL YEAR(P) TO MONTH", "时间间隔数据类型，年月间隔，即两个日期之间的年月数字，P为时间间隔的首精度.P<=9.非必须指定长度,默认2"),
        INTERVAL_DAY_TO_HOUR("INTERVAL DAY TO HOUR", "INTERVAL DAY(P) TO HOUR", "时间间隔数据类型，日时间隔，即为两个日期/时间之间的日时数字，P为时间间隔的首精度.P<=9.非必须指定长度,默认2"),
        INTERVAL_DAY_TO_MINUTE("INTERVAL DAY TO MINUTE", "INTERVAL DAY(P) TO MINUTE", "时间间隔数据类型，日时分间隔，即为两个日期/时间之间的日时分数字，P为时间间隔的首精度.P<=9.非必须指定长度,默认2"),
        INTERVAL_DAY_TO_SECOND("INTERVAL DAY TO SECOND", "INTERVAL DAY(P)TO SECOND(Q)", "时间间隔数据类型，日时分秒间隔，即为两个日期/时间之间的日时分秒数字，P为时间间隔的首精度，Q为时间间隔秒精度.非必须指定长度和精度:P<=9,默认2;Q<=6,默认6"),
        INTERVAL_HOUR_TO_MINUTE("INTERVAL HOUR TO MINUTE", "INTERVAL HOUR(P) TO MINUTE", "时间间隔数据类型，时分间隔，即为两个日期/时间之间的时分数字，P为时间间隔的首精度.P<=9.非必须指定长度,默认2"),
        INTERVAL_HOUR_TO_SECOND("INTERVAL HOUR TO SECOND", "INTERVAL HOUR(P) TO SECOND(Q)", "时间间隔数据类型，时分秒间隔，即为两个日期/时间之间的时分秒数字，P为时间间隔的首精度，Q为时间间隔秒精度.非必须指定长度和精度:P<=9,默认2;Q<=6,默认6"),
        INTERVAL_MINUTE_TO_SECOND("INTERVAL MINUTE TO SECOND", "INTERVAL MINUTE(P) TO SECOND(Q)", "时间间隔数据类型，分秒间隔，即为两个日期/时间之间的分秒间隔，P为时间间隔的首精度，Q为时间间隔秒精度.非必须指定长度和精度:P<=9,默认2;Q<=6,默认6");

        /**
         * 类型名称
         */
        private String name;

        /**
         * 类型格式
         */
        private String format;

        /**
         * 数据类型描叙
         */
        private String desc;

        DM7DataType(String name, String format, String desc) {
            this.name = name;
            this.format = format;
            this.desc = desc;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

    }


    /**
     * PostgreSQL 数据类型
     */
    public enum PostgreSQLDataType {
        // 数值类型
        SMALLINT("SMALLINT", "SMALLINT", "存储整数，小范围。2字节"),
        BIGINT("BIGINT", "BIGINT", "存储整数，大范围。8字节"),
        INTEGER("INTEGER", "INTEGER", "存储整数。使用这个类型可存储典型的整数。4字节"),
        DECIMAL("DECIMAL", "DECIMAL(P,S)", "用户指定的精度，精确"),
        NUMERIC("NUMERIC", "NUMERIC(P,S)", "用户指定的精度，精确"),
        REAL("REAL", "REAL", "可变精度，不精确。4字节"),
        DOUBLE("DOUBLE", "DOUBLE(P)", "可变精度，不精确。8字节"),
        SERIAL("SERIAL", "DOUBLE PRECISION", "自动递增整数。4字节"),
        BIGSERIAL("BIGSERIAL", "FLOAT(P)", "大的自动递增整数。8字节"),

        // 字符串類型
        CHAR("CHAR", "CHAR(N)", "固定长度字符串。 右边的空格填充到相等大小的字符。"),
        CHARACTER("CHARACTER", "CHARACTER(N)", "固定长度字符串。 右边的空格填充到相等大小的字符。"),
        VARCHAR("VARCHAR", "VARCHAR(N)", "可变长度字符串，.必须设置长度"),
        CHARACTER_VARYING("CHARACTER VARYING","CHARACTER VARYING(N)","可变长度字符串，指定长度"),
        TEXT("TEXT", "TEXT", "文本数据类型，可变长度字符串.不用设置长度"),

        // 二进制
        BYTEA("BYTEA", "BYTEA", "单个二进制数据.无需指定长度"),

        //货币
        MONEY("MONEY","MONEY","变长二进制串"),

        //布尔值
        BOOLEAN("BOOLEAN","BOOLEAN","它指定true或false的状态。1字节。取值范围：true/false/null"),

        //UUID
        UUID("UUID","UUID","（通用唯一标识符）写成小写的十六进制数字序列，由连字号，特别是一组8位数字，然后由三组4位数字，然后由一组12位数字分开几组，总32位，128位代表。如：550e8400-e29b-41d4-a716-446655440000"),

        // 时间类型
        DATE("DATE", "DATE", "单个3字节保留到秒"),
        TIME("TIME", "TIME(P)", "时间数据类型，时分秒字段，精度P指定了秒的精度，P<=6.非必须指定长度,默认0"),
        TIMESTAMP("TIMESTAMP", "TIMESTAMP(P)", "时间戳数据类型，年月日时分秒字段，精度P指定了秒的精度，P<=6.非必须指定长度,默认6"),
        TIME_WITH_TIME_ZONE("TIME WITH TIME ZONE", "TIME WITH TIME ZONE", "TIME类型的变种，它包含了时区偏移量的值"),
        TIMESTAMP_WITH_TIME_ZONE("TIMESTAMP WITH TIME ZONE", "TIMESTAMP(P) WITH TIME ZONE", "TIMESTAMP类型的变种，它包含了时区偏移量的值.P<=6.非必须指定长度,默认6"),
        INTERVAL_YEAR("INTERVAL YEAR", "INTERVAL YEAR(P)", "时间间隔数据类型，年间隔，即两个日期之间的年数字，P为时间间隔的首项字段精度(后面简称为：首精度).P<=9.非必须指定长度,默认2"),
        INTERVAL_MONTH("INTERVAL MONTH", "INTERVAL MONTH(P)", "时间间隔数据类型，月间隔，即两个日期之间的月数字，P为时间间隔的首精度.P<=9.非必须指定长度,默认2"),
        INTERVAL_DAY("INTERVAL DAY", "INTERVAL DAY(P)", "时间间隔数据类型，日间隔，即为两个日期/时间之间的日数字，P为时间间隔的首精度.P<=9.非必须指定长度,默认2"),
        INTERVAL_HOUR("INTERVAL HOUR", "INTERVAL HOUR(P)", "时间间隔数据类型，时间隔，即为两个日期/时间之间的时数字，P为时间间隔的首精度.P<=9.非必须指定长度,默认2"),
        INTERVAL_MINUTE("INTERVAL MINUTE", "INTERVAL MINUTE(P)", "时间间隔数据类型，分间隔，即为两个日期/时间之间的分数字，P为时间间隔的首精度.P<=9.非必须指定长度,默认2"),
        INTERVAL_SECOND("INTERVAL SECOND", "INTERVAL SECOND(P,Q)", "时间间隔数据类型，秒间隔，即为两个日期/时间之间的秒数字，P为时间间隔的首精度，Q为时间间隔秒精度.非必须指定长度和精度:P<=9,默认2;Q<=6,默认6"),
        INTERVAL_YEAR_TO_MONTH("INTERVAL YEAR TO MONTH", "INTERVAL YEAR(P) TO MONTH", "时间间隔数据类型，年月间隔，即两个日期之间的年月数字，P为时间间隔的首精度.P<=9.非必须指定长度,默认2"),
        INTERVAL_DAY_TO_HOUR("INTERVAL DAY TO HOUR", "INTERVAL DAY(P) TO HOUR", "时间间隔数据类型，日时间隔，即为两个日期/时间之间的日时数字，P为时间间隔的首精度.P<=9.非必须指定长度,默认2"),
        INTERVAL_DAY_TO_MINUTE("INTERVAL DAY TO MINUTE", "INTERVAL DAY(P) TO MINUTE", "时间间隔数据类型，日时分间隔，即为两个日期/时间之间的日时分数字，P为时间间隔的首精度.P<=9.非必须指定长度,默认2"),
        INTERVAL_DAY_TO_SECOND("INTERVAL DAY TO SECOND", "INTERVAL DAY(P)TO SECOND(Q)", "时间间隔数据类型，日时分秒间隔，即为两个日期/时间之间的日时分秒数字，P为时间间隔的首精度，Q为时间间隔秒精度.非必须指定长度和精度:P<=9,默认2;Q<=6,默认6"),
        INTERVAL_HOUR_TO_MINUTE("INTERVAL HOUR TO MINUTE", "INTERVAL HOUR(P) TO MINUTE", "时间间隔数据类型，时分间隔，即为两个日期/时间之间的时分数字，P为时间间隔的首精度.P<=9.非必须指定长度,默认2"),
        INTERVAL_HOUR_TO_SECOND("INTERVAL HOUR TO SECOND", "INTERVAL HOUR(P) TO SECOND(Q)", "时间间隔数据类型，时分秒间隔，即为两个日期/时间之间的时分秒数字，P为时间间隔的首精度，Q为时间间隔秒精度.非必须指定长度和精度:P<=9,默认2;Q<=6,默认6"),
        INTERVAL_MINUTE_TO_SECOND("INTERVAL MINUTE TO SECOND", "INTERVAL MINUTE(P) TO SECOND(Q)", "时间间隔数据类型，分秒间隔，即为两个日期/时间之间的分秒间隔，P为时间间隔的首精度，Q为时间间隔秒精度.非必须指定长度和精度:P<=9,默认2;Q<=6,默认6");

        /**
         * 类型名称
         */
        private String name;

        /**
         * 类型格式
         */
        private String format;

        /**
         * 数据类型描叙
         */
        private String desc;

        PostgreSQLDataType(String name, String format, String desc) {
            this.name = name;
            this.format = format;
            this.desc = desc;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

    }


    /**
     * 字符集
     */
    public enum CharSet {

        latin1, utf8
    }

    /**
     * mysql数据引擎类型
     */
    public enum MysqlEngineType {

        INNODB, MYISAM

    }

    /**
     * Mysql 索引类型
     */
    public enum MysqlIndexType {

        NORMAL("INDEX", "普通索引"),
        UNIQUE("UNIQUE", "唯一索引"),
        FULLTEXT("FULLTEXT", "全文索引");

        /**
         * 名称
         **/
        private String name;


        /**
         * 描叙
         **/
        private String desc;

        MysqlIndexType(String name, String desc) {
            this.name = name;
            this.desc = desc;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

    }

    /**
     * Oracle 索引类型
     */
    public enum OracleIndexType {

        NORMAL("INDEX", "普通索引"),
        UNIQUE("UNIQUE", "唯一索引"),
        BITMAP("BITMAP", "位图索引"),
        REVERSE("REVERSE", "反向键索引");

        /**
         * 名称
         **/
        private String name;


        /**
         * 描叙
         **/
        private String desc;

        OracleIndexType(String name, String desc) {
            this.name = name;
            this.desc = desc;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

    }

    /**
     * DM7 索引类型
     */
    public enum DM7IndexType {

        NORMAL("INDEX", "普通索引"),
        UNIQUE("UNIQUE", "唯一索引"),
        BITMAP("BITMAP", "位图索引");

        /**
         * 名称
         **/
        private String name;


        /**
         * 描叙
         **/
        private String desc;

        DM7IndexType(String name, String desc) {
            this.name = name;
            this.desc = desc;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

    }


    /**
     * Mysql 索引方法
     */
    public enum MysqlIndexMethod {
        BTREE("BTREE", "多叉树，多路径搜索树"),
        HASH("HASH", "=或<=>的等式比较");

        /**
         * 名称
         **/
        private String name;

        /**
         * 描叙
         **/
        private String desc;

        MysqlIndexMethod(String name, String desc) {
            this.name = name;
            this.desc = desc;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }


    /**
     * 数据库的种类
     *
     * @author lijie@gdbigdata.com
     * @version 1.0
     * @date 创建时间：2017年4月25日 上午9:46:36
     * @parameter
     * @return
     */
    public enum DBType {

        MYSQL, ORACLE, DM7, POSTGRESQL

    }

    /**
     * RDBMS - url、driverClass
     */
    public enum RDBLink {

        MYSQL("com.mysql.jdbc.Driver", "jdbc:mysql://{0}:{1}/{2}?characterEncoding=utf-8"),

        MYSQL_NO_DB("com.mysql.jdbc.Driver", "jdbc:mysql://{0}:{1}?characterEncoding=utf-8"),

        ORACLE("oracle.jdbc.driver.OracleDriver", "jdbc:oracle:thin:@{0}:{1}/{2}"),

        DM7("dm.jdbc.driver.DmDriver", "jdbc:dm://{0}:{1}/{2}"),

        DB2("com.ibm.db2.jcc.DB2Driver", "jdbc:db2://{0}:{1}/{2}"),

        SQLSERVER("com.microsoft.sqlserver.jdbc.SQLServerDriver", "jdbc:sqlserver://{0}:{1};databasename={2}"),

        SYBASE("com.sybase.jdbc3.jdbc.SybDriver", "jdbc:sybase:Tds:{0}:{1}/{2}"),

        POSTGRESQL("org.postgresql.Driver", "jdbc:postgresql://{0}:{1}/{2}");

        private String driverName;

        private String linkUrl;

        RDBLink(String driverName, String linkUrl) {
            this.driverName = driverName;
            this.linkUrl = linkUrl;
        }

        public String getDriverName() {
            return driverName;
        }

        public void setDriverName(String driverName) {
            this.driverName = driverName;
        }

        public String getLinkUrl() {
            return linkUrl;
        }

        public void setLinkUrl(String linkUrl) {
            this.linkUrl = linkUrl;
        }

    }

    //------------------------------------- 表空间定义涉及 -----------------------------------//

    /**
     * 表空间存储文件类型
     */
    public enum TBSStoreFileType {
        NULL, SMALLFILE, BIGFILE
    }

    /**
     * 表空间记录日志类型
     * 默认NULl 没有
     */
    public enum TBSLogType {
        NULL, LOGGING, NOLOGGING
    }

    /**
     * 闪回类型
     * 默认NULl 没有
     */
    public enum TBSFlashbackType {
        NULL, ON, OFF
    }

    /**
     * 自动拓展类型
     */
    public enum TBSAutoExpendType {
        NULL("NULL", ""),
        OFF("OFF", "AUTOEXTEND OFF"),
        ON("ON", "AUTOEXTEND {0} MAXSIZE {1}");

        /**
         * 名称
         */
        private String name;

        /**
         * sql 语句
         */
        private String sql;

        TBSAutoExpendType(String name, String sql) {
            this.name = name;
            this.sql = sql;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }
    }


    /**
     * 表空间存储压缩类型
     */
    public enum TBSCompressType {
        NULL("NULL", ""),
        COMPRESS("COMPRESS", "DEFAULT COMPRESS"),
        NOCOMPRESS("NOCOMPRESS", "DEFAULT NOCOMPRESS");

        /**
         * 名称
         */
        private String name;

        /**
         * sql 语句
         */
        private String sql;

        TBSCompressType(String name, String sql) {
            this.name = name;
            this.sql = sql;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }
    }

    /**
     * 表空间存储拓展区管理
     * 默认NULl 没有
     */
    public enum TBSExpandManageType {
        NULL("NULL", ""),
        LOCAL_UNIFORM("LOCAL_UNIFORM", "MANAGEMENT LOCAL UNIFORM SIZE {0}"),
        LOCAL_AUTO("LOCAL_AUTO", "MANAGEMENT LOCAL AUTOALLOCATE SEGMENT"),
        DICTIONARY("DICTIONARY", "MANAGEMENT DICTIONARY");

        /**
         * 名称
         */
        private String name;

        /**
         * sql 语句
         */
        private String sql;

        TBSExpandManageType(String name, String sql) {
            this.name = name;
            this.sql = sql;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }
    }

    /**
     * 如果表空间的存储拓展区管理是 LOCAL 则可设置是否手动片段管理
     * <p>
     * 默认auto
     */
    public enum TBSLocalManageMode {
        AUTO("AUTO", "SEGMENT SPACE MANAGEMENT AUTO"),
        MANUAL("MANUAL", "SEGMENT SPACE MANAGEMENT MANUAL");

        /**
         * 名称
         */
        private String name;

        /**
         * sql 语句
         */
        private String sql;

        TBSLocalManageMode(String name, String sql) {
            this.name = name;
            this.sql = sql;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }
    }


    /**
     * 加密算法
     */
    public enum TBSEncryptionType {
        NULL("NULL", ""),
        DES168("3DES168", "ENCRYPTION USING '3DES168'"),
        AES128("AES128", "ENCRYPTION USING 'AES128'"),
        AES192("AES128", "ENCRYPTION USING 'AES192'"),
        AES256("AES128", "ENCRYPTION USING 'AES256'");

        /**
         * 名称
         */
        private String name;

        /**
         * sql 语句
         */
        private String sql;

        TBSEncryptionType(String name, String sql) {
            this.name = name;
            this.sql = sql;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSql() {
            return sql;
        }

        public void setSql(String sql) {
            this.sql = sql;
        }
    }


}
