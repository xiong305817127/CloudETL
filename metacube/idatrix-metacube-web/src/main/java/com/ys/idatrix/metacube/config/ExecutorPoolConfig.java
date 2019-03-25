package com.ys.idatrix.metacube.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class ExecutorPoolConfig {

	@Value("${thread.pool.corePoolSize:10}")
	private int corePoolSize ;
	
	@Value("${thread.pool.maxPoolSize:50}")
	private int maxPoolSize ;
	 
	@Value("${thread.pool.queueSize:30}")
	private int queueSize  ;
	
	@Value("${thread.pool.keepAlive:30}")
	private int keepAlive ;

	
	@Bean
    public Executor testExecutorPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueSize);
        executor.setKeepAliveSeconds(keepAlive);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.DiscardPolicy());
        executor.setThreadNamePrefix("MetaCubeExecutorPool-");
 
        executor.initialize();
        return executor;
    }
	
}
