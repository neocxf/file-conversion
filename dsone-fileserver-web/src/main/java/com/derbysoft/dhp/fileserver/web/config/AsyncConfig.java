package com.derbysoft.dhp.fileserver.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @author neo.fei {neocxf@gmail.com}
 */
@Configuration
@EnableScheduling
public class AsyncConfig {

    /**
     *  executor pool config for async execution or other thread-related config
     *
     * @return the executor
     */
    @Bean(name = "executorPool")
    public Executor asyncExecutor() {
        ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
        pool.setWaitForTasksToCompleteOnShutdown(true);
        pool.setCorePoolSize(20);
        pool.setMaxPoolSize(100);
        pool.setQueueCapacity(200);
        pool.initialize();
        return pool;
    }
}
