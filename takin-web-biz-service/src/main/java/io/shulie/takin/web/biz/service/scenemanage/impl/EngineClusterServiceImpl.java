package io.shulie.takin.web.biz.service.scenemanage.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import cn.hutool.core.bean.BeanUtil;
import io.shulie.takin.adapter.api.entrypoint.watchman.CloudWatchmanApi;
import io.shulie.takin.adapter.api.model.request.watchman.WatchmanListRequest;
import io.shulie.takin.adapter.api.model.response.watchman.WatchmanCluster;
import io.shulie.takin.cloud.biz.config.AppConfig;
import io.shulie.takin.plugin.framework.core.PluginManager;
import io.shulie.takin.web.biz.engine.extractor.CompositeSelectedExtractor;
import io.shulie.takin.web.biz.engine.extractor.ExtractorContext;
import io.shulie.takin.web.biz.engine.selector.CompositeEngineSelector;
import io.shulie.takin.web.biz.engine.selector.EngineSelectorStrategy;
import io.shulie.takin.web.biz.pojo.response.scenemanage.WatchmanClusterResponse;
import io.shulie.takin.web.biz.service.scenemanage.EngineClusterService;
import io.shulie.takin.web.ext.api.tenant.WebTenantExtApi;
import io.shulie.takin.web.ext.entity.tenant.EngineType;
import io.shulie.takin.web.ext.entity.tenant.TenantEngineExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class EngineClusterServiceImpl implements EngineClusterService {

    @Resource
    private AppConfig appConfig;

    @Resource
    private PluginManager pluginManager;

    @Resource
    private CloudWatchmanApi cloudWatchmanApi;

    @Resource
    private CompositeEngineSelector compositeEngineSelector;

    @Resource
    private CompositeSelectedExtractor compositeSelectedExtractor;

    @Override
    public List<WatchmanClusterResponse> clusters() {
        List<TenantEngineExt> tenantEngineList = pluginManager.getExtension(WebTenantExtApi.class)
            .getTenantEngineList(WebPluginUtils.traceTenantId(), WebPluginUtils.traceEnvCode(), true);
        if (CollectionUtils.isEmpty(tenantEngineList)) {
            throw new RuntimeException("未找到注册的压力机集群，请先注册：[ " + appConfig.getClusterRegisterUrl() + " ]");
        }
        // 集群Id集合
        List<String> clusterIds = tenantEngineList.stream()
            .map(TenantEngineExt::getEngineId).collect(Collectors.toList());
        WatchmanListRequest request = new WatchmanListRequest();
        request.setWatchmanIdList(clusterIds);
        List<WatchmanCluster> clusters = cloudWatchmanApi.list(request);
        // 集群类型map
        Map<String, EngineType> engineTypeMap = tenantEngineList.stream()
            .collect(Collectors.toMap(TenantEngineExt::getEngineId, TenantEngineExt::getType));
        return clusters.stream().map(cluster -> {
            WatchmanClusterResponse response = BeanUtil.copyProperties(cluster, WatchmanClusterResponse.class);
            response.setType(engineTypeMap.get(cluster.getId()));
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public WatchmanClusterResponse selectOne() {
        return selectOneOfStrategy(null);
    }

    @Override
    public WatchmanClusterResponse selectOneOfStrategy(EngineSelectorStrategy strategy) {
        return compositeEngineSelector.select(clusters(), strategy);
    }

    @Override
    public WatchmanClusterResponse extractLastExecExtract(Long id, Integer type) {
        return compositeSelectedExtractor.extract(new ExtractorContext(id, type));
    }
}
