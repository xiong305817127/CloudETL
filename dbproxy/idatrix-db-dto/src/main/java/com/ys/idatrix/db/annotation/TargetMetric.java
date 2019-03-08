package com.ys.idatrix.db.annotation;

import java.lang.annotation.*;

/**
 * @ClassName: TargetMoniter
 * @Description:
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetMetric {

    boolean value() default true;

}
