package com.ys.idatrix.db.api.common;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * 统一响应对象
 *
 * @ClassName: RespResult
 * @Description: 响应结果
 * @author: ZhouJian
 * @date: 2019/3/4
 */
@Getter
@Setter
@ToString
public class RespResult<T> implements Serializable {

    private static final String DEFAULT_SUCCESS_CODE = "200";

    private static final String DEFAULT_ERROR_CODE = "600";

    /**
     * 错误码。正常：200|错误：非200
     */
    private String code;

    /**
     * 错误信息
     */
    private String msg;

    /**
     * 返回内容
     */
    private T data;

    public RespResult() {
    }

    public RespResult(String code) {
        this.code = code;
    }

    public RespResult(String code, T data) {
        this.code = code;
        this.data = data;
    }

    public RespResult(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public RespResult(String code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    /**
     * 执行是否成功
     *
     * @return
     */
    public boolean isSuccess() {
        return StringUtils.equals(getCode(), DEFAULT_SUCCESS_CODE);
    }


    /**
     * 成功 无数据返回
     *
     * @param <T>
     * @return
     */
    public static <T> RespResult<T> buildSuccessWithoutData() {
        return new RespResult(DEFAULT_SUCCESS_CODE);
    }


    /**
     * 成功 并返回数据
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> RespResult<T> buildSuccessWithData(T data) {
        return new RespResult(DEFAULT_SUCCESS_CODE, data);
    }


    /**
     * 成功 并返回信息
     *
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> RespResult<T> buildSuccessWithMsg(String msg) {
        return new RespResult(DEFAULT_SUCCESS_CODE, msg);
    }


    /**
     * 成功 并返回数据和警告信息
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> RespResult<T> buildSuccessWithDataAndMsg(T data, String msg) {
        return new RespResult(DEFAULT_SUCCESS_CODE, data, msg);
    }


    /**
     * 失败-失败信息
     *
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> RespResult<T> buildFailWithMsg(String msg) {
        return new RespResult(DEFAULT_ERROR_CODE, msg);
    }


    /**
     * 失败 - 失败code + 失败信息
     *
     * @param code
     * @param msg
     * @param <T>
     * @return
     */
    public static <T> RespResult<T> buildFailWithCodeMsg(String code, String msg) {
        return new RespResult(code, msg);
    }


    /**
     * 错误 并返回数据和警告信息
     *
     * @param data
     * @param <T>
     * @return
     */
    public static <T> RespResult<T> buildFailWithDataAndMsg(T data, String msg) {
        return new RespResult(DEFAULT_ERROR_CODE, data, msg);
    }

}
