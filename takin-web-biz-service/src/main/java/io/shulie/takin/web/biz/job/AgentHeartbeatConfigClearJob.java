package io.shulie.takin.web.biz.job;

import com.xxl.job.core.handler.annotation.XxlJob;
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
@Slf4j
public class AgentHeartbeatConfigClearJob {
    @Resource
    private ReportDetailService reportDetailService;

    @XxlJob("agentHeartbeatConfigClearJobExecute")
    public void execute() {
        log.info("执行过期配置清理任务。。");
        reportDetailService.clearExpiredData();
    }
}
