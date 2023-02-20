package io.shulie.takin.web.biz.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.xxl.job.core.handler.annotation.XxlJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.service.perfomanceanaly.PressureMachineStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
* @Package io.shulie.takin.web.biz.job
* @ClassName: StatisticsPressureMachineJob
* @author hezhongqi
* @description:先不做租户隔离
* @date 2021/9/28 15:19
*/
@Component
//@ElasticSchedulerJob(jobName = "statisticsPressureMachineJob", cron = "0 */1 * * * ?", description = "统计机器信息")
@Slf4j
public class StatisticsPressureMachineJob {

    @Autowired
    private PressureMachineStatisticsService pressureMachineStatisticsService;

    @XxlJob("statisticsPressureMachineJobExecute")
//    @Override
    public void execute() {
        pressureMachineStatisticsService.statistics();
    }
}
