package io.shulie.takin.web.app.conf;

import com.dangdang.ddframe.job.lite.lifecycle.api.JobOperateAPI;
import com.dangdang.ddframe.job.lite.lifecycle.api.JobStatisticsAPI;
import com.dangdang.ddframe.job.lite.lifecycle.domain.JobBriefInfo;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.common.util.JVMUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.*;

/**
 * elasticjob 任务监听
 *
 * @Date 7月5号
 */
@Slf4j
@Component
public class EsJobTaskListener implements ApplicationListener<ApplicationStartedEvent> {
    // 需要监听的任务信息,按逗号分割
    @Value("${takin.listener.jobtask:calcApplicationSummaryJob,calcTpsTargetJob,finishReportJob,syncMachineDataJob}")
    private String jobTask;

    // 是否打印jstack,如果状态不对的时候
    @Value("${takin.web.jstack.enable:true}")
    private boolean jstackEnable;

    private static volatile long lastPrintTime = 0;

    private static final long TWO_MINUTES_MILLS = 2 * 60 * 1000;

    private static Semaphore guard = new Semaphore(1);

    private static final String USER_HOME = System.getProperty("user.home");

    private static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd_HH:mm:ss";

    @Resource
    private JobOperateAPI jobOperateAPI;

    @Resource
    private JobStatisticsAPI jobStatisticsAPI;

    @Resource
    private DistributedLock distributedLock;

    /**
     * @param event 事件源
     */
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        try {
            log.info("EsJob 任务监听开启 ");
            if (StringUtils.isNotBlank(jobTask)) {
                ScheduledExecutorService service = Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder().setNameFormat("JobTaskListener-%d").build());
                String esJobLock = "JobTaskListener_onApplicationEvent";
                // 检查job任务状态
                Runnable threadTask = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (!distributedLock.tryLock(esJobLock, 1L, 1L, TimeUnit.MINUTES)) {
                                return;
                            }
                            // 获取任务状态
                            String[] jobTaskArr = jobTask.split(",");
                            for (int i = 0; i < jobTaskArr.length; i++) {
                                // 获取任务状态
                                JobBriefInfo jobBriefInfo = jobStatisticsAPI.getJobBriefInfo(jobTaskArr[i]);
                                if (jobBriefInfo == null) {
                                    log.error("获取任务信息为空,{}", jobTaskArr[i]);
                                    continue;
                                }
                                // 任务状态非正常
                                if (jobBriefInfo.getStatus() == JobBriefInfo.JobStatus.SHARDING_FLAG) {
                                    // 打印下jstack,看下当前线程在干啥
                                    if (jstackEnable) {
                                        dumpJStack();
                                    }
                                    // 点下触发,让他重试下
                                    log.info("当前任务状态为分片待调整,触发当前任务执行,{}", jobTaskArr[i]);
                                    jobOperateAPI.trigger(com.google.common.base.Optional.of(jobTaskArr[i]), null);
                                }
                            }
                        } catch (Throwable e) {
                            log.error("任务处理失败,{}" + ExceptionUtils.getStackTrace(e));
                        } finally {
                            distributedLock.unLock(esJobLock);
                        }
                    }
                };
                // 提交任务,10分钟以后，每隔30秒执行一次
                service.scheduleAtFixedRate(threadTask, 10, 10, TimeUnit.SECONDS);
            }
        } catch (Throwable e) {
            log.error("JobTaskListener is fail,{}" + ExceptionUtils.getStackTrace(e));
        }
    }

    private void dumpJStack() {
        long now = System.currentTimeMillis();
        //dump every 2 minutes
        if (now - lastPrintTime < TWO_MINUTES_MILLS) {
            return;
        }
        if (!guard.tryAcquire()) {
            return;
        }
        ExecutorService pool = Executors.newSingleThreadExecutor();
        pool.execute(() -> {
            String dumpPath = USER_HOME;
            SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT);
            String dateStr = sdf.format(new Date());
            //try-with-resources
            try (FileOutputStream jStackStream = new FileOutputStream(
                    new File(dumpPath, "Takin_web_JStack.log" + "." + dateStr))) {
                JVMUtil.jstack(jStackStream);
            } catch (Throwable t) {
                log.error("dump jStack error", t);
            } finally {
                guard.release();
            }
            lastPrintTime = System.currentTimeMillis();
        });
        //must shutdown thread pool ,if not will lead to OOM
        pool.shutdown();
    }
}
