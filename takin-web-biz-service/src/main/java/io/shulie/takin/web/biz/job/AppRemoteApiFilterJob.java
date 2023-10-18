package io.shulie.takin.web.biz.job;

import cn.hutool.core.collection.CollStreamUtil;
import com.google.common.collect.Maps;
import com.xxl.job.core.handler.annotation.XxlJob;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.service.linkmanage.AppRemoteCallService;
import io.shulie.takin.web.biz.service.linkmanage.ApplicationApiService;
import io.shulie.takin.web.biz.utils.job.JobRedisUtils;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.common.vo.application.ApplicationApiManageVO;
import io.shulie.takin.web.data.result.application.AppRemoteCallResult;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt.TenantEnv;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Z
 *
 * @author 南风
 * @date 2021/8/26 3:03 下午
 */

@Slf4j
@Component
public class AppRemoteApiFilterJob {

    @Resource
    private AppRemoteCallService appRemoteCallService;
    @Resource
    private ApplicationApiService apiService;
    @Resource
    @Qualifier("appRemoteApiFilterThreadPool")
    private ThreadPoolExecutor appRemoteApiFilterThreadPool;
    @Resource
    private DistributedLock distributedLock;

    @XxlJob("appRemoteApiFilterJobExecute")
    @Transactional(rollbackFor = Exception.class)
    public void execute() {
        if (WebPluginUtils.isOpenVersion()) {
            // 私有化 + 开源
            this.appRemoteApiFilter();
        } else {
            List<TenantInfoExt> tenantInfoExtList = WebPluginUtils.getTenantInfoList();
            for (TenantInfoExt ext : tenantInfoExtList) {
                if (CollectionUtils.isEmpty(ext.getEnvs())) {
                    continue;
                }
                // 根据环境 分线程
                for (TenantEnv e : ext.getEnvs()) {
                    // 分布式锁
                    String lockKey = JobRedisUtils.getJobRedis(ext.getTenantId(), e.getEnvCode(), "appRemoteApiFilterJobExecute");
                    if (distributedLock.checkLock(lockKey)) {
                        continue;
                    }
                    appRemoteApiFilterThreadPool.execute(() -> {
                        boolean tryLock = distributedLock.tryLock(lockKey, 0L, 1L, TimeUnit.MINUTES);
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
                    // 分片key
//                    int shardKey = (ext.getTenantId() + e.getEnvCode()).hashCode() & Integer.MAX_VALUE;
//                    if (shardKey % XxlJobHelper.getShardTotal() == XxlJobHelper.getShardIndex()) {
//
//                    }
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
                filterMap.put(apiManage.getApplicationId() + "##" + apiManage.getApi(), appRemoteCallFilterList);
            });
        });

        //清理所有的重复api
        List<Long> delIds = CollStreamUtil.toList(delList, AppRemoteCallResult::getId);
        appRemoteCallService.batchLogicDelByIds(delIds);

        List<AppRemoteCallResult> save = Lists.newArrayList();
        filterMap.forEach((k, v) -> {
            String[] temp = k.split("##");
            if (temp.length != 2) {
                return;
            }
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
