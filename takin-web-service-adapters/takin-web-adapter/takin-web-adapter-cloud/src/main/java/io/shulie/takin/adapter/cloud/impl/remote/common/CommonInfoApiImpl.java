package io.shulie.takin.adapter.cloud.impl.remote.common;

import javax.annotation.Resource;

import com.fasterxml.jackson.core.type.TypeReference;

import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.adapter.api.constant.EntrypointUrl;
import io.shulie.takin.adapter.api.entrypoint.common.CommonInfoApi;
import io.shulie.takin.adapter.api.model.request.common.CloudCommonInfoWrapperReq;
import io.shulie.takin.adapter.api.model.response.common.CommonInfosResp;
import io.shulie.takin.adapter.api.service.CloudApiSenderService;
import org.springframework.stereotype.Service;

/**
 * 公共信息接口Api实现
 *
 * @author lipeng
 * @date 2021-06-24 4:19 下午
 */
@Service
public class CommonInfoApiImpl implements CommonInfoApi {

    @Resource
    private CloudApiSenderService cloudApiSenderService;

    /**
     * 获取cloud配置信息
     *
     * @return -
     */
    @Override
    public CommonInfosResp getCloudConfigurationInfos(CloudCommonInfoWrapperReq request) {
        return cloudApiSenderService.get(EntrypointUrl.join(EntrypointUrl.MODULE_COMMON, EntrypointUrl.METHOD_COMMON_VERSION),
            request, new TypeReference<ApiResult<CommonInfosResp>>() {}).getData();
    }
}
