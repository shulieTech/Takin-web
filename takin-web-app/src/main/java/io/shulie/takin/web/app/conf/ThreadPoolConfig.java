package io.shulie.takin.web.app.conf;

import java.util.concurrent.*;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.client.RestTemplate;

/**
 * @author shiyajian
 * create: 2020-10-04
 */
@Configuration
public class ThreadPoolConfig {

    /**
     * 用于定时任务
     *
     * @return 线程池
     */
    @Bean(name = "jobThreadPool")
    public ThreadPoolExecutor jobThreadPool() {
        ThreadFactory nameThreadFactory = new ThreadFactoryBuilder().setNameFormat("job-%d").build();
        return new ThreadPoolExecutor(20, 20, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(5000), nameThreadFactory,
            new ThreadPoolExecutor.AbortPolicy());
    }

    @Bean(name = "fastDebugThreadPool")
    public ThreadPoolExecutor fastDebug() {
        ThreadFactory nameThreadFactory = new ThreadFactoryBuilder().setNameFormat("fast-debug-%d").build();
        return new ThreadPoolExecutor(10, 50, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(20000), nameThreadFactory,
            new ThreadPoolExecutor.AbortPolicy());
    }

    @Bean(name = "modifyMonitorThreadPool")
    public ThreadPoolExecutor modifyMonitorExecutor() {
        ThreadFactory nameThreadFactory = new ThreadFactoryBuilder().setNameFormat("modify-monitor-%d").build();
        return new ThreadPoolExecutor(5, 5, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100), nameThreadFactory,
            new ThreadPoolExecutor.AbortPolicy());
    }

    @Bean(name = "schedulerPool")
    public TaskScheduler scheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(5);
        ThreadFactory threadFactory = new BasicThreadFactory.Builder().namingPattern("pradar-scheduler-thread-%d")
            .daemon(true).build();
        taskScheduler.setThreadFactory(threadFactory);
        taskScheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        return taskScheduler;
    }

    @Bean(name = "ScriptThreadPool")
    public ThreadPoolExecutor runShellTaskExecutor() {
        ThreadFactory nameThreadFactory = new ThreadFactoryBuilder().setNameFormat("script-thread-%d").build();
        return new ThreadPoolExecutor(5, 5, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100), nameThreadFactory,
            new ThreadPoolExecutor.AbortPolicy());
    }

    @Bean(name = "loadDataThreadPool")
    public ThreadPoolExecutor loadDataTaskExecutor() {
        ThreadFactory nameThreadFactory = new ThreadFactoryBuilder().setNameFormat("loaddata-thread-%d").build();
        return new ThreadPoolExecutor(5, 5, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100), nameThreadFactory,
            new ThreadPoolExecutor.AbortPolicy());
    }


    @Bean(name = "agentDataThreadPool")
    public ThreadPoolExecutor agentDataTaskExecutor() {
        ThreadFactory nameThreadFactory = new ThreadFactoryBuilder().setNameFormat("agentdata-thread-%d").build();
        return new ThreadPoolExecutor(5, 5, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000), nameThreadFactory,
            new ThreadPoolExecutor.AbortPolicy());
    }

    @Bean(name = "backgroundMonitorThreadPool")
    public ThreadPoolExecutor backgroundMonitorThreadPool() {
        ThreadFactory nameThreadFactory = new ThreadFactoryBuilder().setNameFormat("background-monitor-thread-%d").build();
        return new ThreadPoolExecutor(5, 5, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000), nameThreadFactory,
            new ThreadPoolExecutor.AbortPolicy());
    }

    @Bean("asynExecuteScriptThreadPool")
    public Executor myAsync() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //核心线程数
        executor.setCorePoolSize(5);
        //最大核心线程数
        executor.setMaxPoolSize(10);
        //心态检测，超过设置时间回收线程,线程空闲时的存活时间
        executor.setKeepAliveSeconds(0);
        //队列深度
        executor.setQueueCapacity(100);
        //线程名称
        executor.setThreadNamePrefix("myThreadA00-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        //拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Bean(name = "opsScriptThreadPool")
    public ThreadPoolExecutor runOPSShellTaskExecutor() {
        ThreadFactory nameThreadFactory = new ThreadFactoryBuilder().setNameFormat("ops-script-thread-%d").build();
        return new ThreadPoolExecutor(1, 5, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100), nameThreadFactory,
                new ThreadPoolExecutor.AbortPolicy());
    }

    @Primary
    @Bean
    public RestTemplate restTemplate() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(3000);
        requestFactory.setReadTimeout(15000);
        return new RestTemplate(requestFactory);
    }

    @Qualifier("consumingRestTemplate")
    @Bean
    public RestTemplate consumingRestTemplate() {
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(60000);
        return new RestTemplate(requestFactory);
    }

    /**
     * 说明: 文件下载
     *
     * @author shulie
     * @date 2018/10/10 10:37
     */
    @Bean
    public HttpMessageConverters restFileDownloadSupport() {
        ByteArrayHttpMessageConverter arrayHttpMessageConverter = new ByteArrayHttpMessageConverter();
        return new HttpMessageConverters(arrayHttpMessageConverter);
    }

    /**
     * 数据查询分片线程池
     * @return
     */
    @Bean(name = "queryAsyncThreadPool")
    public ThreadPoolExecutor queryAsyncThreadPool() {
        final int coreSize = Runtime.getRuntime().availableProcessors();
        ThreadFactory nameThreadFactory = new ThreadFactoryBuilder().setNameFormat("query-async-thread-%d").build();
        return new ThreadPoolExecutor(coreSize, coreSize * 2, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>(100), nameThreadFactory,
                new ThreadPoolExecutor.AbortPolicy());
    }

    @Value("${poolConfig.e2e.coreSize: 10}")
    private Integer e2eCoreSize;

    @Value("${poolConfig.e2e.maxSize: 10}")
    private Integer e2eMaxSize;

    @Value("${poolConfig.e2e.queueSize: 1000}")
    private Integer e2eQueueSize;

    /**
     * e2e线程池
     * @return
     */
    @Bean(name = "e2eThreadPool")
    public ThreadPoolExecutor e2eThreadPool() {
        ThreadFactory nameThreadFactory = new ThreadFactoryBuilder().setNameFormat("e2e-job-%d").build();
        return new ThreadPoolExecutor(e2eCoreSize, e2eMaxSize, 60L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(e2eQueueSize), nameThreadFactory, new ThreadPoolExecutor.AbortPolicy());
    }

}
