package io.shulie.takin.adapter.cloud.impl.remote.watchman;

import java.util.List;

import javax.annotation.Resource;

import com.fasterxml.jackson.core.type.TypeReference;

import io.shulie.takin.adapter.api.constant.EntrypointUrl;
import io.shulie.takin.adapter.api.entrypoint.watchman.CloudWatchmanApi;
import io.shulie.takin.adapter.api.model.request.watchman.WatchmanResourceRequest;
import io.shulie.takin.adapter.api.model.request.watchman.WatchmanStatusRequest;
import io.shulie.takin.adapter.api.model.response.watchman.WatchmanNode;
import io.shulie.takin.adapter.api.service.CloudApiSenderService;
import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.cloud.model.response.WatchmanStatusResponse;
import org.springframework.stereotype.Service;

@Service
public class CloudWatchmanApiImpl implements CloudWatchmanApi {

    @Resource
    private CloudApiSenderService cloudApiSenderService;

    @Override
    public WatchmanStatusResponse status(WatchmanStatusRequest request) {
        return cloudApiSenderService.get(
            EntrypointUrl.join(EntrypointUrl.MODULE_WATCHMAN, EntrypointUrl.MATHOD_WATCHMAN_STATUS),
            request, new TypeReference<ApiResult<WatchmanStatusResponse>>() {}).getData();
    }

    @Override
    public List<WatchmanNode> resource(WatchmanResourceRequest request) {
        return cloudApiSenderService.get(
            EntrypointUrl.join(EntrypointUrl.MODULE_WATCHMAN, EntrypointUrl.MATHOD_WATCHMAN_RESOURCE),
            request, new TypeReference<ApiResult<List<WatchmanNode>>>() {}).getData();
    }
}
