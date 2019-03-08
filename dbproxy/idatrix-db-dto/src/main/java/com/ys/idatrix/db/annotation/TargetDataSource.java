package com.ys.idatrix.db.annotation;


import java.lang.annotation.*;


/**
 * @ClassName: TargetDataSource
 * @Description: 用于获取AOP切点及数据源key的注解
 * @Author: ZhouJian
 * @Date: 2019/1/11
 */
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetDataSource {

    /**
     * 该值即application.yml中的key值
     *
     * @return
     */
    String name() default "";

}
