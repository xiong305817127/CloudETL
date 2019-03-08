package com.idatrix.resource.catalog.es.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * spring初始化ES相关bean的条件
 *
 * @author wzl
 */
public class InitEsBeanCondition implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext,
            AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment env = conditionContext.getEnvironment();
        String isUseFullTextSearch = env.getProperty("is_use_full_text_search");
        if (isUseFullTextSearch == null) {
            return false;
        }
        return Boolean.valueOf(isUseFullTextSearch);
    }
}
