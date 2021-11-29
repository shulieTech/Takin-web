package io.shulie.takin.web.biz.common;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import jodd.util.concurrent.ThreadFactoryBuilder;

/**
 * @author caijianying
 */
public class TaskHelper {
    private TaskBuilder builder;

    private void build() {
        this.builder = new TaskBuilder();
    }

    public void start(Runnable run) {
        if (this.builder == null) {
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "未配置线程池参数！");
        }
        ThreadFactory nameThreadFactory = new ThreadFactoryBuilder().setNameFormat(builder.taskName + "-%d").get();
        final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(5,
            nameThreadFactory);
        executor.scheduleWithFixedDelay(run, builder.initialDelay, builder.period, builder.periodTimeUnit);
    }

    private class TaskBuilder {
        private int coreSize;

        private long initialDelay;

        private String taskName;

        private long period;

        private TimeUnit periodTimeUnit;

        public void coreSize(int coreSize) {
            this.coreSize = coreSize;
        }

        public void initialDelay(int initialDelay) {
            this.initialDelay = initialDelay;
        }

        public void taskName(String taskName) {
            this.taskName = taskName;
        }

        public void period(long period) {
            this.period = period;
        }

        public void periodTimeUnit(TimeUnit periodTimeUnit) {
            this.periodTimeUnit = periodTimeUnit;
        }
    }

}
