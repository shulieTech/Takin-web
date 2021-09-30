package io.shulie.takin.web.biz.job;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.data.dao.perfomanceanaly.TraceManageDAO;
import io.shulie.takin.web.data.param.tracemanage.TraceManageDeployUpdateParam;
import io.shulie.takin.web.data.result.tracemanage.TraceManageDeployResult;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

    @Autowired
    @Qualifier("jobThreadPool")
    private ThreadPoolExecutor jobThreadPool;
    @Override
    public void execute(ShardingContext shardingContext) {
        List<TenantInfoExt> tenantInfoExts = WebPluginUtils.getTenantInfoList();
        if(CollectionUtils.isEmpty(tenantInfoExts)) {
            // 私有化 + 开源
            collectData(null);
        }else {
            // saas
            tenantInfoExts.forEach(t -> {
                // 根据环境 分线程
                t.getEnvs().forEach(e ->
                    jobThreadPool.execute(() ->  collectData(new TenantCommonExt(t.getTenantId(),t.getUserAppKey(),e.getEnvCode()))));
            });
        }
    }

    private void collectData(TenantCommonExt ext) {
        //获取正在采集中的数据
        List<TraceManageDeployResult> traceManageDeployResults = traceManageDAO.queryTraceManageDeployByStatus(1,ext);
        if(CollectionUtils.isEmpty(traceManageDeployResults)) {
            return;
        }
        long currentTimeMillis = System.currentTimeMillis();
        for (TraceManageDeployResult traceManageDeployResult : traceManageDeployResults) {
            if ((currentTimeMillis - traceManageDeployResult.getUpdateTime().getTime()) > timeout) {
                TraceManageDeployUpdateParam traceManageDeployUpdateParam = new TraceManageDeployUpdateParam();
                traceManageDeployUpdateParam.setId(traceManageDeployResult.getId());
                //设置为采集超时状态
                traceManageDeployUpdateParam.setStatus(3);
                if (ext != null) {
                    traceManageDeployUpdateParam.setTenantId(ext.getTenantId());
                    traceManageDeployUpdateParam.setEnvCode(ext.getEnvCode());
                }
                traceManageDAO.updateTraceManageDeployStatus(traceManageDeployUpdateParam,traceManageDeployResult.getStatus());
                log.warn("方法采集超时，"+ JsonHelper.bean2Json(traceManageDeployResult));
            }
        }
    }
}
