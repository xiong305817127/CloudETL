package com.idatrix.resource.exchange.exception;

/**
 * Created by Administrator on 2018/11/7.
 */
public class InnerTerminalConfigException extends Exception {

    private Integer errorCode = 6002001;

    private String message ;

    public InnerTerminalConfigException(Integer errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public InnerTerminalConfigException(String message) {
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

