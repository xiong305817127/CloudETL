package com.idatrix.unisecurity.common.utils;

public class HttpCodeUtils {

    public static final int NORMAL_STATUS = 200;  //正常状态

    public static final int SERVER_INNER_ERROR_STATUS = 500; //服务器内部错误

    public static final int LOGIN_ERROR_STATUS = 300;  //用户名或密码错误

    public static final int AUTHORIZE_ERROR_STATUS = 103;  //授权失败

    public static final int ADMIN_PWD_NOT_PERMIT_UPDATE = 107;  //管理员密码不准修改

    public static final int RES_URL_CANNOT_BE_EMPTY = 110;  //资源url不能为空

    public static final int RES_HAVE_EXISTED = 550;  //资源已经存在

    public static final int VALIDATE_PWD_QUESTION_FAILURE = 700;  //资源已经存在

    public static final int EMAIL_NOT_EXISTS=701;//邮箱不存在

    public static final int PARAMS_IS_EMPTY=-1;//参数错误

}
