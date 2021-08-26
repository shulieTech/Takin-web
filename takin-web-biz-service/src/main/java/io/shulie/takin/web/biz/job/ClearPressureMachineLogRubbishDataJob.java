package io.shulie.takin.web.biz.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.service.perfomanceanaly.PressureMachineLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 无涯
 * @date 2021/6/15 6:03 下午
 */
@Component
@ElasticSchedulerJob(jobName = "clearPressureMachineLogRubbishDataJob", cron = "0 0 */2 * * ?", description = "定时清理压力机日志数据，清理20天之前的数据")
@Slf4j
public class ClearPressureMachineLogRubbishDataJob implements SimpleJob {
    @Autowired
    private PressureMachineLogService machineLogService;

    @Override
    public void execute(ShardingContext shardingContext) {
        machineLogService.clearRubbishData();
    }
}
