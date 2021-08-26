package io.shulie.takin.web.biz.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.service.scenemanage.SceneSchedulerTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 无涯
 * @date 2021/6/15 5:36 下午
 */
@Component
@ElasticSchedulerJob(jobName = "sceneSchedulerJob", cron = "0 */1 * * * ?", description = "压测场景定时执行，一分钟检查一次，待压测场景执行")
@Slf4j
public class SceneSchedulerJob implements SimpleJob {
    @Autowired
    private SceneSchedulerTaskService sceneSchedulerTaskService;

    @Override
    public void execute(ShardingContext shardingContext) {
        sceneSchedulerTaskService.executeSchedulerPressureTask();
    }
}
