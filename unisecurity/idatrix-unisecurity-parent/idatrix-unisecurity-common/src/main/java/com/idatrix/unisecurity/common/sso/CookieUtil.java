package com.idatrix.unisecurity.common.sso;


import com.idatrix.unisecurity.common.utils.CookieUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 操作cookie
 */
public class CookieUtil {

    private CookieUtil() {
    }

    /**
     * 查找特定cookie值
     *
     * @param cookieName
     * @param request
     * @return
     */
    public static String getCookie(String cookieName, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(cookieName)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }


    public static List<Cookie> getCookies(String cookieName, HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        List<Cookie> list = new ArrayList<Cookie>();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().startsWith(cookieName)) {
                    list.add(cookie);
                }
            }
        }
        return list;
    }

    /**
     * 删除cookie
     *
     * @param response
     */
    public static void deleteCookie(String cookieName, HttpServletResponse response, String path) {
//        Cookie cookie = new Cookie(cookieName, null);
//        cookie.setMaxAge(0);
//        if (StringUtils.isEmpty(path)) {
//            cookie.setPath("/");
//        }else{
//            cookie.setPath(path);
//        }
//        response.addCookie(cookie);
    }

    public static String getVT(HttpServletRequest request) {
        // 先从cookie中获取VT，如果没有那么从请求头中获取VT
        String vt = CookieUtils.getCookieValue(request, "VT");
        if (vt == null) {// cookie VT 为空，则从请求头中获取VT
            Enumeration<String> enums = request.getHeaders("VT");
            if (enums.hasMoreElements()) {
                vt = enums.nextElement();
            }
        }
        if (vt == null) {// 如果cookie和请求头中VT都是为空，那么从参数中获取VT
            vt = request.getParameter("VT");
        }
        return vt;
    }
}
