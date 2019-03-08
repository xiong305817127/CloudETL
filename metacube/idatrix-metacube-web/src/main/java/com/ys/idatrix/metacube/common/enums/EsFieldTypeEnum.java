package com.ys.idatrix.metacube.common.enums;

/**
 * 索引字段类型
 *
 * @ClassName: IdxFieldType
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/1/11
 */
public enum EsFieldTypeEnum {
    //常用
    Text,
    Keyword,
    Integer,
    Long,
    Date,
    Float,
    Double,
    Boolean,
    Ip,

    //不常用（暂不考虑）
    Object,
    Nested,
    Attachment

}
