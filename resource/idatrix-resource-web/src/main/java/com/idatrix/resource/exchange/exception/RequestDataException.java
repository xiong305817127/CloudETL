package com.idatrix.resource.exchange.exception;

/**
 * 请求数据校验异常
 */
public class RequestDataException extends Exception {

    private Integer errorCode = 6002001;

    private String message ;

    public RequestDataException(Integer errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }

    public RequestDataException(String message) {
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

