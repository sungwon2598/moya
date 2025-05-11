package com.study.moya.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean("taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(7);    // 기본 스레드 수: CPU 코어 수 6개 + 1와 동일
        executor.setMaxPoolSize(20);    // 최대 스레드 수: 코어 수의 2배
        executor.setQueueCapacity(100); // 큐 용량: 작업이 많을 경우 대기열에 쌓임
        executor.setThreadNamePrefix("AsyncThread-");
        executor.initialize();
        return executor;
    }
}
