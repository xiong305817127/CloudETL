package com.ys.idatrix.db.annotation;


import com.ys.idatrix.db.enums.OpPermissionEnum;

import java.lang.annotation.*;


/**
 * @ClassName: TargetPermission
 * @Description: 用于获取AOP切点 需要验证权限的注解
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TargetPermission {

    /**
     * 所需权限
     *
     * @return
     */
    OpPermissionEnum value() default OpPermissionEnum.READ;


}
