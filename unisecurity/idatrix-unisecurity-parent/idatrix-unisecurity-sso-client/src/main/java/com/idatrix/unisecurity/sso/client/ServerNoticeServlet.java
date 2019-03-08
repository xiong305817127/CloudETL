package com.idatrix.unisecurity.sso.client;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * 客户端：
 * 接收服务端发送的通知，主要是与服务端进行通讯
 */
@WebServlet("/notice/*")
public class ServerNoticeServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static Logger logger = LoggerFactory.getLogger(ServerNoticeServlet.class);

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // notice后路径为notice类型，如/notice/timeout，则当前通知为timeout类型
        String uri = request.getRequestURI();
        String cmd = uri.substring(uri.lastIndexOf("/") + 1);
        response.setContentType("text/plain");
        response.setCharacterEncoding("utf-8");

        // 根据当前的uri来判断当前是要做什么动作
        switch (cmd) {
            case "timeout": { // 获取当前客户端中某个用户的最后活动时间
                String vt = request.getParameter("vt");
                logger.debug("notice timeout begin vt >>>:" + vt);
                int tokenTimeout = Integer.parseInt(request.getParameter("tokenTimeout"));// 获取服务端发来的token的有效时间
                Date expries = TokenManager.timeout(vt, tokenTimeout);// 拿到当前客户端缓存最后过期时间点
                logger.debug("notice timeout begin expires ：" + expries);
                response.getWriter().write(expries == null ? "" : String.valueOf(expries.getTime()));
                break;
            }
            case "logout": { //用户退出
                String vt = request.getParameter("vt");
                logger.debug("notice logout begin vt >>>:" + vt);
                TokenManager.invalidate(vt);
                response.getWriter().write("true");
                break;
            }
            case "shutdown":{ // 安全系统关闭，正常关闭情况，才能触发，异常关闭时不会触发的。（这样如果安全系统挂了，那么其它系统不会受到影响）
                logger.debug("notice shutdown");
                TokenManager.destroy();
                UserHolder.destory();
                response.getWriter().write("true");
                break;
            }
            default:
                break;
        }
    }

}
