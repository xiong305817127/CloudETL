package com.idatrix.resource.exchange.exception;

/**
 * Created by Administrator on 2018/11/7.
 */
public class DeptCodeException extends Exception {

    private String errorCode = "6002001";

    private String message ;

    public DeptCodeException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public DeptCodeException(String message) {
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

