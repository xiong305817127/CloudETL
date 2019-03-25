/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.cloudetl.ext.config;

import org.pentaho.di.core.util.IdatrixPropertyUtil;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.www.CarteSingleton;
import org.pentaho.di.www.SlaveServerConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.spring.AnnotationBean;

/**
 * CloudDubboConfig.java
 * @author JW
 * @since 2017年7月27日
 *
 */
@SuppressWarnings("deprecation")
@Configuration
@PropertySource("file:./config/idatrix.properties")
public class CloudDubboConfig implements EnvironmentAware {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(CloudDubboConfig.class);
	
	private Environment env;
	
	public static final String APPLICATION_NAME = "cloudetl";
	
	// For provider
    public static final String PROVIDER_ANNOTATION_PACKAGE = "com.ys.idatrix.cloudetl.dubbo.service";
    
    // For consumer
    public static final String CONSUMER_ANNOTATION_PACKAGE = "com.ys.idatrix.cloudetl.reference,com.ys.idatrix.cloudetl.dubbo.reference";

    @Bean
    @Conditional(DubboDeploymentCondition.class)
    public ApplicationConfig applicationConfig() {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        applicationConfig.setName(APPLICATION_NAME);
        applicationConfig.setOwner("idatrix");
        applicationConfig.setOrganization("ys");
        LOGGER.debug("Dubbo - applicationConfig()");
        return applicationConfig;
    }
    
    @Bean
    @Conditional(DubboDeploymentCondition.class)
    public ProtocolConfig protocolConfig() {
    	ProtocolConfig protocolConfig = new ProtocolConfig();
    	try {
    		protocolConfig.setName("dubbo");
    		protocolConfig.setPort(Integer.parseInt(env.getProperty("dubbo.port")));
    		String host = env.getProperty("dubbo.host") ;
    		if(Utils.isEmpty(host) ) {
    			SlaveServerConfig slaveConfig = CarteSingleton.getSlaveServerConfig() ;
    			if( slaveConfig != null && slaveConfig.getSlaveServer() != null) {
    				host = slaveConfig.getSlaveServer().getHostname();
    			}
    		}
    		if(!Utils.isEmpty(host)) {
    			protocolConfig.setHost(host);
    		}
    		protocolConfig.setSerialization("java");
//    		protocolConfig.setOptimizer("com.ys.idatrix.metacube.api.SerializationOptimizerImpl");
    		//protocolConfig.setThreadpool("cached"); //默认 fixed
    	} catch (NumberFormatException e) {
    		protocolConfig.setPort(20999);
    	}
    	
    	LOGGER.debug("Dubbo - port=" + protocolConfig.getPort());
    	return protocolConfig;
    }

    @Bean
    @Conditional(DubboDeploymentCondition.class)
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(env.getProperty("zookeeper.address"));
        LOGGER.debug("Dubbo - zookeeper.address=" + registryConfig.getAddress());
        registryConfig.setCheck(false);
        registryConfig.setTimeout(30000);
        return registryConfig;
    }
    
    @Bean
    @Conditional(DubboDeploymentCondition.class)
    public ConsumerConfig consumerConfig() {
    	ConsumerConfig consumerConfig = new ConsumerConfig();
    	consumerConfig.setCheck(false);
    	consumerConfig.setTimeout(30000);
    	LOGGER.debug("Dubbo - consumerConfig()");
    	return consumerConfig;
    }
    
    @Bean
    @Conditional(DubboDeploymentCondition.class)
    public AnnotationBean annotationBean() {
        AnnotationBean annotationBean = new AnnotationBean();
        String package_base = CONSUMER_ANNOTATION_PACKAGE;
        if( "true".equalsIgnoreCase(IdatrixPropertyUtil.getProperty("idatrix.web.deployment")) ){
        	//当部署了web应用时(主服务器),部署提供者
        	package_base=package_base+","+PROVIDER_ANNOTATION_PACKAGE ;
        }
        annotationBean.setPackage(package_base);
        LOGGER.debug("Dubbo - annotationBean()");
        return annotationBean;
    }

	/*
	 * @see org.springframework.context.EnvironmentAware#setEnvironment(org.springframework.core.env.Environment)
	 */
	@Override
	public void setEnvironment(Environment environment) {
		env = environment;
	}

}
