package com.ys.idatrix.db.aspect;


import com.ys.idatrix.db.annotation.TargetMetric;
import com.ys.idatrix.db.api.common.RespResult;
import com.ys.idatrix.db.api.sql.dto.SqlExecRespDto;
import com.ys.idatrix.db.api.sql.dto.SqlTaskExecDto;
import com.ys.idatrix.db.api.sql.dto.SqlQueryRespDto;
import com.ys.idatrix.db.exception.DbProxyException;
import com.ys.idatrix.db.util.JsonUtils;
import com.ys.idatrix.metric.impl.ServiceMetricSinkImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 外部服务调用切面类
 *
 * @ClassName: ExternalInvokeAspect
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Slf4j
@Component
@Aspect
@Order(2)
public class ExternalInvokeAspect {

    private final String LOG_INPUT = "类 -> {} , 方法 -> {} , 输入参数 -> {}";

    private final String LOG_OUTPUT = "类 -> {} , 方法 -> {} ,执行成功|结果 -> {}";

    private final String LOG_SUCCESS = "类 -> {} , 方法 -> {} ,执行成功";

    private final String LOG_FAILURE = "类 -> {} , 方法 -> {} ,执行失败 -> {}";

    private final String LOG_EXCEPTION = "类 -> {} , 方法 -> {} ,执行异常 -> {}";

    private final String LOG_QUERY_SUCCESS = "类 -> {} , 方法 -> {} ,成功查询记录数 -> {}";


    /**
     * 扫描所有含有提供给外部调用的接口实现类
     */
    @Pointcut("execution(* com.ys.idatrix.db.service.external.provider.impl..*.*(..))")
    public void invokeMonitor() {
    }


    @Around("invokeMonitor()")
    public Object invokeMonitor(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        //切点函数运行
        Long startTimeMS = System.currentTimeMillis();
        Object resultObj;
        try {
            log.info(LOG_INPUT, className, methodName, JsonUtils.objectToJsonString(args, "参数省略..."));
            resultObj = joinPoint.proceed();
        } catch (Throwable e) {
            log.error(LOG_EXCEPTION, className, methodName, e.getMessage());
            Class returnClazz = ((MethodSignature) joinPoint.getSignature()).getReturnType();
            Object returnObj = returnClazz.newInstance();
            resultObj = warpException(returnObj, e);
            e.printStackTrace();
        }
        Long runTimeMs = System.currentTimeMillis() - startTimeMS;

        printLog(resultObj, className, methodName);

        publishToMetric(resultObj, joinPoint, runTimeMs);

        return resultObj;

    }


    /**
     * 根据返回类型和异常，获取返回对象
     *
     * @param returnObj
     * @param e
     * @return
     */
    private Object warpException(Object returnObj, Throwable e) {
        String errorMsg = e.getMessage();
        String sqlCommand = null;

        //自定义异常，获取sql输入执行命令，可能为空
        if (e instanceof DbProxyException) {
            sqlCommand = ((DbProxyException) e).getSqlCommand();
        }

        //sql 查询结果
        if (returnObj instanceof RespResult) {
            Object data = ((RespResult) returnObj).getData();
            if (null != data) {
                if (data instanceof SqlQueryRespDto) {
                    if (StringUtils.isNotBlank(sqlCommand)) {
                        SqlQueryRespDto respDto = new SqlQueryRespDto(sqlCommand);
                        return RespResult.buildFailWithDataAndMsg(respDto, errorMsg);
                    } else {
                        return RespResult.buildFailWithMsg(errorMsg);
                    }
                } else if (data instanceof SqlExecRespDto) {
                    if (StringUtils.isNotBlank(sqlCommand)) {
                        SqlExecRespDto respDto = new SqlExecRespDto(sqlCommand);
                        return RespResult.buildFailWithDataAndMsg(respDto, errorMsg);
                    } else {
                        return RespResult.buildFailWithMsg(errorMsg);
                    }
                } else {
                    return RespResult.buildFailWithMsg(errorMsg);
                }
            }
        }

        return RespResult.buildFailWithMsg(errorMsg);

    }


    /**
     * 错误日志输出
     *
     * @param obj
     * @param className
     * @param methodName
     * @return
     * @throws Exception
     */
    private void printLog(Object obj, String className, String methodName) throws Exception {
        //日志输出
        if (obj instanceof RespResult) {
            RespResult rt = (RespResult) obj;
            if (!rt.isSuccess()) {
                log.error(LOG_FAILURE, className, methodName, rt.getMsg());
            } else {
                Object data = rt.getData();
                if (null != data) {
                    if (data instanceof SqlExecRespDto) {
                        log.info(LOG_OUTPUT, className, methodName, JsonUtils.objectToJsonString(obj, "结果省略..."));
                    } else if (data instanceof SqlQueryRespDto) {
                        SqlQueryRespDto realRt = (SqlQueryRespDto) data;
                        log.info(LOG_QUERY_SUCCESS, className, methodName, CollectionUtils.isNotEmpty(realRt.getData()) ? realRt.getData().size() : 0);
                    } else if (data instanceof List<?>) {
                        List realRt = (List) data;
                        log.info(LOG_QUERY_SUCCESS, className, methodName, CollectionUtils.isNotEmpty(realRt) ? realRt.size() : 0);
                    } else {
                        log.info(LOG_SUCCESS, className, methodName);
                    }
                }
            }
        }

    }


    /**
     * 上传监控信息
     * <p>
     * 这里有sqlexecutedao.executequery/sqlexecutedao.executeupdate/sqlexecutedao.asyncexecute 需要上传监控信息
     * 这些标签可以根据"类名"+"函数名"生成： 如 executeQuery作为切点时候，类为SqlExecuteDao,拼接到一起就是再转换成小写：sqlexecutedao.executequery
     *
     * @param obj
     * @param point
     * @param runTimeMs
     * @throws Exception
     */
    private void publishToMetric(Object obj, ProceedingJoinPoint point, long runTimeMs) throws Exception {
        Method method = getMethodByPoint(point);
        TargetMetric targetMetric = method.getAnnotation(TargetMetric.class);
        if (null != targetMetric) {
            boolean hasMetric = targetMetric.value();
            if (hasMetric) {
                Boolean result;
                if (null == obj) {
                    result = false;
                } else if (obj instanceof SqlTaskExecDto) {
                    //根据代码分析返回SqlProcessDto为查询类服务都是成功
                    result = true;
                } else {
                    //其它函数返回值有isSuccess函数,根据这个函数判断是否执行成功。
                    String RUN_METHOD = "isSuccess";
                    Object value = obj.getClass().getMethod(RUN_METHOD, new Class<?>[]{}).invoke(obj);
                    result = (Boolean) value;
                }
                String className = getClassByPoint(point).getSimpleName();
                className = className.indexOf("Impl") > 0 ? className.substring(0, className.indexOf("Impl")) : className;
                Double statusValue = (double) ((result == true) ? 1 : 0);
                MetricSink metricSink = MetricSink.getInstrance();
                ServiceMetricSinkImpl sink = metricSink.getSink();
                String interfaceName = new StringBuffer(className).append("_").append(getMethodByPoint(point).getName()).toString().toLowerCase();
                String interfaceTimeLabel = "dbproxy_exec_time_" + interfaceName;
                String interfaceStatusLabel = "dbproxy_exec_success_" + interfaceName;
                //上传运行时间
                sink.publishSingleMetric(interfaceTimeLabel, runTimeMs);
                //上传成功状态
                sink.publishSingleMetric(interfaceStatusLabel, statusValue);
                log.info("sink.publishSingleMetricTime {}：{}", interfaceTimeLabel, runTimeMs);
                log.info("sink.publishSingleMetricStatus {}：{}", interfaceStatusLabel, statusValue);
            }
        }

    }


    /**
     * 获取切入类
     *
     * @param point
     * @return
     */
    private Class getClassByPoint(ProceedingJoinPoint point) {
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        return methodSignature.getDeclaringType();
    }


    /**
     * 获取切入方法
     *
     * @param point
     * @return
     */
    private Method getMethodByPoint(ProceedingJoinPoint point) {
        MethodSignature methodSignature = (MethodSignature) point.getSignature();
        return methodSignature.getMethod();
    }

}
