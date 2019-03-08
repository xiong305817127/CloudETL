package com.ys.idatrix.metacube.common.utils;

import lombok.Data;

/**
 * @ClassName HeadEntity
 * @Description
 * @Author ouyang
 * @Date
 */
@Data
public class HeadEntity {

    /**
     * 列名顺序
     * 从 0 开始
     */
    private int idx;

    /**
     * 列名
     */
    private String name;

    /**
     * 是否必输
     */
    private boolean hasRequired;

    /**
     * 是否数字
     */
    private boolean hasNumeric;

    public HeadEntity() {

    }

    public HeadEntity(int idx, String name, boolean hasRequired) {
        this.idx = idx;
        this.name = name;
        this.hasRequired = hasRequired;
    }

    public HeadEntity(int idx, String name, boolean hasRequired, boolean hasNumeric) {
        this.idx = idx;
        this.name = name;
        this.hasRequired = hasRequired;
        this.hasNumeric = hasNumeric;
    }
}