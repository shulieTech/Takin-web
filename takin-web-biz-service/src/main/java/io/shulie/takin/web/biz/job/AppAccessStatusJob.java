package io.shulie.takin.web.biz.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.service.ApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 无涯
 * @date 2021/6/15 5:30 下午
 */
@Component
@ElasticSchedulerJob(jobName = "appAccessStatusJob", cron = "0/10 * *  * * ?", description = "同步大数据应用状态")
@Slf4j
public class AppAccessStatusJob implements SimpleJob {

    @Autowired
    private ApplicationService applicationService;

    @Override
    public void execute(ShardingContext shardingContext) {
        applicationService.syncApplicationAccessStatus();
    }
}
