package com.idatrix.unisecurity.aspects;

import com.idatrix.unisecurity.common.domain.UUser;
import com.idatrix.unisecurity.core.shiro.token.manager.ShiroTokenManager;
import com.idatrix.unisecurity.log.SecurityLog;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @ClassName WebLogAspect
 * @Description 拦截controller记录log的一个切面
 * @Author ouyang
 * @Date 2018/8/27 15:49
 * @Version 1.0
 **/
@Aspect
public class WebLogAspect {

    private Logger log = LoggerFactory.getLogger(getClass());

    private String server;

    private Date visitTime;// 请求的时间

    private Boolean isImplement;// 当前log是否写入数据库

    public void setServer(String server) {
        this.server = server;
    }

    public void init() {
        isImplement = false;
        MDC.remove("userId");
        MDC.remove("userName");
        MDC.remove("renterId");
        MDC.remove("server");
        MDC.remove("resource");
        MDC.remove("methodType");
        MDC.remove("clientIp");
        MDC.remove("visitTime");
        // 记录开始时间
        visitTime = new Date();
    }

    /**
     * Controller层切点 注解拦截
     *
     * @param []
     * @return void
     * @author oyr
     * @date 2018/8/27 15:56
     */
    @Pointcut("execution(* com.idatrix.unisecurity.*.controller..*.*(..))")
    public void controllerAspect() {
    }

    /**
     * 方法调用前触发
     *
     * @param [joinPoint]
     * @return void
     * @author oyr
     * @date 2018/8/27 15:56
     */
    @Before("controllerAspect()")
    public void doBefore(JoinPoint joinPoint) {
        //打印请求信息
        println(joinPoint);

        //初始化
        init();

        //SSOUser user = UserHolder.getUser();

        // 当前登录的用户
        UUser user = ShiroTokenManager.getToken();
        out:
        if (user != null) {// 只有登录后才会进行记载
            Long userId = user.getId();
            String username = user.getUsername();
            Long renterId = user.getRenterId();
            if (renterId == null) {
                renterId = 0l;
            }
/*            String userId = user.getId();
            Object username = user.getProperty("username");
            Object renterId = user.getProperty("renterId");*/

            //获取客户端请求的信息
            RequestAttributes ra = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes sra = (ServletRequestAttributes) ra;
            HttpServletRequest request = sra.getRequest();
            String resource = request.getRequestURI();//请求的uri
            if (resource.contains("open")) {
                break out;
            }
            String clientIp = getIp(request);//请求的IP
            String methodType = request.getMethod();//请求类型

            MDC.put("userId", userId + "");
            MDC.put("userName", username + "");
            MDC.put("renterId", renterId + "");
            MDC.put("server", server);
            MDC.put("resource", resource);
            MDC.put("methodType", methodType);
            MDC.put("clientIp", clientIp);
            MDC.put("result", "success");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String dateStr = format.format(visitTime);
            MDC.put("visitTime", dateStr);
            isImplement = true;
        }
    }

    /**
     * 方法执行后触发，controller抛出异常不会进入
     *
     * @param [joinPoint, result]
     * @return java.lang.Object
     * @author oyr
     * @date 2018/8/28 16:37
     */
    @AfterReturning(returning = "result", pointcut = "controllerAspect()")
    public Object doAfterReturning(JoinPoint joinPoint, Object result) {
        // 请求未抛出异常，代表成功
        if (isImplement) {
            SecurityLog.log("success，log入库中");
        }
        return result;
    }

    /**
     * 切入点抛出异常后触发
     *
     * @param [e]
     * @return void
     * @author oyr
     * @date 2018/8/28 16:55
     */
    @AfterThrowing(throwing = "e", pointcut = "controllerAspect()")
    public void doAfterThrowing(Exception e) {
        log.error("error：" + e.getMessage());
        // 请求未抛出异常，代表失败
        if (isImplement) {
            MDC.put("result", "error");
            SecurityLog.log("error，log入库中");
        }
    }

    /**
     * 获取客户端ip
     *
     * @param [request]
     * @return java.lang.String
     * @author oyr
     * @date 2018/8/27 16:07
     */
    public static String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (StringUtils.isNotEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

    /**
     * 打印请求的信息
     *
     * @param [joinPoint]
     * @return void
     * @author oyr
     * @date 2018/8/28 17:26
     */
    public void println(JoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        String methodName = joinPoint.getSignature().getName();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            Map<String, String[]> parameterMap = request.getParameterMap();
            log.info("aop println()：被访问的类和方法：" + className + "." + methodName + "，参数是：" + parameterMap.toString());
        }
    }
}
