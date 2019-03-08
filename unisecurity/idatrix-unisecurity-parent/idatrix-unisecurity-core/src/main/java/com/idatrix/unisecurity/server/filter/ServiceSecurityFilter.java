package com.idatrix.unisecurity.server.filter;

import com.idatrix.unisecurity.common.utils.SpringContextUtil;
import com.idatrix.unisecurity.user.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;

/**
 * 服务端，安全过滤器
 * 系统间内网通信安全拦截器，此处执行安全认证
 */
@WebFilter({ "/validate_service", "/other_request" })
public class ServiceSecurityFilter implements Filter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Boolean securityValidate = false;

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        logger.debug("service security filter已执行！");

        if(securityValidate) {
            // 此处执行安全认证，常用认证方式
            // 1. 从request的paramter,header中获取安全认证凭证，按凭证验证
            //    request.getParameter("credential_name");
            //   ((HttpServletRequest) request).getHeader("credential_name");
            // 2. 根据已配置的客户端列表得到客户端IP列表，仅限列表内IP访问
            Config config = SpringContextUtil.getBean(Config.class);
            config.getClientSystems(); // 得到clientList
            request.getRemoteAddr(); // 得到当前客户端的ip地址
        }
        chain.doFilter(request, response);
    }

    public void init(FilterConfig fConfig) throws ServletException {

    }

    public void destroy() {

    }
}
