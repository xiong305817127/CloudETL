package com.idatrix.unisecurity.core.shiro.filter;

import com.idatrix.unisecurity.common.utils.LoggerUtils;
import net.sf.json.JSONObject;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * 开发公司：粤数大数据
 */
public class ShiroFilterUtils {

    final static Class<? extends ShiroFilterUtils> CLAZZ = ShiroFilterUtils.class;

    //登录页面
    static final String LOGIN_URL = "/u/login.shtml";

    //踢出登录提示
    final static String KICKED_OUT = "/open/kickedOut.shtml";

    //没有权限提醒
    final static String UNAUTHORIZED = "/open/unauthorized.shtml";

    /**
     * 是否是Ajax请求
     * @param request
     * @return
     */
    public static boolean isAjax(ServletRequest request) {
        return "XMLHttpRequest".equalsIgnoreCase(((HttpServletRequest) request).getHeader("X-Requested-With"));
    }

    /**
     * response 输出JSON
     * @param
     * @param resultMap
     * @throws IOException
     */
    public static void out(ServletResponse response, Map<String, String> resultMap) {
        PrintWriter out = null;
        try {
            response.setCharacterEncoding("UTF-8");
            out = response.getWriter();
            out.println(JSONObject.fromObject(resultMap).toString());
        } catch (Exception e) {
            LoggerUtils.fmtError(CLAZZ, e, "输出JSON报错。");
        } finally {
            if (null != out) {
                out.flush();
                out.close();
            }
        }
    }
}
