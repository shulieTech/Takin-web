package io.shulie.takin.web.biz.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.service.perfomanceanaly.ReportDetailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description agent心跳配置数据清理任务
 * @Author nanfeng
 * @Date 2021/12308 2:30 下午
 */
@Component
@ElasticSchedulerJob(jobName = "agentHeartbeatConfigClearJob", cron = "0 0/1 * * * ? *", description = "清理过期的agent心跳配置数据")
@Slf4j
public class AgentHeartbeatConfigClearJob implements SimpleJob {
    @Resource
    private ReportDetailService reportDetailService;

    @Override
    public void execute(ShardingContext shardingContext) {
        log.info("执行过期配置清理任务。。");
        reportDetailService.clearExpiredData();
    }
}
