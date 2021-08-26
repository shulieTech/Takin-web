package io.shulie.takin.web.biz.job;

import java.util.List;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.data.dao.perfomanceanaly.TraceManageDAO;
import io.shulie.takin.web.data.param.tracemanage.TraceManageDeployUpdateParam;
import io.shulie.takin.web.data.result.tracemanage.TraceManageDeployResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author 无涯
 * @date 2021/6/15 5:45 下午
 */
@Component
@ElasticSchedulerJob(jobName = "traceManageJob",cron = "*/5 * * * * ?", description = "性能分析-方法追踪超时检测")
@Slf4j
public class TraceManageJob implements SimpleJob {
    @Autowired
    private TraceManageDAO traceManageDAO;
    public static long timeout = 20 * 1000;
    @Override
    public void execute(ShardingContext shardingContext) {
        //获取正在采集中的数据
        List<TraceManageDeployResult> traceManageDeployResults = traceManageDAO.queryTraceManageDeployByStatus(1);
        if (CollectionUtils.isNotEmpty(traceManageDeployResults)) {
            long currentTimeMillis = System.currentTimeMillis();
            for (TraceManageDeployResult traceManageDeployResult : traceManageDeployResults) {
                if ((currentTimeMillis - traceManageDeployResult.getUpdateTime().getTime()) > timeout) {
                    TraceManageDeployUpdateParam traceManageDeployUpdateParam = new TraceManageDeployUpdateParam();
                    traceManageDeployUpdateParam.setId(traceManageDeployResult.getId());
                    //设置为采集超时状态
                    traceManageDeployUpdateParam.setStatus(3);
                    traceManageDAO.updateTraceManageDeployStatus(traceManageDeployUpdateParam,traceManageDeployResult.getStatus());
                    log.warn("方法采集超时，"+ JsonHelper.bean2Json(traceManageDeployResult));
                }
            }
        }
    }
}
