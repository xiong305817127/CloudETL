package com.ys.idatrix.metacube.common.enums;

/**
 * 变更类型枚举
 */
public enum ChangeTypeEnum {
    /**
     * 服务器
     */
    SERVER(1),
    /**
     * 数据库
     */
    DATABASE(2);

    private int code;

    ChangeTypeEnum(int code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
