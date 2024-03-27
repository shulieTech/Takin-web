package io.shulie.takin.web.biz.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.service.perfomanceanaly.PressureMachineStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
* @Package io.shulie.takin.web.biz.job
* @ClassName: StatisticsPressureMachineJob
* @author hezhongqi
* @description:先不做租户隔离
* @date 2021/9/28 15:19
*/
@Component
@ElasticSchedulerJob(jobName = "statisticsPressureMachineJob", cron = "0 */1 * * * ?", description = "统计机器信息")
@Slf4j
public class StatisticsPressureMachineJob implements SimpleJob {

    @Autowired
    private PressureMachineStatisticsService pressureMachineStatisticsService;
    @Autowired
    private DistributedLock distributedLock;

    private String key ="statistics_pressure_machine_job_key";
    @Override
    public void execute(ShardingContext shardingContext) {
        if (distributedLock.checkLock(key)) {
            return;
        }
        try {
            boolean tryLock = distributedLock.tryLock(key, 0L, 30L, TimeUnit.SECONDS);
            if (!tryLock) {
                return;
            }
            // 私有化 + 开源
            pressureMachineStatisticsService.statistics();
        } finally {
            distributedLock.unLockSafely(key);
        }
    }
}
