package com.idatrix.unisecurity.core.shiro.filter;

import com.idatrix.unisecurity.common.utils.LoggerUtils;
import com.idatrix.unisecurity.core.shiro.session.CustomSessionManager;
import com.idatrix.unisecurity.core.shiro.session.SessionStatus;
import net.sf.json.JSONObject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * 开发公司：粤数大数据
 */
public class SimpleAuthFilter extends AccessControlFilter {

    @Override
    protected boolean isAccessAllowed(ServletRequest request,
                                      ServletResponse response, Object mappedValue) throws Exception {
        HttpServletRequest httpRequest = ((HttpServletRequest) request);
        String url = httpRequest.getRequestURI();
        if (url.startsWith("/open/")) {
            return Boolean.TRUE;
        }

        Session session = SecurityUtils.getSubject().getSession();
        SessionStatus sessionStatus = (SessionStatus) session.getAttribute(CustomSessionManager.SESSION_STATUS);

        /*SecurityUtils.getSubject().getSession();
        Subject subject = getSubject(request, response);
        Session session = subject.getSession();
        SessionStatus sessionStatus = (SessionStatus) session.getAttribute(CustomSessionManager.SESSION_STATUS);*/

        Map<String, String> resultMap = new HashMap<String, String>();
        if (null != sessionStatus && !sessionStatus.isOnlineStatus()) {
            //判断是不是Ajax请求
            if (ShiroFilterUtils.isAjax(request)) {
                LoggerUtils.debug(getClass(), "当前用户已经被踢出，并且是Ajax请求！");
                resultMap.put("user_status", "300");
                resultMap.put("message", "您已经被踢出，请重新登录！");
                out(response, resultMap);
            }
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        //先退出
        Subject subject = getSubject(request, response);
        subject.logout();
        /**
         * 保存Request，用来保存当前Request，然后登录后可以跳转到当前浏览的页面。
         * 比如：
         * 我要访问一个URL地址，/admin/index.html，这个页面是要登录。然后要跳转到登录页面，但是登录后要跳转回来到/admin/index.html这个地址，怎么办？
         * 传统的解决方法是变成/user/login.shtml?redirectUrl=/admin/index.html。
         * shiro的解决办法不是这样的。需要：<code>WebUtils.getSavedRequest(request);</code>
         * 							 然后：{@link UserLoginController.login(...)}中的<code>String url = WebUtils.getSavedRequest(request).getRequestUrl();</code>
         */
        WebUtils.saveRequest(request);
        // 再重定向
        WebUtils.issueRedirect(request, response, "/open/kickedOut.shtml");
        return false;
    }

    private void out(ServletResponse hresponse, Map<String, String> resultMap)
            throws IOException {
        hresponse.setCharacterEncoding("UTF-8");
        PrintWriter out = hresponse.getWriter();
        out.println(JSONObject.fromObject(resultMap).toString());
        out.flush();
        out.close();
    }
}
