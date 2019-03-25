package com.idatrix.resource.subscribe.utils;

import org.apache.commons.lang3.StringUtils;

/**
 * 数据脱敏，只有对应数据类型为字符类型才能进行处理
 */
public enum DataMaskingTypeEnum {

    //Mysql 字符串類型
    MYSQL_CHAR("MYSQL","CHAR", "CHAR(N)", "定长字符串.N<=255.非必须指定长度,默认1"),
    MYSQL_VARCHAR("MYSQL","VARCHAR", "VARCHAR(N)", "不定长字符串.最大65532字节.N<21845,需要指定长度"),
    MYSQL_ENUM("MYSQL","ENUM", "ENUM('boy','girl','secret')", "单选字符串"),
    MYSQL_SET("MYSQL","SET", "SET('a', 'b', 'c')", "多选字符串"),
    MYSQL_TINYTEXT("MYSQL","TINYTEXT", "TINYTEXT", "不用设置长度"),
    MYSQL_TEXT("MYSQL","TEXT", "TEXT", "最大长度为65535(2^16-1)个字符.无需指定长度"),
    MYSQL_MEDIUMTEXT("MYSQL","MEDIUMTEXT", "MEDIUMTEXT", "最大长度为16777215(2^24-1)个字符.无需指定长度"),
    MYSQL_LONGTEXT("MYSQL","LONGTEXT", "LONGTEXT", "最大长度为4294967295(2^32-1)个字符.无需指定长度"),


    // Oracle字符串類型
    ORACLE_CHAR("ORACLE","CHAR", "CHAR(N)", "定长字符串.1<=N<=2000.非必须指定长度,默认1"),
    ORACLE_VARCHAR2("ORACLE","VARCHAR2", "VARCHAR2(N)", "不定长字符串.最多可以存储4,000字节,1<=N<=4000.需要指定长度"),
    ORACLE_NCHAR("ORACLE","NCHAR", "NCHAR(N)", "UNICODE格式字符。最多可以存储2,000字节,1<=N<=1000.非必须指定长度,默认1"),
    ORACLE_NVARCHAR2("ORACLE","NVARCHAR2", "NVARCHAR2(N)", "UNICODE格式字符。最多可以存储4,000字节,1<=N<=2000.需要指定长度"),
    ORACLE_LONG("ORACLE","LONG", "LONG", "不定长字符串.最大2G.无需指定长度"),
    ORACLE_CLOB("ORACLE","CLOB", "CLOB", "字符大型对象.最大4G.无需指定长度"),
    ORACLE_NCLOB("ORACLE","NCLOB", "NCLOB", "UNICODE格式字符.最大4G.无需指定长度"),

    //DM字符串類型
    DM_CHAR("DM","CHAR", "CHAR(N)", "定长字符串.固定串长度为N的字符串，N<=8188.非必须指定长度,默认1"),
    DM_CHARACTER("DM","CHARACTER", "CHARACTER(N)", "定长字符串.固定串长度为N的字符串，N<=8188.非必须指定长度,默认1"),
    DM_VARCHAR("DM","VARCHAR", "VARCHAR(N)", "最大字符串长度为N的可变长度字符串，N<=8188.必须设置长度"),
    DM_LONGVARCHAR("DM","LONGVARCHAR","LONGVARCHAR","最大字符长度为2147483647，无需指定长度"),
    DM_VARCHAR2("DM","VARCHAR2", "VARCHAR2(N)", "最大字符串长度为N的可变长度字符串，N<=8188.必须设置长度"),
    DM_CLOB("DM","CLOB", "CLOB", "字符串大对象，可变长度的字符数据，最大长度为2G-1.最大4G"),
    DM_TEXT("DM","TEXT", "TEXT", "文本数据类型，可变长度的字符数据，最大长度为2G-1.不用设置长度"),

    //DM字符串類型
    POSTGRESQL_CHAR("POSTGRESQL","CHAR", "CHAR(N)", "定长, 不足补空白"),
    POSTGRESQL_CHARACTER("POSTGRESQL","CHARACTER", "CHARACTER(N)", "定长, 不足补空白"),
    POSTGRESQL_VARCHAR("POSTGRESQL","VARCHAR", "VARCHAR(N)", "变长，最大长度有限制"),
    POSTGRESQL_CHARACTER_VERYING("POSTGRESQL","CHARACTER VERYING","CHARACTER VERYING","变长，最大长度有限制"),
    POSTGRESQL_TEXT("POSTGRESQL","TEXT", "TEXT", "变长，最大长度没有限制");
    /**
     * 数据库类型
     */
    private String dbType;

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


    DataMaskingTypeEnum(String type, String name, String format, String desc){
        this.dbType = type;
        this.name = name;
        this.format = format;
        this.desc = desc;
    }

    public static boolean verifyDataMaskingType(String dbType, String type){

        String typeValue = type;
        if(type.indexOf("(")>0){
            String[] typeArray = type.split("\\(");
            typeValue = typeArray[0];
        }
        for(DataMaskingTypeEnum enumValue:values()){
            if(StringUtils.equalsAnyIgnoreCase(enumValue.getDbType(), dbType)&&
                    StringUtils.equalsAnyIgnoreCase(enumValue.getName(), typeValue)){
                return true;
            }
        }
        return  false;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
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
