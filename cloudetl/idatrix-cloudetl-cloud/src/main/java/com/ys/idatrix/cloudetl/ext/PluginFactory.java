/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.ext;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Application plug-ins bean factory.
 * 
 * @author JW
 * @since 05-12-2017
 * 
 */
public class PluginFactory implements ApplicationContextAware {
	private static ApplicationContext context = null;

	@Override
	public void setApplicationContext(ApplicationContext ctx) throws BeansException {
		context = ctx;
	}

	public static Object getBean(String beanId) {
		if (context != null)
			return context.getBean(beanId);
		return null;
	}

	public static <T> T getBean(Class<T> beanclass) {
		if (context != null)
			return context.getBean(beanclass);
		return null;
	}
	
	public  static  <T> Map<String, T> getBeans(Class<T> beanclass) {
		if (context != null)
			return context.getBeansOfType(beanclass);
		return null;
	}
	
	public static boolean containBean(String beanId) {
		if (context != null)
			return context.containsBean(beanId);
		return false;
	}

}
