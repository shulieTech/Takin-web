package io.shulie.takin.web.biz.threadpool;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 隔离任务线程池,相互之间不影响，彼此之间不影响
 *
 * @author zhouyuan
 * @description: TODO
 * @date 2022/7/7 9:32 AM
 */
@Component
public class ThreadPoolUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    List<String> syncMachineList =
            Arrays.asList("reportMachineThreadPool",
                    "reportMachineThreadPool_one",
                    "reportMachineThreadPool_two");

    List<String> reportTpsList =
            Arrays.asList("reportTpsThreadPool",
                    "reportTpsThreadPool_one",
                    "reportTpsThreadPool_two");

    List<String> reportSummaryList =
            Arrays.asList("reportSummaryThreadPool",
                    "reportSummaryThreadPool_one",
                    "reportSummaryThreadPool_two");

    List<String> reportFinishList =
            Arrays.asList("reportFinishThreadPool",
                    "reportFinishThreadPool_one",
                    "reportFinishThreadPool_two");

    List<String> collectDataList =
            Arrays.asList("collectDataThreadPool",
                    "collectDataThreadPool_one",
                    "collectDataThreadPool_two",
                    "collectDataThreadPool_three");

    private static AtomicLong atomicMachine = new AtomicLong(0);
    private static AtomicLong atomicTps = new AtomicLong(0);
    private static AtomicLong atomicSummary = new AtomicLong(0);
    private static AtomicLong atomicFinish = new AtomicLong(0);
    private static AtomicLong atomicCollectData = new AtomicLong(0);

    static List<ThreadPoolExecutor> syncMachineDataJobThreadPools = new ArrayList<>();
    static List<ThreadPoolExecutor> reportTpsThreadPools = new ArrayList<>();
    static List<ThreadPoolExecutor> reportSummaryThreadPools = new ArrayList<>();
    static List<ThreadPoolExecutor> reportFinishThreadPools = new ArrayList<>();
    static List<ThreadPoolExecutor> collectDataThreadPools = new ArrayList<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() {
        syncMachineList.stream().forEach(pool -> {
            syncMachineDataJobThreadPools.add(applicationContext.getBean(pool, ThreadPoolExecutor.class));
        });

        reportTpsList.stream().forEach(pool -> {
            reportTpsThreadPools.add(applicationContext.getBean(pool, ThreadPoolExecutor.class));
        });

        reportSummaryList.stream().forEach(pool -> {
            reportSummaryThreadPools.add(applicationContext.getBean(pool, ThreadPoolExecutor.class));
        });

        reportFinishList.stream().forEach(pool -> {
            reportFinishThreadPools.add(applicationContext.getBean(pool, ThreadPoolExecutor.class));
        });

        collectDataList.stream().forEach(pool -> {
            collectDataThreadPools.add(applicationContext.getBean(pool, ThreadPoolExecutor.class));
        });
    }

    public static ThreadPoolExecutor getSyncMachinePool() {
        return syncMachineDataJobThreadPools.get((int) Math.abs(atomicMachine.getAndIncrement() % syncMachineDataJobThreadPools.size()));
    }

    public static ThreadPoolExecutor getReportTpsThreadPool() {
        return reportTpsThreadPools.get((int) Math.abs(atomicTps.getAndIncrement() % reportTpsThreadPools.size()));
    }

    public static ThreadPoolExecutor getReportSummaryThreadPool() {
        return reportSummaryThreadPools.get((int) Math.abs(atomicSummary.getAndIncrement() % reportSummaryThreadPools.size()));
    }

    public static ThreadPoolExecutor getReportFinishThreadPool() {
        return reportFinishThreadPools.get((int) Math.abs(atomicFinish.getAndIncrement() % reportFinishThreadPools.size()));
    }

    public static ThreadPoolExecutor getCollectDataThreadPool() {
        return collectDataThreadPools.get((int) Math.abs(atomicCollectData.getAndIncrement() % collectDataThreadPools.size()));
    }
}
