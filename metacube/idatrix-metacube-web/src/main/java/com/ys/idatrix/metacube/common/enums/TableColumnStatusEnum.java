package com.ys.idatrix.metacube.common.enums;

/**
 * Created by Administrator on 2019/1/25.
 */
public enum TableColumnStatusEnum {

    //0：默认值什么也不做 1：新建 2：修改 3：删除
    SAME(0,"不变"),
    CREATE(1, "新增"),
    CHANGE(2, "修改"),
    DELETE(3, "删除");


    private int value;

    private String statusZH;

    TableColumnStatusEnum(int value, String statusZH){
        this.value = value;
        this.statusZH = statusZH;
    }

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
}
