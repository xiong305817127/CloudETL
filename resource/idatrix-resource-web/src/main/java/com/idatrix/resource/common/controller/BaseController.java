package com.idatrix.resource.common.controller;


import com.alibaba.fastjson.JSONException;
import com.idatrix.resource.common.utils.Result;
import com.idatrix.unisecurity.sso.client.UserHolder;
import org.apache.ibatis.binding.BindingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
//import com.idatrix.unisecurity.sso.client.UserHolder;


/**
 * @ClassName: BaseController
 * @Description: controller 调用异常 基于@ExceptionHandler异常处理
 * @Author: ZhouJian
 * @Date: 2017/6/5
 */
public class BaseController {

	private static final Logger LOG = LoggerFactory.getLogger(BaseController.class);
	/**
	 * 获取当前用户名： window调试环境下直接使用 "admin"其它环境通过系统变量获取。
	 *  
	 * @param
	 * @return
	 */
	public String getUserName(){
		String userName = (String) UserHolder.getUser().getProperty("username");
        return userName;
//        return UserUtils.getCurrentSaveUserInfo();
//		LOG.info("getUserName:{}", userName);

		
		//没有对接sso
	/*	String userName = null;
		String os = System.getProperty("os.name").toLowerCase();
		if(os.indexOf("windows")>=0){
			userName = "admin";
		}else {
			userName = (String) UserHolder.getUser().getProperty("username");
		}
		return userName;*/
	}
	
    /**
     * controller 异常处理
     *
     * @param request
     * @param ex
     * @return
     */
    @ExceptionHandler
    @ResponseBody
    public Result doException(HttpServletRequest request, Exception ex) {
        request.setAttribute("ex", ex);
        if (ex instanceof DuplicateKeyException) {//db记录重复异常
            return Result.error(6001000, "db record duplicate");
        } else if (ex instanceof BindingException) {//mybatis配置文件参数绑定异常
            return Result.error(6001001, "mybatis config file error");
        } else if (ex instanceof HttpMessageNotReadableException) {//Json 转换异常（输入）
            return Result.error(6001002, "json convert failure");
        } else if (ex instanceof IllegalArgumentException) {//参数不合法异常
            return Result.error(6001003, "argument illegal");
        } else if (ex instanceof JSONException) {//json操作异常（输出）
            return Result.error(6001004, "json operati on failure");
        } else if (ex instanceof MethodArgumentNotValidException) {//方法参数无效异常
            BindingResult bindingResult = ((MethodArgumentNotValidException) ex).getBindingResult();
            String errorMsg = "Invalid Request:";
            for (FieldError fieldError : bindingResult.getFieldErrors()) {
                errorMsg += fieldError.getDefaultMessage() + ", ";
            }
            return Result.error(6001005, errorMsg);
        } else {//非常见异常
            return Result.error(700, "System Error");
        }

    }

}
