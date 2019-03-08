package com.ys.idatrix.metacube.api.beans;

/**
 * @ClassName ActionTypeEnum
 * @Description
 * @Author ouyang
 * @Date
 */
public enum ActionTypeEnum {

    READ(1, "读") ,
    WRITE(2, "写"),
    ALL(3, "全部"),
    READORWRITE(4, "读或写"),
    NONE(0, "没有"),
    ;

    private int code;
    private String name;

    ActionTypeEnum(int code, String name) {
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