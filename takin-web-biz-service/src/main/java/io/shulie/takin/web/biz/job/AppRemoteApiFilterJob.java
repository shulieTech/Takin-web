package io.shulie.takin.web.biz.job;

import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import com.google.common.collect.Maps;
import org.apache.commons.compress.utils.Lists;
import org.springframework.util.AntPathMatcher;
import cn.hutool.core.collection.CollStreamUtil;
import org.springframework.stereotype.Component;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import io.shulie.takin.web.ext.util.WebPluginUtils;
import io.shulie.takin.web.biz.utils.job.JobRedisUtils;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt.TenantEnv;
import io.shulie.takin.web.biz.service.linkmanage.AppRemoteCallService;
import io.shulie.takin.web.data.result.application.AppRemoteCallResult;
import io.shulie.takin.web.biz.service.linkmanage.ApplicationApiService;
import io.shulie.takin.web.common.vo.application.ApplicationApiManageVO;

/**
 * Z
 *
 * @author 南风
 * @date 2021/8/26 3:03 下午
 */

@Slf4j
@Component
@ElasticSchedulerJob(jobName = "appRemoteApiFilterJob", cron = "0 0/5 * * * ? *", description = "远程调用restful风格api合并")
public class AppRemoteApiFilterJob implements SimpleJob {

    @Resource
    private AppRemoteCallService appRemoteCallService;
    @Resource
    private ApplicationApiService apiService;
    @Resource
    @Qualifier("jobThreadPool")
    private ThreadPoolExecutor jobThreadPool;
    @Resource
    private DistributedLock distributedLock;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(ShardingContext shardingContext) {
        if (WebPluginUtils.isOpenVersion()) {
            // 私有化 + 开源
            this.appRemoteApiFilter();
        } else {
            List<TenantInfoExt> tenantInfoExtList = WebPluginUtils.getTenantInfoList();
            for (TenantInfoExt ext : tenantInfoExtList) {
                // 开始数据层分片
                if (ext.getTenantId() % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem()) {
                    // 根据环境 分线程
                    for (TenantEnv e : ext.getEnvs()) {
                        // 分布式锁
                        String lockKey = JobRedisUtils.getJobRedis(ext.getTenantId(), e.getEnvCode(), shardingContext.getJobName());
                        if (distributedLock.checkLock(lockKey)) {
                            continue;
                        }
                        jobThreadPool.execute(() -> {
                            boolean tryLock = distributedLock.tryLock(lockKey, 1L, 1L, TimeUnit.MINUTES);
                            if (!tryLock) {
                                return;
                            }
                            try {
                                WebPluginUtils.setTraceTenantContext(
                                    new TenantCommonExt(ext.getTenantId(), ext.getTenantAppKey(), e.getEnvCode(),
                                        ext.getTenantCode(), ContextSourceEnum.JOB.getCode()));
                                this.appRemoteApiFilter();
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

    private void appRemoteApiFilter() {
        //加载所有的远程调用数据
        Map<Long, List<AppRemoteCallResult>> appRemoteCallGroupByAppId = appRemoteCallService.getListGroupByAppId();
        if (appRemoteCallGroupByAppId.isEmpty()) {
            return;
        }
        //加载入口规则
        Map<Long, List<ApplicationApiManageVO>> apiManageGroupByAppId = apiService.selectListGroupByAppId();

        AntPathMatcher antPathMatcher = new AntPathMatcher();
        Map<String, List<AppRemoteCallResult>> filterMap = Maps.newHashMap();
        List<AppRemoteCallResult> delList = Lists.newArrayList();
        apiManageGroupByAppId.forEach((appId, manageVOS) -> {
            List<AppRemoteCallResult> appRemoteCallListVOS = appRemoteCallGroupByAppId.get(appId);
            manageVOS.forEach(apiManage -> {
                List<AppRemoteCallResult> appRemoteCallFilterList = Lists.newArrayList();
                if (CollectionUtils.isNotEmpty(appRemoteCallListVOS)) {
                    appRemoteCallListVOS.forEach(appRemoteCall -> {
                        boolean match = antPathMatcher.match(apiManage.getApi(), appRemoteCall.getInterfaceName());
                        boolean equals = apiManage.getApi().equals(appRemoteCall.getInterfaceName());
                        if (match && !equals) {
                            appRemoteCallFilterList.add(appRemoteCall);
                        }
                    });
                }
                delList.addAll(appRemoteCallFilterList);
                // 唯一
                filterMap.put(apiManage.getApplicationId()+"##" +apiManage.getApi(), appRemoteCallFilterList);
            });
        });

        //清理所有的重复api
        List<Long> delIds = CollStreamUtil.toList(delList, AppRemoteCallResult::getId);
        appRemoteCallService.batchLogicDelByIds(delIds);

        List<AppRemoteCallResult> save = Lists.newArrayList();
        filterMap.forEach((k, v) -> {
            String[] temp = k.split("##");
            String interfaceName = temp[1];
            //已经合并过的剔除
            List<AppRemoteCallResult> filterList = v.stream()
                    .filter(appRemoteCallResult -> appRemoteCallResult.getInterfaceName().equals(interfaceName))
                    .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(filterList)) {
                return;
            }
            if (CollectionUtils.isNotEmpty(v)) {
                AppRemoteCallResult appRemoteCallResult = v.get(0);
                appRemoteCallResult.setInterfaceName(interfaceName);
                appRemoteCallResult.setId(null);
                save.add(appRemoteCallResult);
            }
        });
        appRemoteCallService.batchSave(save);
    }
}
