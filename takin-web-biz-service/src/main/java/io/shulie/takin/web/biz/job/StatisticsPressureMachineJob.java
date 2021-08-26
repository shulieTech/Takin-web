package io.shulie.takin.web.biz.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.service.perfomanceanaly.PressureMachineStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 无涯
 * @date 2021/6/15 5:53 下午
 */
@Component
@ElasticSchedulerJob(jobName = "statisticsPressureMachineJob", cron = "0 */1 * * * ?", description = "统计机器信息")
@Slf4j
public class StatisticsPressureMachineJob implements SimpleJob {

    @Autowired
    private PressureMachineStatisticsService pressureMachineStatisticsService;

    @Override
    public void execute(ShardingContext shardingContext) {
        pressureMachineStatisticsService.statistics();
    }
}
