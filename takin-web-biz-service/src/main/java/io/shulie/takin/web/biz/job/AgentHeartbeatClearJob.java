package io.shulie.takin.web.biz.job;

import javax.annotation.Resource;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.xxl.job.core.handler.annotation.XxlJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.service.agentupgradeonline.AgentReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Description agent心跳数据清理任务
 * @Author ocean_wll
 * @Date 2021/11/18 2:30 下午
 */
@Component
//@ElasticSchedulerJob(jobName = "agentHeartbeatClearJob", cron = "0 0/1 * * * ? *", description = "清理过期的agent心跳数据")
@Slf4j
public class AgentHeartbeatClearJob {
    @Resource
    private AgentReportService agentReportService;

    @XxlJob("agentHeartbeatClearJobExecute")
//    @Override
    public void execute() {
        agentReportService.clearExpiredData();
    }
}
