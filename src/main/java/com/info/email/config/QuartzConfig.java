package com.info.email.config;

import java.util.concurrent.Executor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.info.email.autoconfigure.QuartzProperties;
import com.info.email.exception.ExceptionHandlingAsyncTaskExecutor;

import lombok.RequiredArgsConstructor;

@EnableAsync
@RequiredArgsConstructor
@EnableConfigurationProperties({ QuartzProperties.class })
public class QuartzConfig implements AsyncConfigurer{
	
	private final  QuartzProperties quartzProperties;
	
	@Override
	@Bean(name = "taskExecutor")
	public Executor getAsyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(quartzProperties.getAsync().getCorePoolSize());
		executor.setMaxPoolSize(quartzProperties.getAsync().getMaxPoolSize());
		executor.setQueueCapacity(quartzProperties.getAsync().getQueueCapacity());
		executor.setThreadNamePrefix("Email-");
		return new ExceptionHandlingAsyncTaskExecutor(executor);
	}
	
	@Override
	public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
		return new SimpleAsyncUncaughtExceptionHandler();
	}

}
