/**
 * 云化数据集成系统
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.web.servlet;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

import com.ys.idatrix.quality.dto.common.ReturnCodeDto;

import net.sf.json.JSONObject;

/**
 * 在servlet标准中，
 * 是不允许同时使用response.getWriter()和response.getOutputStream()的,
 * 所以报java.lang.IllegalStateException: STREAM错误;
 * 在获取response.getWriter()出错时，就改用response.getOutputStream()输出流.
 * @author JW
 * @since 2017年7月5日
 *
 */
public class CustomSimpleMappingExceptionResolver extends SimpleMappingExceptionResolver {
	
	private final Logger logger = LoggerFactory.getLogger(CustomSimpleMappingExceptionResolver.class);
	
	@Override
	protected ModelAndView doResolveException(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception exception) {

		if (handler == null) {
			return null;
		}
		
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		Method method = handlerMethod.getMethod();

		if (method == null) {
			return null;
		}
		
		Class<?> methodReturnType = method.getReturnType();
		ResponseBody responseBody = AnnotationUtils.findAnnotation(method, ResponseBody.class);
		if (methodReturnType.getName().equals("void") || responseBody != null) {
			
			if(StringUtils.isEmpty(exception.getMessage())){
				logger.error("",exception);
			}
			
			/* 使用response返回 */
			response.setStatus(HttpStatus.OK.value()); // 设置状态码
			response.setContentType(MediaType.APPLICATION_JSON_VALUE); // 设置ContentType
			response.setCharacterEncoding("UTF-8"); // 避免乱码
			response.setHeader("Cache-Control", "no-cache, must-revalidate");
			
			ReturnCodeDto returnDto = new ReturnCodeDto();
			returnDto.setRetCode(1);
			returnDto.setMessage(StringUtils.isEmpty(exception.getMessage())?exception.getClass().getSimpleName():exception.getMessage().trim());
			String returnStr= JSONObject.fromObject(returnDto).toString();
			
			PrintWriter writer;
			try {
				writer = response.getWriter();
				writer.write(returnStr);
			    writer.flush();
				writer.close();
			} catch (Exception e) {
				logger.info("IllegalStateException: STREAM");
				try {		
					OutputStream output = response.getOutputStream();
					output.write(returnStr.getBytes("UTF-8"));
					output.flush();
					output.close();					
				} catch (Exception e1) {
					//e1.printStackTrace();
					logger.info("[EofException] it is due to http servlet connection closed while running Response.getWriter()!");
				}
			}
		} else {
			String viewName = super.determineViewName(exception, request);			
			if (viewName != null) {
				// JSP格式返回
				Integer statusCode = super.determineStatusCode(request, viewName);
				if (statusCode != null) {
					super.applyStatusCodeIfPossible(request, response, statusCode);
				}
				return super.getModelAndView(viewName, exception, request);
			}
		}
		
		return new ModelAndView();
	}

}
