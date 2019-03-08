package com.ys.idatrix.metacube.common.enums;

/**
 * 元数据当前任务状态
 */
public enum DataStatusEnum {


    DRAFT(0, "草稿", "draft"),
    VALID(1, "生效", "valid"),
    DELETE(2, "删除", "delete");

    DataStatusEnum(int value, String zh, String status){
        this.value = value;
        this.statusZH = zh;
        this.status = status;
    }

    private int value;

    private String statusZH;

    private String status;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getStatusZH() {
        return statusZH;
    }

    public void setStatusZH(String statusZH) {
        this.statusZH = statusZH;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
