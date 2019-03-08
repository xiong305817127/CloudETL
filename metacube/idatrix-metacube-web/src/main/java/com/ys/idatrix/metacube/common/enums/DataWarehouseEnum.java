package com.ys.idatrix.metacube.common.enums;

/**
 * 数据仓库枚举
 *
 * @author wzl
 */
public enum DataWarehouseEnum {
    /**
     * ODS 操作性数据存储
     */
    ODS(1, "操作性数据存储"),
    /**
     * DW 数据仓库
     */
    DW(2, "数据仓库"),
    /**
     * DM 数据集市
     */
    DM(3, "数据集市");

    private int code;
    private String name;

    DataWarehouseEnum(int code, String name) {
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
