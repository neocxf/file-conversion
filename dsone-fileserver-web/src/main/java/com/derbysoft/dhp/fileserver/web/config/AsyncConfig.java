package com.derbysoft.dhp.fileserver.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @author neo.fei {neocxf@gmail.com}
 */
@Configuration
public class AsyncConfig {

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
