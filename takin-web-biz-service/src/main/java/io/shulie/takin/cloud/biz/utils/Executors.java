/*
 * Copyright 2021 Shulie Technology, Co.Ltd
 * Email: shulie@shulie.io
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.shulie.takin.cloud.biz.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author liyuanba
 * @date 2021/10/8 1:59 下午
 */
@Slf4j
public class Executors {
    /**
     * 业务线程池
     */
    private static ThreadPoolExecutor threadPool;
    /**
     * 定时任务线程池
     */
    private static ScheduledExecutorService scheduledExecutorService;

    static {
        ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("cloud-service-thread-%d").build();
        //丢弃任务，写异常日志，但是不抛出异常
        RejectedExecutionHandler handler = new RejectedExecutionHandler() {
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
                log.error("Task " + r.toString() + " rejected from " + executor.toString());
            }
        };
        threadPool = new ThreadPoolExecutor(300, 400, 10L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(5000), factory, handler);

        ThreadFactory scheduleFactory = new ThreadFactoryBuilder().setNameFormat("cloud-schedule-thread-%d").build();
        scheduledExecutorService = java.util.concurrent.Executors.newScheduledThreadPool(3, scheduleFactory);
    }

    public static void execute(Runnable runnable) {
        threadPool.execute(runnable);
    }

    public static Future<?> submit(Runnable runnable) {
        return threadPool.submit(runnable);
    }

    public static <T> Future<T> submit(Callable<T> callable) {
        return threadPool.submit(callable);
    }

    /**
     * 延时执行
     *
     * @param runnable
     * @param delay    延时时间，单位毫秒
     */
    public static ScheduledFuture<?> schedule(Runnable runnable, long delay) {
        return scheduledExecutorService.schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * 延时执行
     *
     * @param callable
     * @param delay    延时时间，单位毫秒
     */
    public static <T> ScheduledFuture<T> schedule(Callable<T> callable, long delay) {
        return scheduledExecutorService.schedule(callable, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * 固定延时时间执行，前一个结束之后开始计算延时，受上一个执行时间影响，固定延时时间
     *
     * @param runnable     任务
     * @param initialDelay 第一次执行延时时间
     * @param delay        每次间隔延时时间，以上一次结束之后开始算
     */
    public static ScheduledFuture<?> scheduleWithFixedDelay(Runnable runnable, long initialDelay, long delay) {
        return scheduledExecutorService.scheduleWithFixedDelay(runnable, initialDelay, delay, TimeUnit.MILLISECONDS);
    }

    /**
     * 延时固定频率执行，前一个任务开始时间开始计算延时，不受上一个执行时间影响，固定频率执行
     *
     * @param runnable     任务
     * @param initialDelay 第一次执行延时时间
     * @param period       每次间隔时间，从上一次开始时间开始算
     */
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable runnable, long initialDelay, long period) {
        return scheduledExecutorService.scheduleAtFixedRate(runnable, initialDelay, period, TimeUnit.MILLISECONDS);
    }

}
