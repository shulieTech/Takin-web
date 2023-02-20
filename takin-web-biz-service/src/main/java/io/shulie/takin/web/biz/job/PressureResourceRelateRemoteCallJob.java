package io.shulie.takin.web.biz.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceCommonService;
import io.shulie.takin.web.biz.utils.job.JobRedisUtils;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceDAO;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 压测资源关联远程调用
 */
@Component
//@ElasticSchedulerJob(jobName = "pressureResourceRelateRemoteCallJob",
//        isSharding = true,
//        cron = "0 0/1 * * * ? *",
//        description = "压测资源准备-压测资源关联远程调用")
@Slf4j
public class PressureResourceRelateRemoteCallJob{
    @Resource
    private PressureResourceCommonService pressureResourceCommonService;

    @Resource
    private PressureResourceDAO pressureResourceDAO;

    @Resource
    @Qualifier("pressureResourceThreadPool")
    private ThreadPoolExecutor pressureResourceThreadPool;

    @Resource
    private DistributedLock distributedLock;

    @XxlJob("pressureResourceRelateRemoteCallJobExecute")
//    @Override
    public void execute() {
        // 查询所有压测资源准备配置
        List<PressureResourceEntity> resourceList = pressureResourceDAO.getAll();
        if (CollectionUtils.isEmpty(resourceList)) {
            log.warn("当前压测资源准备配置为空,暂不处理!!!");
            return;
        }
        // 按配置Id分片
        List<PressureResourceEntity> filterList = resourceList.stream().filter(resouce ->
                        resouce.getId() % XxlJobHelper.getShardTotal() == XxlJobHelper.getShardIndex())
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(filterList)) {
            return;
        }

        for (int i = 0; i < filterList.size(); i++) {
            PressureResourceEntity resource = filterList.get(i);
            String lockKey = JobRedisUtils.getRedisJobResource(1L, "default", "PressureResourceRelateRemoteCallJob" + resource.getId());
            if (distributedLock.checkLock(lockKey)) {
                continue;
            }
            pressureResourceThreadPool.execute(() -> {
                try {
                    boolean tryLock = distributedLock.tryLock(lockKey, 0L, 60L, TimeUnit.SECONDS);
                    if (!tryLock) {
                        return;
                    }
                    ResourceContextUtil.setTenantContext(resource);
                    pressureResourceCommonService.processAutoPressureResourceRelate_remoteCall(resource);
                } finally {
                    distributedLock.unLockSafely(lockKey);
                }
            });
        }
    }
}
