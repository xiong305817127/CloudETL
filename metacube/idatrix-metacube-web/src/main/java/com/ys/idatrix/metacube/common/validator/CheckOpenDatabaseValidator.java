package com.ys.idatrix.metacube.common.validator;

import com.ys.idatrix.metacube.api.beans.DatabaseTypeEnum;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 校验允许用户创建或注册的数据库类型
 *
 * @author wzl
 */
public class CheckOpenDatabaseValidator implements
        ConstraintValidator<CheckOpenDatabase, Integer> {

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        for (DatabaseTypeEnum dbTypeEnum : DatabaseTypeEnum.values()) {
            if (dbTypeEnum.getCode() == value && dbTypeEnum.isOpen()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void initialize(CheckOpenDatabase constraintAnnotation) {
        // 启动时执行
    }
}
