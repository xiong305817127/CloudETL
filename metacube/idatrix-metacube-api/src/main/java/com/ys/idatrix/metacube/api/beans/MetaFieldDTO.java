package com.ys.idatrix.metacube.api.beans;


import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: MetaFieldDTO
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/1/29
 */
@Data
public class MetaFieldDTO implements Serializable {

    private static final long serialVersionUID = -1956562569865983681L;

    // 列名
    private String columnName;

    // 数据类型
    private String dataType;

    // 长度
    private String length;

    // 精度
    private String typePrecision;

    // 是否为主键
    private Boolean isPk;

    // 字段位置,列表按位置排序
    private int location;

}

