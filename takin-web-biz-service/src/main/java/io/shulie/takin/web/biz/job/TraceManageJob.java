package io.shulie.takin.web.biz.job;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt.TenantEnv;
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

        if(WebPluginUtils.isOpenVersion()) {
            // 私有化 + 开源
            collectData();
        }else {
            List<TenantInfoExt> tenantInfoExts = WebPluginUtils.getTenantInfoList();
            // saas 根据租户进行分片
            for (TenantInfoExt ext : tenantInfoExts) {
                // 开始数据层分片
                if (ext.getTenantId() % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem()) {
                    // 根据环境 分线程
                    for (TenantEnv e : ext.getEnvs()) {
                        // 分布式锁
                        String lockKey = JobRedisUtils.getJobRedis(ext.getTenantId(),e.getEnvCode(),shardingContext.getJobName());
                        if (distributedLock.checkLock(lockKey)) {
                            continue;
                        }
                        traceManageThreadPool.execute(() ->  {
                            boolean tryLock = distributedLock.tryLock(lockKey, 1L, 1L, TimeUnit.MINUTES);
                            if(!tryLock) {
                                return;
                            }
                            try {
                                WebPluginUtils.setTraceTenantContext(
                                    new TenantCommonExt(ext.getTenantId(),ext.getTenantAppKey(),e.getEnvCode(), ext.getTenantCode(), ContextSourceEnum.JOB.getCode()));
                                collectData();
                                WebPluginUtils.removeTraceContext();
                            } finally {
                                distributedLock.unLockSafely(lockKey);
                            }
                        });
                    }
                }
            }
        }
    }

    private void collectData() {
        //获取正在采集中的数据
        List<TraceManageDeployResult> traceManageDeployResults = traceManageDAO.queryTraceManageDeployByStatus(1);
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
                traceManageDeployUpdateParam.setTenantId(WebPluginUtils.traceTenantId());
                traceManageDeployUpdateParam.setEnvCode(WebPluginUtils.traceEnvCode());

                traceManageDAO.updateTraceManageDeployStatus(traceManageDeployUpdateParam,traceManageDeployResult.getStatus());
                log.warn("方法采集超时，"+ JsonHelper.bean2Json(traceManageDeployResult));
            }
        }
    }

}
