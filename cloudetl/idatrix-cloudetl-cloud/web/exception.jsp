<%@page import="com.ys.idatrix.cloudetl.ext.utils.StringEscapeHelper"%>
<%@page import="com.ys.idatrix.cloudetl.web.utils.ExceptionUtils"%>
<%@page pageEncoding="utf-8" %>
<%
	Exception e = (Exception) request.getAttribute("exception");
	String str = ExceptionUtils.toString(e);
	str = StringEscapeHelper.encode(str);
	response.getWriter().write(str);
	response.getWriter().flush();
	
%>