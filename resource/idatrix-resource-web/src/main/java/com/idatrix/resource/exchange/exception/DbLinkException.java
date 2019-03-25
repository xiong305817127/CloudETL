package com.idatrix.resource.exchange.exception;

/**
 * 数据库连接异常
 */
public class DbLinkException extends Exception {

    private String errorCode = "6002000";

    private String message;

    public DbLinkException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public DbLinkException(String message) {
        super(message);
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
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

