package com.idatrix.unisecurity.core.shiro.filter;

import com.idatrix.unisecurity.common.enums.ResultEnum;
import com.idatrix.unisecurity.common.utils.GsonUtil;
import com.idatrix.unisecurity.common.utils.ResultVoUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * 角色过滤器
 * 开发公司：粤数大数据
 */
public class RoleFilter extends AccessControlFilter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        // 是否允许访问，自己定义规则
        logger.debug("shiro：进入自定义的 role 认证 filter");
        String[] arra = (String[]) mappedValue;
        Subject subject = SecurityUtils.getSubject();
        for (String role : arra) {
            if (subject.hasRole("role：" + role)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        logger.info("shiro filter 当前用户没有角色，返回json格式数据");
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json");
        httpServletResponse.getWriter().write(GsonUtil.toJson(ResultVoUtils.error(ResultEnum.NOT_PERMISSIONS.getCode(), ResultEnum.NOT_PERMISSIONS.getMessage())));
        return false;
    }
}
