package com.idatrix.resource.exchange.exception;

/**
 * Created by Administrator on 2018/11/7.
 */
public class DeptCodeException extends Exception {

    private Integer errorCode = 6002002;

    private String message ;

    public DeptCodeException(Integer errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public DeptCodeException(String message) {
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

