/**
 * 云化数据集成系统 
 * iDatrix CloudETL
 */
package com.ys.idatrix.quality.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * CloudApplicationConfig.java
 * @author JW
 * @since 2017年7月27日
 *
 */
@Configuration
@EnableAspectJAutoProxy
@ComponentScan
public class CloudWebConfig {
	
	@Bean
	public CloudWebAspect cloudServiceAspect() {
		return new CloudWebAspect();
	}

}
