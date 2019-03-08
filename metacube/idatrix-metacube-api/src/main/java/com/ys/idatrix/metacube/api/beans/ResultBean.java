package com.ys.idatrix.metacube.api.beans;

import java.io.Serializable;
import lombok.Data;

@Data
public class ResultBean<T> implements Serializable {

    private static final transient String DEFAULT_SUCCESS_CODE = "200";
    private static final transient String DEFAULT_SUCCESS_MSG = "success";
    private static final transient String DEFAULT_ERROR_CODE = "500";

    private String code;

    private String msg;

    private T data;

    public static ResultBean ok() {
        return ok(DEFAULT_SUCCESS_MSG, null);
    }

    public static <T> ResultBean<T> ok(T data) {
        return ok(DEFAULT_SUCCESS_MSG, data);
    }

    public static ResultBean ok(String msg) {
        return ok(msg, null);
    }

    public static <T> ResultBean<T> ok(String msg, T data) {
        ResultBean<T> resultBean = new ResultBean();
        resultBean.setCode(DEFAULT_SUCCESS_CODE);
        resultBean.setMsg(msg);
        resultBean.setData(data);
        return resultBean;
    }

    public static ResultBean error(String msg) {
        return error(DEFAULT_ERROR_CODE, msg);
    }

    public static ResultBean error(String code, String msg) {
        ResultBean resultBean = new ResultBean();
        resultBean.setCode(code);
        resultBean.setMsg(msg);
        return resultBean;
    }

    public boolean isSuccess() {
        return DEFAULT_SUCCESS_CODE.equals(code);
    }

}
