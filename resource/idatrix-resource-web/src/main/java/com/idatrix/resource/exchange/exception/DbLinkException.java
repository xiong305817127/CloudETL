package com.idatrix.resource.exchange.exception;

/**
 * 数据库连接异常
 */
public class DbLinkException extends Exception {

    private Integer errorCode;

    private String message;

    public DbLinkException(Integer errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public DbLinkException(String message) {
        super(message);
        this.message = message;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

