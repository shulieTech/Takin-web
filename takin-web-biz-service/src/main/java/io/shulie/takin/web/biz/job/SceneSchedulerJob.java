package io.shulie.takin.web.biz.job;

import com.xxl.job.core.handler.annotation.XxlJob;
import io.shulie.takin.web.biz.service.scenemanage.SceneSchedulerTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author 无涯
 * @date 2021/6/15 5:36 下午
 */
@Component
@Slf4j
public class SceneSchedulerJob {
    @Resource
    private SceneSchedulerTaskService sceneSchedulerTaskService;

    @XxlJob("sceneSchedulerJobExecute")
    public void execute() {
        // 查询所有
        sceneSchedulerTaskService.executeSchedulerPressureTask();
    }
}
