package com.ys.idatrix.db.aspect;


import com.ys.idatrix.db.annotation.TargetDataSource;
import com.ys.idatrix.db.datasource.DynamicDataSourceHolder;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 数据源动态切换切面类
 *
 * @ClassName: DynamicDataSourceAspect
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Component
@Aspect
@Order(-10)  //使该切面在事务之前执行
public class DynamicDataSourceAspect {

    /**
     * 扫描所有含有@TargetDataSource注解的类
     */
    @Pointcut("@within(com.ys.idatrix.db.annotation.TargetDataSource)")
    public void switchDataSource() {
    }


    /**
     * 使用around方式监控
     *
     * @param point
     * @return
     * @throws Throwable
     */
    @Around("switchDataSource()")
    public Object switchByMethod(ProceedingJoinPoint point) throws Throwable {

        //获取执行方法
        Method method = getMethodByPoint(point);

        //获取执行参数
        Parameter[] params = method.getParameters();
        Parameter parameter;
        String sourceKey = null;
        boolean isDynamic = false;

        //扫描是否有参数带有@TargetDataSource注解
        for (int i = params.length - 1; i >= 0; i--) {
            parameter = params[i];
            if (parameter.getAnnotation(TargetDataSource.class) != null && point.getArgs()[i] instanceof String) {
                //key值即该参数的值，要求该参数必须为String类型
                sourceKey = (String) point.getArgs()[i];
                isDynamic = true;
                break;
            }
        }

        //不存在参数带有Datasource注解
        if (!isDynamic) {

            //获取方法的@TargetDataSource注解
            TargetDataSource dataSource = method.getAnnotation(TargetDataSource.class);

            //方法不含有注解
            if (null == dataSource || !StringUtils.hasLength(dataSource.name())) {
                //获取类级别的@DataSource注解
                dataSource = method.getDeclaringClass().getAnnotation(TargetDataSource.class);
            }

            if (null != dataSource) {
                //设置key值
                sourceKey = dataSource.name();
            }
        }
        return persistBySwitchSource(sourceKey, point);
    }


    /**
     * 切换数据源执行
     *
     * @param sourceKey
     * @param point
     * @return
     * @throws Throwable
     */
    private Object persistBySwitchSource(String sourceKey, ProceedingJoinPoint point) throws Throwable {
        try {
            //切换数据源
            DynamicDataSourceHolder.switchSource(sourceKey);
            //继续执行
            return point.proceed();
        } finally {
            //清空key值
            DynamicDataSourceHolder.clearDataSource();
        }
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
