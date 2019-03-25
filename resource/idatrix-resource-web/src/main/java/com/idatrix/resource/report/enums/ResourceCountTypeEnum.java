package com.idatrix.resource.report.enums;

/**
 * 资源统计类型枚举
 *
 * @author wzl
 */
public enum ResourceCountTypeEnum {

    REGISTER(1, "注册量"),
    SUBSCRIPTION(2, "订阅量"),
    PUBLICATION(3, "发布量"),
    Frequency(4, "使用频率");

    private int code;
    private String name;

    ResourceCountTypeEnum(int code, String name) {
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
