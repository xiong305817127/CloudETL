package com.idatrix.resource.servicelog.vo;

import java.util.Date;

public class ServiceLogDetailVO {
    /*服务日志关联ID*/
    private Long parentId;

    /*输入参数JSON*/
    private String input;

    /*输出结果JSON*/
    private String output;

    /*错误信息:输入参数校验失败，无调用权限，调用失败*/
    private String errorMessage;

    /*错误堆栈*/
    private String errorStack;

    /*执行时长*/
    private Integer execTime;

    /*调用时间*/
    private String callTime;

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorStack() {
        return errorStack;
    }

    public void setErrorStack(String errorStack) {
        this.errorStack = errorStack;
    }

    public Integer getExecTime() {
        return execTime;
    }

    public void setExecTime(Integer execTime) {
        this.execTime = execTime;
    }

    public String getCallTime() {
        return callTime;
    }

    public void setCallTime(String callTime) {
        this.callTime = callTime;
    }

    @Override
    public String toString() {
        return "ServiceLogDetailVO{" +
                "parentId=" + parentId +
                ", input='" + input + '\'' +
                ", output='" + output + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                ", errorStack='" + errorStack + '\'' +
                ", execTime=" + execTime +
                ", callTime='" + callTime + '\'' +
                '}';
    }
}
