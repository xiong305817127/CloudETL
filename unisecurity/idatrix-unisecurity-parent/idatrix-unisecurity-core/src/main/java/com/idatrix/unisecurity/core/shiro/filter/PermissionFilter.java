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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 校验权限过滤器
 */
public class PermissionFilter extends AccessControlFilter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected boolean isAccessAllowed(ServletRequest request,
                                      ServletResponse response, Object mappedValue) throws Exception {
        logger.debug("shiro：进入自定义的 permission 认证 filter");
        // 先判断带参数的权限判断
        Subject subject = SecurityUtils.getSubject();
        if (mappedValue != null) {
            String[] arra = (String[]) mappedValue;
            for (String permission : arra) {
                if (subject.isPermitted(permission)) {
                    return true;
                }
            }
        }

        /**
         * 此处是改版后，为了兼容项目不需要部署到root下，也可以正常运行，但是权限没设置目前必须到 root 的URI
         */
        HttpServletRequest httpRequest = ((HttpServletRequest) request);
        String uri = httpRequest.getRequestURI();// 获取URI
        String basePath = httpRequest.getContextPath();// 获取basePath
        if (uri != null && uri.startsWith(basePath)) {
            uri = uri.replace(basePath, "");
        }
        if(uri.equals("/member/exportErrLog.shtml")) {// 下载 用户导出错误信息不需要权限
        	return true;
        }
        if (subject.isPermitted(uri)) {
            return true;
        }
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        logger.info("sso filter 当前用户没有权限，返回json格式数据");
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json");
        httpServletResponse.getWriter().write(GsonUtil.toJson(ResultVoUtils.error(ResultEnum.NOT_PERMISSIONS.getCode(), ResultEnum.NOT_PERMISSIONS.getMessage())));
        return false;
    }
}
