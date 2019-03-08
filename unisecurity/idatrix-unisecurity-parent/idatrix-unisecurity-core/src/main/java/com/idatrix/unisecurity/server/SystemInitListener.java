package com.idatrix.unisecurity.server;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class SystemInitListener implements ServletContextListener {

	public void contextDestroyed(ServletContextEvent event) {
	}

	public void contextInitialized(ServletContextEvent event) {
		ServletContext servletContext = event.getServletContext();
		// 设置项目路径
		servletContext.setAttribute("appctx", servletContext.getContextPath());
		// 设置系统名称
		servletContext.setAttribute("sysName", servletContext.getInitParameter("sysName"));
	}
}
