package io.shulie.takin.web.biz.job;

import cn.hutool.core.collection.CollStreamUtil;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.google.common.collect.Maps;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.service.linkmanage.AppRemoteCallService;
import io.shulie.takin.web.biz.service.linkmanage.ApplicationApiService;
import io.shulie.takin.web.common.vo.application.ApplicationApiManageVO;
import io.shulie.takin.web.data.result.application.AppRemoteCallResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Z
 *
 * @author 南风
 * @date 2021/8/26 3:03 下午
 */

@Component
@ElasticSchedulerJob(jobName = "appRemoteApiFilterJob", cron = "0 0/5 * * * ? *", description = "远程调用restful风格api合并")
@Slf4j
public class AppRemoteApiFilterJob implements SimpleJob {

    @Autowired
    private AppRemoteCallService appRemoteCallService;

    @Autowired
    private ApplicationApiService apiService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void execute(ShardingContext shardingContext) {
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
            List<AppRemoteCallResult> appRemoteCallFilterList = Lists.newArrayList();
            manageVOS.forEach(apiManage -> {
                //防止空指针
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
                filterMap.put(apiManage.getApi(), appRemoteCallFilterList);
            });
        });

        //清理所有的重复api
        List<Long> delIds = CollStreamUtil.toList(delList, AppRemoteCallResult::getId);
        appRemoteCallService.batchLogicDelByIds(delIds);

        List<AppRemoteCallResult> save = Lists.newArrayList();
        filterMap.forEach((k, v) -> {
            //已经合并过的剔除
            List<AppRemoteCallResult> filterList = v.stream()
                .filter(appRemoteCallResult -> appRemoteCallResult.getInterfaceName().equals(k))
                .collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(filterList)) {
                return;
            }
            AppRemoteCallResult appRemoteCallResult = v.get(0);
            appRemoteCallResult.setInterfaceName(k);
            appRemoteCallResult.setId(null);
            save.add(appRemoteCallResult);
        });
        appRemoteCallService.batchSave(save);
    }
}
