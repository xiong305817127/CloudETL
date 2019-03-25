package com.idatrix.resource.exchange.exception;

/**
 * 请求数据校验异常
 */
public class RequestDataException extends Exception {

    private String errorCode = "6002004";

    private String message ;

    public RequestDataException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public RequestDataException(String message) {
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

