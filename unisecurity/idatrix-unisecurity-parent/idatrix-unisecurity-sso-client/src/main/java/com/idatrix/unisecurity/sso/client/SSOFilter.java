package com.idatrix.unisecurity.sso.client;

import com.idatrix.unisecurity.sso.client.enums.ResultEnum;
import com.idatrix.unisecurity.sso.client.model.SSOUser;
import com.idatrix.unisecurity.sso.client.utils.CookieUtil;
import com.idatrix.unisecurity.sso.client.utils.GsonUtil;
import com.idatrix.unisecurity.sso.client.utils.ResultVoUtils;
import com.idatrix.unisecurity.sso.client.vo.ResultVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.List;

/**
 * 客户端：
 * 登录状态验证拦截器
 */
public class SSOFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private String excludes; //不需要拦截的URI模式，以正则表达式表示

    private String serverBaseUrl; // 服务端公网访问地址

    private String serverInnerAddress; // 服务端系统间通信用内网地址

    private String projectName; // 服务端项目名称

    // private boolean notLoginOnFail;  当授权失败时是否让浏览器跳转到服务端登录页

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.debug("==============SSOFilter：init==============");
        excludes = filterConfig.getInitParameter("excludes");
        serverBaseUrl = filterConfig.getInitParameter("serverBaseUrl");
        projectName = filterConfig.getInitParameter("projectName");
        serverInnerAddress = filterConfig.getInitParameter("serverInnerAddress");
        if (StringUtils.isBlank(serverBaseUrl) && StringUtils.isBlank(serverInnerAddress)) {
            throw new ServletException("SSOFilter配置错误，必须设置serverBaseUrl和serverInnerAddress其中的一个");
        }
        serverBaseUrl = StringUtils.isBlank(serverBaseUrl) == false ? serverBaseUrl : serverInnerAddress;
        serverInnerAddress = StringUtils.isBlank(serverInnerAddress) == false ? serverInnerAddress : serverBaseUrl;
        TokenManager.serverIndderAddress = serverInnerAddress;
        TokenManager.projectName = projectName;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException,
            ServletException {
        logger.debug("====================== sso filter 判断当前用户是否通过登录认证 ======================");
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;
        // 如果是不需要拦截的请求，直接放行
        if (requestIsExclude(request) || request.getRequestURI().indexOf("/noticeTimout") != -1) {
            chain.doFilter(request, response);
            return;
        }

        /**
         * 1.获取到Token，校验有效性，先本地校验有效性，如果校验失败远程校验有效性。
         * 2.根据校验的结果来决定是否需要拦截当前请求
         */
        String vt = CookieUtil.getVT(request); // 获取Token
        String lt = CookieUtil.getLT(request); // 记住我标识
        logger.debug("客户端中的VT：{}, LT：{}", vt, lt);
        logger.debug("当请求的路径url：{}", request.getContextPath() + "/" + request.getRequestURI());

        if (StringUtils.isNotEmpty(vt)) { // 令牌不为空时
            SSOUser user = null;
            ResultVo result = null;
            try {
                // 校验令牌
                result = TokenManager.validate(vt, "aaaa");
                if (result.getCode().equals("200")) {
                    // 校验成功
                    user = (SSOUser) result.getData();
                }
            } catch (Exception e) {
                logger.debug("SSOFilter error :" + e.getMessage());
                e.printStackTrace();
                result = ResultVoUtils.error(500, e.getMessage());
            }
            if (user != null) {
                logger.debug("SSOFilter holder user token：" + vt);
                // 同步业务缓存中
                holdUser(user, vt, request);
                chain.doFilter(request, response);
            } else {
                logger.debug("无效的token：{}", vt);
                // 移除缓存
                TokenManager.invalidate(vt);
                // 返回认证失败的信息
                out(response, result);
                return;
            }
        } else {
            // = TokenManager.validateLT(lt);
            // 当前令牌是不存在的，那么等于没有通过认证
            out(response , ResultVoUtils.error(ResultEnum.USER_NOT_LOGIN.getCode(), ResultEnum.USER_NOT_LOGIN.getMessage()));
        }
    }

    public void out(HttpServletResponse response, ResultVo result) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().append(GsonUtil.toJson(result));
    }

    private void returnTimoutMsg(HttpServletResponse response, int status, String message) throws IOException {
        /*String qstr = makeQueryString(request); // 将所有请求参数重新拼接成queryString
           *//**//* System.out.println("查询字符串:"+qstr);*//**//*
        String backUrl = request.getRequestURL() + qstr;
        String address = "";
        if (projectName != null && projectName.length() > 1)
            address = serverBaseUrl + projectName + "/u/login?backUrl=";
        else
            address = serverBaseUrl + "/u/login?backUrl=";
        String location = address + URLEncoder.encode(backUrl, "utf-8");
        // 普通类型请求
        if (notLoginOnFail) {
            location += "&notLogin=true";
        }
        response.sendRedirect(location);*/
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().append(GsonUtil.toJson(ResultVoUtils.error(status, message)));
    }

    private SSOUser getUserByCookies(List<Cookie> list, StringBuilder vt) {
        for (Cookie cookie : list) {
            String value = cookie.getValue();
            if (value != null) {
                String[] subValues = value.split("-");
                if (subValues != null && subValues.length == 2) {
                    try {
                        SSOUser user = TokenManager.validate(subValues[1]);
                        if (user != null && subValues[0].equals(user.getId())) {
                            vt.append(subValues[1]);
                            return user;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                }
            }
        }
        return null;
    }

    // 从参数中获取服务端传来的vt后，执行一个到本链接的重定向，将vt写入cookie
    // 重定向后再发来的请求就存在有效vt参数了
    /*private void redirectToSelf(String vt, HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String PARANAME = "__vt_param__="; 
        // 此处拼接redirect的url，去除vt参数部分
        StringBuffer location = request.getRequestURL();
        //获取request中的所有参数
        String qstr = request.getQueryString();
        int index = qstr.indexOf(PARANAME);

        if (index > 0) { // 还有其它参数，para1=param1&param2=param2&__vt_param__=xxx是最后一个参数
            qstr = "?" + qstr.substring(0, qstr.indexOf(PARANAME) - 1);
        } else { // 没有其它参数 qstr = __vt_param__=xxx
            qstr = "";
        }
        location.append(qstr);
        Cookie cookie = new Cookie("VT", vt);
        cookie.setPath(request.getContextPath());
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        response.sendRedirect(location.toString());
    }*/

    // 从请求参数中解析vt
    /*private String pasreVtParam(HttpServletRequest request) {
        final String PARANAME = "__vt_param__=";
        String qstr = request.getQueryString();
        if (qstr == null) {
            return null;
        }
        int index = qstr.indexOf(PARANAME);
        if (index > -1) {
            return qstr.substring(index + PARANAME.length());
        } else {
            return null;
        }
    }*/

    // 引导浏览器重定向到服务端执行登录校验
    /*private void loginCheck(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // ajax类型请求涉及跨域问题
        // CORS方案解决跨域操作时，无法携带Cookie，所以无法完成验证，此处不适合
        // jsonp方案可以处理Cookie问题，但jsonp方式对后端代码有影响，能实现但复杂不理想，大家可以课后练习实现
        // 所以ajax请求前建议先让业务系统获取到vt，这样发起ajax请求时就不会执行跳转验证操作，避免跨域操作产生
        if ("XMLHttpRequest".equals(request.getHeader("x-requested-with"))) {
            // 400 状态表示请求格式错误，服务器没有理解请求，此处返回400状态表示未登录时服务器拒绝此ajax请求0
            response.sendError(400);
        } else {
            // redirect只能是get请求，所以如果当前是post请求，会将post过来的请求参数变成url querystring，即get形式参数
            // 这种情况，此处实现就会有一个局限性 —— 请求参数长度的限制，因为浏览器对get请求的长度都会有所限制。
            // 如果post过来的内容过大，就会造成请求参数丢失
            // 解决这个问题，只能是让用户系统去避免这种情况发生.
            // 可以在发送这类请求前任意时间点发起一次任意get类型请求，这个get请求通过loginCheck
            // 的引导从服务端获取到vt，当再发起post请求时，vt已存在并有效，就不会进入到这个过程，从而避免了问题出现
            // http://www.sys1.com:8081/test/tt?a=2&b=xxx&__vt_param__=
          
            String qstr = makeQueryString(request); // 将所有请求参数重新拼接成queryString
           *//* System.out.println("查询字符串:"+qstr);*//*
            String backUrl = request.getRequestURL() + qstr;
            String address = "";
            if (projectName != null && projectName.length() > 1)
                address = serverBaseUrl + projectName + "/u/login?backUrl=";
            else
                address = serverBaseUrl + "/u/login?backUrl=";
            String location = address + URLEncoder.encode(backUrl, "utf-8");
            // 普通类型请求
            if (notLoginOnFail) {
                location += "&notLogin=true";
            }
            response.sendRedirect(location);
        }
    }*/

    // 将所有请求参数重新拼接成queryString
    private String makeQueryString(HttpServletRequest request) throws UnsupportedEncodingException {
        StringBuilder builder = new StringBuilder();
        Enumeration<String> paraNames = request.getParameterNames();
        while (paraNames.hasMoreElements()) {
            String paraName = paraNames.nextElement();
            String[] paraVals = request.getParameterValues(paraName);
            for (String paraVal : paraVals) {
                builder.append("&").append(paraName).append("=").append(URLEncoder.encode(paraVal, "utf-8"));
            }
        }
        if (builder.length() > 0) {
            builder.replace(0, 1, "?");
        }
        return builder.toString();
    }

    // 将user存入threadLocal 和 request，供业务系统使用
    private void holdUser(SSOUser user, String vt, HttpServletRequest request) {
        UserHolder.set(user, vt, request);
    }

    // 判断请求是否不需要拦截
    private boolean requestIsExclude(ServletRequest request) {
        // 没有设定excludes时，所以经过filter的请求都需要被处理
        if (StringUtils.isEmpty(excludes)) {
            return false;
        }

        // 获取去除context path后的请求路径
        String contextPath = request.getServletContext().getContextPath();
        String uri = ((HttpServletRequest) request).getRequestURI();
        uri = uri.substring(contextPath.length());

        // 正则模式匹配的uri被排除，不需要拦截
        boolean isExcluded = uri.matches(excludes);

        if (isExcluded) {
            logger.debug("request path: {} is excluded!" + uri);
        }
        return isExcluded;
    }

    @Override
    public void destroy() {

    }
}
