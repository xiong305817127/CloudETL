package com.idatrix.unisecurity.common.exception;

/**
 * @ClassName SecurityException
 * @Description 安全专用异常
 * @Author ouyang
 * @Date 2018/11/27 10:25
 * @Version 1.0
 */
public class SecurityException extends RuntimeException {

    private Integer code;

    private String message;

    public SecurityException() {

    }

    public SecurityException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}
