package com.idatrix.unisecurity.server.token;

import com.idatrix.unisecurity.common.domain.UUser;
import com.idatrix.unisecurity.common.enums.ResultEnum;
import com.idatrix.unisecurity.common.utils.GsonUtil;
import com.idatrix.unisecurity.common.utils.ResultVoUtils;
import com.idatrix.unisecurity.common.utils.SpringContextUtil;
import com.idatrix.unisecurity.common.vo.ResultVo;
import com.idatrix.unisecurity.user.Config;
import com.idatrix.unisecurity.user.service.UserSerializer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 服务端
 * 客户端验证令牌的servlet
 */
@WebServlet("/validate_service")
public class TokenValidateServlet extends HttpServlet {

    private Logger log = LoggerFactory.getLogger(getClass());
	
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 接收客户端传来的Token
        String vt = request.getParameter("vt");
        log.debug("server 验证令牌 客户端传来的 Token：{}", vt);

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        if (StringUtils.isEmpty(vt)) {
            // 如果令牌为空，直接返回用户没有登录
            response.getWriter().write(GsonUtil.toJson(ResultVoUtils.error(ResultEnum.USER_NOT_LOGIN.getCode(), ResultEnum.USER_NOT_LOGIN.getMessage())));
        }
        // 开始校验有效性
        ResultVo result = TokenManager.validate(vt, "aa");
        try {
            if(result.getCode().equals("200")) {// 令牌校验成功
                Config config = SpringContextUtil.getBean(Config.class);// 获取序-列化对象
                UserSerializer userSerializer = config.getUserSerializer();
                UUser user = (UUser) result.getData();
                UserSerializer.UserData userData = userSerializer.serial(user, "aa");// 序列化 user
                log.debug("server 校验令牌成功，result：{}", result);
                response.getWriter().write(GsonUtil.toJson(ResultVoUtils.ok("success", GsonUtil.toJson(userData)))); // 返回数据给客户端
            } else {// 令牌校验失败
                log.debug("server 校验令牌失败，result：{}", result);
                response.getWriter().write(GsonUtil.toJson(result));
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
