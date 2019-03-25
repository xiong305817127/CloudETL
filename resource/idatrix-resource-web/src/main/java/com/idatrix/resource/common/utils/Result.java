package com.idatrix.resource.common.utils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class Result<T> {

    @ApiModelProperty(value = "处理信息")
    private String msg;

    @ApiModelProperty(value = "返回编码")
    private String code;

    @ApiModelProperty(value = "数据内容")
    private T data;

    @ApiModelProperty(value = "成功标志")
    private boolean flag;

    public Result(String message, String code, T data, boolean flag) {
        super();
        this.msg = message;
        this.code = code;
        this.data = data;
        this.flag = flag;
    }

    public Result(T data) {
        super();
        this.msg = null;
        this.code = "200";
        this.data = data;
        this.flag = true;
    }

    public static<T> Result ok(T data) {
        return new Result(null, "200", data, true);
    }

    public static Result error(String errorCode, String errorMsg) {
        return new Result(errorMsg, errorCode, null, false);
    }

    public static Result error(String errorMsg) {
        return error("6000000", errorMsg);
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }


    public Result() {
        super();
    }

    @Override
    public String toString() {
        return "Result{" +
                "msg='" + msg + '\'' +
                ", code='" + code + '\'' +
                ", data=" + data +
                ", flag=" + flag +
                '}';
    }
}
