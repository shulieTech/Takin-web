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
 * @date 2021/6/15 6:23 下午
 * todo 不知道做什么用
 */
@Component
@ElasticSchedulerJob(jobName = "configureJob", cron = "0/30 * * * * ?", description = "agent接收的关闭信息后不再上报信息")
@Slf4j
public class ConfigureJob implements SimpleJob {
    @Autowired
    private ApplicationService applicationService;

    @Override
    public void execute(ShardingContext shardingContext) {
        applicationService.configureTasks();
    }
}
