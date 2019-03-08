package com.idatrix.resource.datareport.dto;

import java.io.Serializable;

public class StatusFeedbackDto implements Serializable {


    private static final long serialVersionUID = -2665913790147008273L;

    public Integer statusCode;

    public String errMsg;

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    @Override
    public String toString() {
        return "StatusFeedbackDto{" +
                "statusCode=" + statusCode +
                ", errMsg='" + errMsg + '\'' +
                '}';
    }
}
