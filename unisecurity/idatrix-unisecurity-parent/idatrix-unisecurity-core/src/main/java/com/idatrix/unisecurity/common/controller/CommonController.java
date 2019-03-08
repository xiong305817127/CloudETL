package com.idatrix.unisecurity.common.controller;


import com.idatrix.unisecurity.common.utils.LoggerUtils;
import com.idatrix.unisecurity.common.utils.ResultVoUtils;
import com.idatrix.unisecurity.common.utils.VerifyCodeUtils;
import com.idatrix.unisecurity.common.utils.vcode.Captcha;
import com.idatrix.unisecurity.common.utils.vcode.GifCaptcha;
import com.idatrix.unisecurity.common.utils.vcode.SpecCaptcha;
import com.idatrix.unisecurity.core.shiro.token.manager.ShiroTokenManager;
import com.idatrix.unisecurity.permission.service.RoleService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Map;

@ApiIgnore
@RestController
@RequestMapping("/open")
public class CommonController {

    @Resource
    RoleService roleService;

    @RequestMapping("/refreshDB")
    @ResponseBody
    public Map<String, Object> refreshDB() {
        Map resultMap = ResultVoUtils.resultMap();
        roleService.initData();
        resultMap.put("status", 200);
        return resultMap;
    }

    /**
     * 404错误
     * @param request
     * @return
     */
    @RequestMapping("404")
    @ResponseBody
    public Map<String, Object> _404(HttpServletRequest request) {
        Map resultMap = ResultVoUtils.resultMap();
        resultMap.put("status", HttpServletResponse.SC_NOT_FOUND);
        resultMap.put("massage", "找不到页面");
        return resultMap;
    }

    @RequestMapping("/700")
    @ResponseBody
    public Map<String, Object> _700() {
        Map resultMap = ResultVoUtils.resultMap();
        resultMap.put("status", "7001");
        resultMap.put("massage", "禁止访问");
        return resultMap;
    }
    
    @RequestMapping("/403")
    @ResponseBody
    public Map<String, Object> _403() {
        Map resultMap = ResultVoUtils.resultMap();
        resultMap.put("status", "403");
        resultMap.put("massage", "禁止访问");
        return resultMap;
    }

    @RequestMapping("/500")
    @ResponseBody
    public Map<String, Object> _500(HttpServletRequest request) {
        Map resultMap = ResultVoUtils.resultMap();
        Throwable t = (Throwable) request.getAttribute("javax.servlet.error.exception");
        String defaultMessage = "未知";
        if (null == t) {
            resultMap.put("line", defaultMessage);
            resultMap.put("clazz", defaultMessage);
            resultMap.put("methodName", defaultMessage);
            return resultMap;
        }else {
            String message = t.getMessage();//错误信息
            StackTraceElement[] stack = t.getStackTrace();
            resultMap.put("message", message);
            if (null != stack && stack.length != 0) {
                StackTraceElement element = stack[0];
                int line = element.getLineNumber();//错误行号
                String clazz = element.getClassName();//错误java类
                String fileName = element.getFileName();

                String methodName = element.getMethodName();//错误方法
                resultMap.put("line", line);
                resultMap.put("clazz", clazz);
                resultMap.put("methodName", methodName);
                LoggerUtils.fmtError(getClass(), "line:%s,clazz:%s,fileName:%s,methodName:%s()",
                        line, clazz, fileName, methodName);
            }
        }
        resultMap.put("status", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return resultMap;
    }

    /**
     * 获取验证码
     * @param response
     */
    @RequestMapping(value = "/getVCode", method = RequestMethod.GET)
    public void getVCode(HttpServletResponse response, HttpServletRequest request) {
        try {
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType("image/jpg");

            String verifyCode = VerifyCodeUtils.generateVerifyCode(4);

            ShiroTokenManager.setVal2Session(VerifyCodeUtils.V_CODE, verifyCode.toLowerCase());

            int w = 146, h = 33;
            VerifyCodeUtils.outputImage(w, h, response.getOutputStream(), verifyCode);
        } catch (Exception e) {
            LoggerUtils.fmtError(getClass(), e, "获取验证码异常：%s", e.getMessage());
        }
    }

    /**
     * 获取验证码（Gif版本）
     * @param response
     */
    @RequestMapping(value = "/getGifCode", method = RequestMethod.GET)
    public void getGifCode(HttpServletResponse response, HttpServletRequest request) {
        try {
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType("image/gif");
            /**
             * gif格式动画验证码
             * 宽，高，位数。
             */
            Captcha captcha = new GifCaptcha(146, 42, 4);
            //输出
            ServletOutputStream out = response.getOutputStream();
            captcha.out(out);
            out.flush();

            ShiroTokenManager.setVal2Session(VerifyCodeUtils.V_CODE, captcha.text().toLowerCase());
        } catch (Exception e) {
            LoggerUtils.fmtError(getClass(), e, "获取验证码异常：%s", e.getMessage());
        }
    }

    /**
     * 获取验证码（jpg版本）
     */
    @RequestMapping(value = "/getJPGCode", method = RequestMethod.GET)
    public void getJPGCode(HttpServletResponse response, HttpServletRequest request) {
        try {
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache");
            response.setDateHeader("Expires", 0);
            response.setContentType("image/jpg");
            /**
             * jgp格式验证码
             * 宽，高，位数。
             */
            Captcha captcha = new SpecCaptcha(146, 33, 4);
            //输出
            captcha.out(response.getOutputStream());
            HttpSession session = request.getSession(true);
            //存入Session
            session.setAttribute("_code", captcha.text().toLowerCase());
        } catch (Exception e) {
            LoggerUtils.fmtError(getClass(), e, "获取验证码异常：%s", e.getMessage());
        }
    }

    /**
     * 踢出页面
     *
     * @return
    @RequestMapping(value = "kickedOut", method = RequestMethod.GET)
    public ModelAndView kickedOut(HttpServletRequest request, UrlPathHelper pp) {
        if (SecurityStringUtils.isBlank(request.getHeader("Referer"))) {
            return redirect("/");
        }
        return new ModelAndView("common/kicked_out");
    }
     */

    /**
     * 没有权限提示页面
     * @return
     */
    @RequestMapping(value = "/unauthorized", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> unauthorized() {
        Map resultMap = ResultVoUtils.resultMap();
        resultMap.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        resultMap.put("massage", "无权访问该页面");
        return resultMap;
    }
}
