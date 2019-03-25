package com.ys.idatrix.metacube.metamanage.vo.response;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: FieldBean
 * @Description:TODO(这里用一句话描述这个类的作用)
 * @author: chl
 * @date: 2017年6月7日 下午2:05:14
 */
@Data
public class DbFieldBean implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 字段类型
     */
    private String fieldType;

    /**
     * 字段长度
     */
    private String fieldLength;

    /**
     * 字段精度
     */
    private String fieldPrecision;


    /**
     * 日期类型格式
     */
    private String dateformat;

    /**
     * 是否为空
     */
    private Boolean IsNull;

    /**
     * 是否主键
     */
    private Boolean IsPk;

    /**
     * 字段描述
     */
    private String description;

}
