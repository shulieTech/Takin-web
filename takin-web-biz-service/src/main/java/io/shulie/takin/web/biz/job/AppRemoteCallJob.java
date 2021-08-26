package io.shulie.takin.web.biz.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.pojo.response.common.IsNewAgentResponse;
import io.shulie.takin.web.biz.service.impl.ApiServiceImpl;
import io.shulie.takin.web.biz.service.linkManage.AppRemoteCallService;
import io.shulie.takin.web.common.constant.AppConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 无涯
 * @date 2021/6/9 3:03 下午
 */

@Component
@ElasticSchedulerJob(jobName = "appRemoteCallJob", cron = "0 0/5 * * * ? *", description = "同步大数据远程调用数据，即入口数据")
@Slf4j
public class AppRemoteCallJob implements SimpleJob {

    @Autowired
    private AppRemoteCallService appRemoteCallService;

    @Autowired
    private ApiServiceImpl apiService;

    @Override
    public void execute(ShardingContext shardingContext) {
        IsNewAgentResponse config = apiService.getIsNewAgentResponseByConfig();
        if (config != null && config.getIsNew().equals(AppConstants.YES)) {
            appRemoteCallService.syncAmdb();
        }
    }
}
