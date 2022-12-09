package io.shulie.takin.web.biz.job;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.utils.job.JobRedisUtils;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.data.dao.perfomanceanaly.TraceManageDAO;
import io.shulie.takin.web.data.param.tracemanage.TraceManageDeployUpdateParam;
import io.shulie.takin.web.data.result.tracemanage.TraceManageDeployResult;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
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

    /**
     * aka 5 minutes
     */
    public static long timeout = 300 * 1000;

    @Autowired
    @Qualifier("traceManageThreadPool")
    private ThreadPoolExecutor traceManageThreadPool;

    @Autowired
    private DistributedLock distributedLock;

    @Override
    public void execute(ShardingContext shardingContext) {
        List<TraceManageDeployResult> deployResults = traceManageDAO.queryRunningTraceManageDeploy();
        if (CollectionUtils.isEmpty(deployResults)) {
            return;
        }
        Map<Tenant, List<TraceManageDeployResult>> map = deployResults.stream().collect(
            Collectors.groupingBy(deploy -> new Tenant(deploy.getTenantId(), deploy.getEnvCode())));
        for (Entry<Tenant, List<TraceManageDeployResult>> entry : map.entrySet()) {
            // 分布式锁
            Tenant key = entry.getKey();
            Long tenantId = key.getTenantId();
            String envCode = key.getEnvCode();
            String lockKey = JobRedisUtils.getJobRedis(tenantId, envCode, shardingContext.getJobName());
            if (distributedLock.checkLock(lockKey)) {
                continue;
            }
            traceManageThreadPool.execute(() ->  {
                boolean tryLock = distributedLock.tryLock(lockKey, 1L, 1L, TimeUnit.MINUTES);
                if(!tryLock) {
                    return;
                }
                try {
                    TenantCommonExt ext = new TenantCommonExt();
                    ext.setTenantId(tenantId);
                    ext.setEnvCode(envCode);
                    ext.setSource(ContextSourceEnum.JOB.getCode());
                    TenantInfoExt infoExt = WebPluginUtils.getTenantInfo(tenantId);
                    if (infoExt == null) {
                        log.error("租户信息未找到【{}】", tenantId);
                        return;
                    }
                    ext.setTenantAppKey(infoExt.getTenantAppKey());
                    WebPluginUtils.setTraceTenantContext(ext);
                    collectData(entry.getValue());
                } finally {
                    distributedLock.unLockSafely(lockKey);
                }
            });
        }
    }

    private void collectData(List<TraceManageDeployResult> traceManageDeployResults) {
        long currentTimeMillis = System.currentTimeMillis();
        for (TraceManageDeployResult traceManageDeployResult : traceManageDeployResults) {
            if ((currentTimeMillis - traceManageDeployResult.getUpdateTime().getTime()) > timeout) {
                TraceManageDeployUpdateParam traceManageDeployUpdateParam = new TraceManageDeployUpdateParam();
                traceManageDeployUpdateParam.setId(traceManageDeployResult.getId());
                //设置为采集超时状态
                traceManageDeployUpdateParam.setStatus(3);
                traceManageDeployUpdateParam.setTenantId(WebPluginUtils.traceTenantId());
                traceManageDeployUpdateParam.setEnvCode(WebPluginUtils.traceEnvCode());

                traceManageDAO.updateTraceManageDeployStatus(traceManageDeployUpdateParam,traceManageDeployResult.getStatus());
                log.warn("方法采集超时，"+ JsonHelper.bean2Json(traceManageDeployResult));
            }
        }
    }

    @Data
    @AllArgsConstructor
    @EqualsAndHashCode
    private static class Tenant {
        private Long tenantId;
        private String envCode;
    }
}
