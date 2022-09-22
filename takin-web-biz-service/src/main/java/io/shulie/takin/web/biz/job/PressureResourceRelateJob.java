package io.shulie.takin.web.biz.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceCommonService;
import io.shulie.takin.web.biz.utils.job.JobRedisUtils;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceDAO;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceEntity;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 压测资源关联应用,数据源,表
 */
@Component
@ElasticSchedulerJob(jobName = "pressureResourceRelateJob",
        isSharding = true,
        cron = "0 0/1 * * * ? *",
        description = "压测资源准备-压测资源关联应用,数据源,表")
@Slf4j
public class PressureResourceRelateJob implements SimpleJob {
    @Resource
    private PressureResourceCommonService pressureResourceCommonService;

    @Resource
    private PressureResourceDAO pressureResourceDAO;

    @Resource
    @Qualifier("pressureResourceThreadPool")
    private ThreadPoolExecutor pressureResourceThreadPool;

    @Resource
    private DistributedLock distributedLock;

    @Override
    public void execute(ShardingContext shardingContext) {
        // 查询所有压测资源准备配置
        List<PressureResourceEntity> resourceList = pressureResourceDAO.getAll();
        if (CollectionUtils.isEmpty(resourceList)) {
            log.warn("当前压测资源准备配置为空,暂不处理!!!");
            return;
        }
        // 按配置Id分片
        List<PressureResourceEntity> filterList = resourceList.stream().filter(resouce ->
                        resouce.getId() % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem())
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(filterList)) {
            return;
        }
        for (int i = 0; i < filterList.size(); i++) {
            PressureResourceEntity resource = filterList.get(i);
            String lockKey = JobRedisUtils.getRedisJobResource(1L, "default", "PressureResourceRelateJob" + resource.getId());
            if (distributedLock.checkLock(lockKey)) {
                continue;
            }
            pressureResourceThreadPool.execute(() -> {
                try {
                    boolean tryLock = distributedLock.tryLock(lockKey, 0L, 60L, TimeUnit.SECONDS);
                    if (!tryLock) {
                        return;
                    }
                    TenantCommonExt commonExt = new TenantCommonExt();
                    commonExt.setSource(ContextSourceEnum.JOB.getCode());
                    commonExt.setEnvCode(resource.getEnvCode());
                    commonExt.setTenantId(resource.getTenantId());
                    TenantInfoExt tenantInfoExt = WebPluginUtils.getTenantInfo(resource.getTenantId());
                    if (tenantInfoExt == null) {
                        return;
                    }
                    String tenantAppKey = tenantInfoExt.getTenantAppKey();
                    commonExt.setTenantAppKey(tenantAppKey);
                    WebPluginUtils.setTraceTenantContext(commonExt);

                    pressureResourceCommonService.processAutoPressureResourceRelate(resource);
                } finally {
                    distributedLock.unLockSafely(lockKey);
                }

            });
        }
    }
}
