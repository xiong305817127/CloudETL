package com.ys.idatrix.metacube.common.enums;

import lombok.Getter;

/**
 * @ClassName ResultCodeEnum
 * @Description
 * @Author ouyang
 * @Date
 */
@Getter
public enum  ResultCodeEnum {

    DATABASE_ERROR("40001", "数据库异常"),

    ;

    private String code;

    private String message;

    ResultCodeEnum(String code, String message) {
        this.code = code;
        this.message = message;
    }
}