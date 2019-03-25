package com.idatrix.unisecurity.sso.client.enums;

/**
 * @ClassName ResultEnum
 * @Description 返回值的一些定义
 * @Author ouyang
 * @Date 2018/9/3 17:12
 * @Version 1.0
 **/
public enum ResultEnum {

    URL_NOT_FIND(404, "未找到指定的资源"), HTTP_METHOD_ERROR(405, "请求方式错误"), SERVER_ERROR(500, "服务器异常"),

    UNKNOWN_ERROR(600, "未知错误"), PARAM_ERROR(5001, "参数异常"),

    NOT_PERMISSIONS(401, "无权访问该页面"), NOT_ROLE(10002, "没有对应的角色"),

    USER_NOT_LOGIN(300, "当前用户没有登录！"), USER_LOGIN_OVERDUE(444, "登录过期，请重新登录"), USER_KICKED_OUT(777, "用户已被踢出，请重新登录！！！"),

    CODE_ERROR(10004, "验证码错误"), USERNAME_OR_PASSWORD_ERROR(10005, "账号或密码错误"),

    USER_LOCK(10006, "账户已被锁定"), USER_PROHIBIT_LOGIN(2, "用户禁止登陆"),

    USER_ACCOUNT_EXIST(20001, "用户账号存在"), EMAIL_EXIST(20002, "邮箱存在"), PHONE_EXIST(20003, "手机号存在"),

    ;

    private Integer code;

    private String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
