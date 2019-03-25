package com.ys.idatrix.db.init;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * @ClassName: ThreadPoolConfig
 * @Description: 线程此配置
 * @Author: ZhouJian
 * @Date: 2019/3/4
 */
@Configuration
public class ThreadPoolConfig {

    private static int CORE_POOL_SIZE = 5;
    private static int MAX_POOL_SIZE = 10;

    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor poolTaskExecutor = new ThreadPoolTaskExecutor();

        //线程池维护线程的最少数量
        poolTaskExecutor.setCorePoolSize(CORE_POOL_SIZE);

        //线程池维护线程的最大数量
        poolTaskExecutor.setMaxPoolSize(MAX_POOL_SIZE);

        //线程池所使用的缓冲队列
        poolTaskExecutor.setQueueCapacity(25);

        //线程池维护线程所允许的空闲时间
        poolTaskExecutor.setKeepAliveSeconds(300);
        poolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        return poolTaskExecutor;
    }

}
