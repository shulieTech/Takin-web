package io.shulie.takin.cloud.biz.collector;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

import cn.hutool.core.thread.NamedThreadFactory;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @author <a href="tangyuhan@shulie.io">yuhan.tang</a>
 * @date 2020-04-27 12:06
 */
@Configuration
public class ScheduleConfig {

    @Bean(name = "collectorSchedulerPool")
    public TaskScheduler scheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(5);
        ThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("collector-scheduler-thread-%d")
            .daemon(true).build();
        taskScheduler.setThreadFactory(threadFactory);
        taskScheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return taskScheduler;
    }

    @Bean(name = "checkStartedPodPool")
    public Executor podAsyncServiceExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(0);
        // 设置最大线程数
        executor.setMaxPoolSize(Integer.MAX_VALUE);
        //配置队列大小
        executor.setQueueCapacity(-1);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(60);
        // 设置默认线程名称
        executor.setThreadNamePrefix("check-started-pod");
        //执行初始化
        executor.initialize();
        return executor;
    }

    @Bean(name = "checkStartedJmeterPool")
    public Executor jmeterAsyncServiceExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(0);
        // 设置最大线程数
        executor.setMaxPoolSize(Integer.MAX_VALUE);
        //配置队列大小
        executor.setQueueCapacity(-1);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(60);
        // 设置默认线程名称
        executor.setThreadNamePrefix("check-started-jmeter");
        //执行初始化
        executor.initialize();
        return executor;
    }

    @Bean(name = "checkPodHeartbeatPool")
    public Executor podHeartbeatAsyncServiceExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(0);
        // 设置最大线程数
        executor.setMaxPoolSize(Integer.MAX_VALUE);
        //配置队列大小
        executor.setQueueCapacity(-1);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(60);
        // 设置默认线程名称
        executor.setThreadNamePrefix("check-pod-heartbeat");
        //执行初始化
        executor.initialize();
        return executor;
    }

    @Bean(name = "checkJmeterHeartbeatPool")
    public Executor jmeterHeartbeatAsyncServiceExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(0);
        // 设置最大线程数
        executor.setMaxPoolSize(Integer.MAX_VALUE);
        //配置队列大小
        executor.setQueueCapacity(-1);
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(60);
        // 设置默认线程名称
        executor.setThreadNamePrefix("check-jmeter-heartbeat");
        //执行初始化
        executor.initialize();
        return executor;
    }

    @Bean(name = "pressureStopPool")
    public ScheduledExecutorService pressureStopExecutor() {
        return Executors.newScheduledThreadPool(5, new NamedThreadFactory("pressure_stop", true));
    }
}
