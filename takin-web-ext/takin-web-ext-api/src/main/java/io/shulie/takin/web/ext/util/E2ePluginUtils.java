package io.shulie.takin.web.ext.util;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.shulie.takin.web.ext.entity.e2e.E2eBaseStorageParam;
import io.shulie.takin.web.ext.entity.e2e.E2eBaseStorageRequest;
import io.shulie.takin.web.ext.entity.e2e.E2eExceptionConfigInfoExt;

import static io.shulie.takin.web.ext.util.WebPluginUtils.inspectionExtApi;

public class E2ePluginUtils {

    /**
     * 判断e2e插件是否存在
     */
    public static Boolean checkE2ePlugin() {
        if (Objects.nonNull(inspectionExtApi)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    /**
     * 根据租户ID 获取 e2e 模块的异常配置信息
     */
    public static List<E2eExceptionConfigInfoExt> getExceptionConfig(Long tenantId, String envCode, String... service) {
        if (Objects.nonNull(inspectionExtApi)) {
            return inspectionExtApi.getExceptionConfig(tenantId, envCode, service);
        }
        return Lists.newArrayList();
    }

    /**
     * 批量获取异常配置信息
     */
    public static Map<String,List<E2eExceptionConfigInfoExt>> getBatchExceptionConfig(Long tenantId, String envCode, List<String> service) {
        if (Objects.nonNull(inspectionExtApi)) {
            return inspectionExtApi.getBatchExceptionConfig(tenantId, envCode, service);
        }
        return Maps.newHashMap();
    }


    /**
     * 瓶颈计算
     */
    public static Map<Integer, Integer> bottleneckCompute(
        E2eBaseStorageRequest baseStorageRequest,
        List<E2eExceptionConfigInfoExt> configs) {

        if (Objects.nonNull(inspectionExtApi)) {
            return inspectionExtApi.bottleneckCompute(baseStorageRequest, configs);
        }
        return Maps.newHashMap();
    }

    /**
     * 瓶颈入库
     */
    public static Long bottleneckStorage(E2eBaseStorageParam baseStorageParam) {
        if (Objects.nonNull(inspectionExtApi)) {
            return inspectionExtApi.bottleneckStorage(baseStorageParam);
        }
        return null;
    }

}
