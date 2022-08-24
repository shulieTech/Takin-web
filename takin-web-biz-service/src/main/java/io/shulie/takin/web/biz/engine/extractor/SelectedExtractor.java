package io.shulie.takin.web.biz.engine.extractor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.shulie.takin.cloud.data.util.PressureStartCache;
import io.shulie.takin.web.biz.pojo.response.scenemanage.WatchmanClusterResponse;
import io.shulie.takin.web.ext.entity.tenant.EngineType;
import org.apache.commons.lang3.StringUtils;

/**
 * 上次运行集群提取器
 */
public interface SelectedExtractor {

    WatchmanClusterResponse DEFAULT_RESPONSE = new WatchmanClusterResponse();

    WatchmanClusterResponse extract(ExtractorContext context);

    ExtractorType type();

    default WatchmanClusterResponse extraFromFeatures(String features) {
        if (StringUtils.isBlank(features)) {
            return DEFAULT_RESPONSE;
        }
        JSONObject params = JSON.parseObject(features);
        WatchmanClusterResponse response = new WatchmanClusterResponse();
        response.setId(params.getString(PressureStartCache.FEATURES_MACHINE_ID));
        response.setType(EngineType.of(params.getInteger(PressureStartCache.FEATURES_MACHINE_TYPE)));
        return response;
    }
}
