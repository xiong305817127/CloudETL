/**
 * 云化数据集成系统 
 * iDatrix quality
 */
package com.ys.idatrix.quality.web.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.pentaho.di.core.util.OsgiBundleUtils;
import org.pentaho.di.core.util.Utils;
import org.springframework.http.HttpEntity;
import com.ys.idatrix.quality.dto.common.ReturnCodeDto;
import com.ys.idatrix.quality.ext.CloudSession;
import com.ys.idatrix.quality.logger.CloudLogUtils;
import com.ys.idatrix.quality.logger.CloudLogger;

import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

/**
 * Common aspect providing logger & exception
 * procedures for all cloud service implementation.
 * @author JW
 * @since 2017年7月26日
 *
 */
@Aspect
public class CloudWebAspect {
	
	
	/**
	 * 控制器方法切点（包括com.ys.idatrix.quality.web.controller的全部包，包括子包）
	 */
	@Pointcut("execution(* com.ys.idatrix.quality.web.controller..*.*(..))"
			+ " && !@annotation(org.springframework.web.bind.annotation.ModelAttribute)"
			+ " && !@within(org.springframework.web.bind.annotation.ControllerAdvice)")
	private void controllerPointcut() {}
	
	/**
	 * 切面：控制器方法调用Before通知
	 * @param joinPoint
	 */
	@Before("controllerPointcut()")
	public void beforeRequest(JoinPoint joinPoint) {
//		String className = joinPoint.getTarget().getClass().getSimpleName();
//		String methodName = joinPoint.getSignature().getName();
//		Object[] args = joinPoint.getArgs();
//		StringBuilder log = new StringBuilder("");
//		for (Object arg : args) {
//			if(arg!=null && arg.getClass().getSuperclass() == Object.class && (arg.getClass().getInterfaces() == null|| arg.getClass().getInterfaces().length ==0)){
//				log.append((JSONUtils.isObject(arg)?JSONObject.fromObject(arg).toString():arg )+ " ,");
//			}else{
//				log.append(arg+ " ,");
//			}
//		}
//		String key = CloudSession.getUsername()+"."+className + "." + methodName ;
//		CloudLogger.info("请求方法: " + key + "(" + log.toString() + ")");
	}
	
	/**
	 * 切面：控制器方法调用AfterReturning通知
	 * @param returnVal
	 */
	@AfterReturning(pointcut = "controllerPointcut()", returning = "returnVal")
	public void afterResponse(Object returnVal) {
		//CloudLogger.info("请求响应: " + (returnVal != null ? CloudLogUtils.jsonLog2(returnVal) : "<NULL>"));
	}
	
	/**
	 * 切面：控制器方法调用Around通知，包装返回值，增加返回代码
	 * @param proceedingJoinPoint
	 * @return
	 * @throws Throwable
	 */
	@Around("controllerPointcut()")
	public Object wrapReturnCode(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		Long startTime = System.currentTimeMillis();
		String className = proceedingJoinPoint.getTarget().getClass().getSimpleName();
		String methodName = proceedingJoinPoint.getSignature().getName();

		//控制器处理前
		Object[] args = proceedingJoinPoint.getArgs();
		//解析对象中的资源用户,并保存到session中
		resolveResoueceUser(args);
		
		StringBuilder logStart = new StringBuilder("请求:"+methodName);
		logStart.append("( ");
		for (Object arg : args) {
			if(arg!=null && arg.getClass().getSuperclass() == Object.class && (arg.getClass().getInterfaces() == null|| arg.getClass().getInterfaces().length ==0)){
				logStart.append((JSONUtils.isObject(arg)?JSONObject.fromObject(arg).toString():arg )+ " ,");
			}else{
				logStart.append(arg+ " ,");
			}
		}
		logStart.append(" )");
		CloudLogger.getInstance().addNumber().info(className,logStart.toString() );
		
		//控制器处理
		Object object = proceedingJoinPoint.proceed(); // 返回值用一个Object类型来接收
		
		//控制器处理后
		Object result;
		if( object instanceof ReturnCodeDto || object instanceof HttpEntity){
			result = object;
		}else{
			result = new ReturnCodeDto(0,object);
		}
		StringBuilder logEnd = new StringBuilder("响应: ");
		logEnd.append("耗时:"+(System.currentTimeMillis() - startTime)+"ms ");
		logEnd.append(",result: "+ (result != null ? CloudLogUtils.jsonLog2(result) : "<NULL>") );
		CloudLogger.getInstance().info(className,logEnd.toString());
		
		return result;
	}
	
	private void resolveResoueceUser( Object[] args  ) {
	
		if( args != null && args.length == 1&& args[0]!= null && !OsgiBundleUtils.baseTypeTransfor.containsKey(args[0].getClass()) && !( args[0] instanceof String )) {
			//只有一个非空参数,且不为基础类型不为String类型
			Object param = args[0] ;
			Object owner = OsgiBundleUtils.getOsgiField(param, "owner", true);
			if( owner!= null && !Utils.isEmpty(owner.toString())) {
				CloudSession.setThreadResourceUser( owner.toString() );
			}
		}

	}
	
	
}
