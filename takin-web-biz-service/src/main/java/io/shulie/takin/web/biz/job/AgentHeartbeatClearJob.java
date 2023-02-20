package io.shulie.takin.web.biz.job;

import com.xxl.job.core.handler.annotation.XxlJob;
import io.shulie.takin.web.biz.service.agentupgradeonline.AgentReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description agent心跳数据清理任务
 * @Author ocean_wll
 * @Date 2021/11/18 2:30 下午
 */
@Component
@Slf4j
public class AgentHeartbeatClearJob {
    @Resource
    private AgentReportService agentReportService;

    @XxlJob("agentHeartbeatClearJobExecute")
    public void execute() {
        agentReportService.clearExpiredData();
    }
}
