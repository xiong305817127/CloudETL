package com.ys.idatrix.metacube.common.enums;

/**
 * 模式操作类型枚举
 *
 * @author wzl
 */
public enum SchemaOperationTypeEnum {
    CREATE(1, "新建"), REGISTER(2, "注册");

    private int code;
    private String name;

    SchemaOperationTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
