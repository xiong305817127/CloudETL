package com.ys.idatrix.quality.enums;

/**
 * @ClassName AnalysisEnum
 * @Description TODO
 * @Author ouyang
 * @Date 2018/10/15 16:48
 * @Version 1.0
 */
public enum  AnalysisEnum {

    NOT_ACTIVE(0, "当前字典是不生效的"), ACTIVE(1, "当前字典是生效的"), TO_BE_UPDATED(2, "字典待更新")
    ;

    private Integer code;

    private String message;

    AnalysisEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
