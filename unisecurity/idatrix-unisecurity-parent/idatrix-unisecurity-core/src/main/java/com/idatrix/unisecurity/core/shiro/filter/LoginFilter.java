package com.idatrix.unisecurity.core.shiro.filter;

import com.idatrix.unisecurity.common.domain.UUser;
import com.idatrix.unisecurity.common.enums.ResultEnum;
import com.idatrix.unisecurity.common.utils.GsonUtil;
import com.idatrix.unisecurity.common.utils.ResultVoUtils;
import com.idatrix.unisecurity.core.shiro.token.manager.ShiroTokenManager;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

/**
 * 自定义认证过滤器
 * 登录了或者记住我都属于已认证
 */
public class LoginFilter extends AccessControlFilter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        // 判断当前是否为 已认证 或 记住我状态
        UUser token = ShiroTokenManager.getToken();
        Subject subject = SecurityUtils.getSubject();
        if(token != null && (subject.isAuthenticated() || subject.isRemembered())) {
            return true;
        }
        return false;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        logger.debug("shiro filter 当前用户没有通过认证，返回json格式数据");
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json");
        httpServletResponse.getWriter().write(GsonUtil.toJson(ResultVoUtils.error(ResultEnum.USER_NOT_LOGIN.getCode(), ResultEnum.USER_NOT_LOGIN.getMessage())));
        return false;
    }

}
