package com.idatrix.unisecurity.server;

/**
 * 提供系统内session out ，通知前端timeout

@WebServlet("/sessionTimeOut")
public class SessionTimeOut extends HttpServlet {
	
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,
IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.setStatus(7001);
    }

} */
