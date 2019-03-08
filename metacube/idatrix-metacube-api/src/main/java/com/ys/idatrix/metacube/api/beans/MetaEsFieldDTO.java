package com.ys.idatrix.metacube.api.beans;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName: MetaEsFieldDTO
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/2/27
 */
@Data
public class MetaEsFieldDTO implements Serializable {

    private static final long serialVersionUID = -9027243216429250142L;

    /**
     * 索引类型名称
     */
    private String typeName;

    /**
     * 字段名称
     */
    private String fieldName;

    /**
     * 字段类型
     */
    private String fieldType;

}
