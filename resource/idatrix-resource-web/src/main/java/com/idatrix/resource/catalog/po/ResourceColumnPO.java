package com.idatrix.resource.catalog.po;

import lombok.Data;

import java.util.Date;

/**
 * 资源细项信息表
 * @Author: Wangbin
 * @Date: 2018/5/23
 */
@Data
public class ResourceColumnPO {

    /*主键*/
    private Long id;

    /*引用资源标识符id*/
    private Long resourceId;

    /*列名称*/
    private String colName;

    /*数据类型：字符型 C、数值型 N、货币型 Y、日期型 D、日期时间型 T、
    逻辑型 L、备注型 M、通用型 G、双精度型 B、整型 I、浮点型 F*/
    private String colType;

    /*细项顺序码：001-999*/
    private String colSeqNum;

    /*绑定数据库表的列名*/
    private String tableColCode;

    /*绑定数据库表的列类型*/
    private String tableColType;

    /*交换时是否必选：0否，1是*/
    private Boolean requiredFlag;

    /*是否主键: 0否，1是*/
    private Boolean uniqueFlag;

    /*日期类型格式*/
    private String dateFormat;

    private String creator;

    private Date createTime;

    private String modifier;

    private Date modifyTime;

}
