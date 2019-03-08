/**
 * 云化数据集成系统
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.web.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;

import com.ys.idatrix.cloudetl.logger.CloudLogger;

/**
 * To be changed.
 * @author JW
 * @since 05-12-2017
 * 
 */
@Deprecated
public class ResponseUtils {

	public static void success(String message) throws IOException {
		success("系统提示", message);
	}
	
	public static void success(String title, String message) throws IOException {
		response(true, title, message);
	}
	
	public static void fail(String message) throws IOException {
		fail("系统提示", message);
	}
	
	public static void fail(String title, String message) throws IOException {
		response(false, title, message);
	}
	
	public static void response(boolean success, String title, String message) throws IOException {
		Map<String,Object> jsonObject = new LinkedHashMap<String, Object>();
		jsonObject.put("success", success);
		jsonObject.put("title", title);
		jsonObject.put("message", message);
		
		response(jsonObject);
	}
	
	public static void response(Map<String,Object> jsonObject) throws IOException {
		HttpServletResponse response = tl.get();
		response.setContentType("text/html; charset=utf-8");
		response.getWriter().write(toString(jsonObject));
	}
	
	public static void response( ArrayList<Object> jsonArray) throws IOException {
		HttpServletResponse response = tl.get();
		response.setContentType("text/html; charset=utf-8");
		response.getWriter().write(jsonArray.toString());
	}
	
	public static void responseXml(String xml) throws IOException {
		HttpServletResponse response = tl.get();
		response.setContentType("text/xml; charset=utf-8");
		response.getWriter().write(xml);
	}
	
	private static ThreadLocal<HttpServletResponse> tl = new ThreadLocal<HttpServletResponse>();
	
	public static void putResponse(HttpServletResponse response) {
		tl.set(response);
	}
	
	
	public static  String toString(Object obj) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(obj);
		} catch (Exception e) {
			return CloudLogger.getExceptionMessage(e);
		}
	}
	
}
