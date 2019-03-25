package com.idatrix.resource.basedata.exception;

/**
 * 字典删除异常
 */
public class DictionaryDeleteException  extends Exception {

    private Integer errorCode ;

    private String message;

    public DictionaryDeleteException(Integer errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
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
