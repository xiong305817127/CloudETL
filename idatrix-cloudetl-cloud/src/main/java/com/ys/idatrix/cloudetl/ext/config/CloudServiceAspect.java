/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.ext.config;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import com.ys.idatrix.cloudetl.logger.CloudLogUtils;
import com.ys.idatrix.cloudetl.logger.CloudLogger;

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
public class CloudServiceAspect {
	
	/**
	 * 服务方法切点（包括com.ys.idatrix.cloudetl.service.*的全部包， 但不包括*中的子包）
	 */
	@Pointcut("execution(* com.ys.idatrix.cloudetl.service.*.*.*(..))"
			+ " && !@within(org.springframework.web.bind.annotation.ControllerAdvice)")
	private void servicePointcut2() {}
	
	/**
	 * 元数据处理方法切点（包括com.ys.idatrix.cloudetl.metacube的全部包，包括子包）
	 */
	@Pointcut("execution(* com.ys.idatrix.cloudetl.metacube..*.*(..))"
			+ " && !@within(org.springframework.web.bind.annotation.ControllerAdvice)")
	private void metacubePointcut() {}
	
	
	/**
	 * 切面：服务方法调用Before通知
	 * @param joinPoint
	 */
	@Before("servicePointcut2()")
	public void beforeService(JoinPoint joinPoint) {
	}
	
	/**
	 * 切面：服务方法调用AfterReturning通知
	 * @param returnVal
	 */
	@AfterReturning(pointcut = "servicePointcut2()", returning = "returnVal")
	public void afterServiceReturing(Object returnVal) {
	}
	
	@Around("servicePointcut2()")
	public Object wrapReturnCode(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		
		Long startTime = System.currentTimeMillis();
		String className = proceedingJoinPoint.getTarget().getClass().getSimpleName();
		String methodName = proceedingJoinPoint.getSignature().getName();
		
		//服务处理前
		Object[] args = proceedingJoinPoint.getArgs();
		StringBuilder logStart = new StringBuilder("服务调用:"+methodName);
		logStart.append("( ");
		for (Object arg : args) {
			if(arg!=null && arg.getClass().getSuperclass() == Object.class && (arg.getClass().getInterfaces() == null|| arg.getClass().getInterfaces().length ==0)){
				logStart.append((JSONUtils.isObject(arg)?JSONObject.fromObject(arg).toString():arg )+ " ,");
			}else{
				logStart.append(arg+ " ,");
			}
		}
		logStart.append(" )");
		CloudLogger.getInstance().debug(className, logStart.toString());
		
		//服务处理
		Object object = proceedingJoinPoint.proceed(); // 返回值用一个Object类型来接收
		
		//服务处理后
		StringBuilder logEnd = new StringBuilder("服务结束:");
		logEnd.append(",耗时:"+(System.currentTimeMillis() - startTime)+"ms");
		logEnd.append(object != null ? CloudLogUtils.jsonLog2(object) : "<NULL>");
		CloudLogger.getInstance().debug(className, logEnd.toString());
		
		return object;
	}

	/**
	 * 切面：服务方法调用AfterThrowing通知
	 * @param joinPoint
	 * @param e
	 */
	@AfterThrowing(pointcut = "servicePointcut2()", throwing="e")
	public void afterThrowingForService(JoinPoint joinPoint, Throwable e) {
		String className = joinPoint.getTarget().getClass().getSimpleName();
		String methodName = joinPoint.getSignature().getName();
		CloudLogger.getInstance().error(className, "异常拦截: 服务方法["+methodName+"]抛出[ "+e.getClass().getSimpleName()+"]异常.",e);
	}
	
	/**
	 * 切面：元数据处理方法调用AfterThrowing通知
	 * @param joinPoint
	 * @param e
	 */
	@AfterThrowing(pointcut = "metacubePointcut()", throwing="e")
	public void afterThrowingForMetaCube(JoinPoint joinPoint, Throwable e) {
		String className = joinPoint.getTarget().getClass().getSimpleName();
		String methodName = joinPoint.getSignature().getName();
		CloudLogger.getInstance().error(className, "异常拦截: 服务方法["+methodName+"]抛出 "+e.getClass().getSimpleName()+"异常.",e);
	}


}
