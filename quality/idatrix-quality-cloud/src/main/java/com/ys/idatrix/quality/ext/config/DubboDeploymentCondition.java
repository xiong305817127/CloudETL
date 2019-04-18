/**
 * 云化数据集成系统 
 * iDatrix quality
 */
package com.ys.idatrix.quality.ext.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.type.AnnotatedTypeMetadata;

import org.pentaho.di.core.util.IdatrixPropertyUtil;

/**
 * DubboDeploymentCondition.java
 * @author JW
 * @since 2017年8月1日
 *
 */
@PropertySource("file:./config/idatrix.properties")
public class DubboDeploymentCondition implements Condition {
	
	/*
	 * @see org.springframework.context.annotation.Condition#matches(org.springframework.context.annotation.ConditionContext, org.springframework.core.type.AnnotatedTypeMetadata)
	 */
	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		String ddProperty = IdatrixPropertyUtil.getProperty("dubbo.deployment", "false");
		
		if ("true".equalsIgnoreCase(ddProperty)) {
    		return true;
    	} else {
    		// No dubbo to be configured!
    		return false;
    	}
	}

}
