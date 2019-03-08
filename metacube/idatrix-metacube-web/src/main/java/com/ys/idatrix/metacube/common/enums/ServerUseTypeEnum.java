package com.ys.idatrix.metacube.common.enums;

import lombok.Data;

/**
 * 服务器用途枚举
 */
public enum ServerUseTypeEnum {
    PRE_DATABASE(1, "前置库"),
    PLATFORM(2, "平台库"),
    PLATFORM_HADOOP(3, "平台库-Hadoop");

    private int code;
    private String name;

    ServerUseTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
