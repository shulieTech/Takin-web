package io.shulie.takin.web.biz.job;

import javax.annotation.Resource;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.xxl.job.core.handler.annotation.XxlJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.service.scenemanage.SceneSchedulerTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author 无涯
 * @date 2021/6/15 5:36 下午
 */
@Component
//@ElasticSchedulerJob(jobName = "sceneSchedulerJob", cron = "0 */1 * * * ?", description = "压测场景定时执行，一分钟检查一次，待压测场景执行")
@Slf4j
public class SceneSchedulerJob {
    @Resource
    private SceneSchedulerTaskService sceneSchedulerTaskService;

    @XxlJob("sceneSchedulerJobExecute")
//    @Override
    public void execute() {
        // 查询所有
        sceneSchedulerTaskService.executeSchedulerPressureTask();
    }
}
