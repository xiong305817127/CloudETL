package com.idatrix.unisecurity.sso.client;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 用于服务端登录后跨域写Cookie
 */
@WebServlet("/cookie_set")
@SuppressWarnings("serial")
public class CookieSetServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String vt = req.getParameter("vt");
        if (StringUtils.isNotBlank(vt)) {
            resp.addHeader("P3P", "CP=CURa ADMa DEVa PSAo PSDo OUR BUS UNI PUR INT DEM STA PRE COM NAV OTC NOI DSP COR");
            Cookie cookie = new Cookie("VT", vt);
            cookie.setPath(req.getContextPath());
            cookie.setHttpOnly(true);
            resp.addCookie(cookie);
        }
    }

}
