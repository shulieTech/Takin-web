package io.shulie.takin.adapter.cloud.impl.remote.watchman;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.fasterxml.jackson.core.type.TypeReference;

import io.shulie.takin.adapter.api.constant.EntrypointUrl;
import io.shulie.takin.adapter.api.entrypoint.watchman.CloudWatchmanApi;
import io.shulie.takin.adapter.api.model.request.watchman.WatchmanBatchRequest;
import io.shulie.takin.adapter.api.model.request.watchman.WatchmanListRequest;
import io.shulie.takin.adapter.api.model.request.watchman.WatchmanRegisterRequest;
import io.shulie.takin.adapter.api.model.request.watchman.WatchmanRegisterUpdateBatchRequest;
import io.shulie.takin.adapter.api.model.request.watchman.WatchmanRegisterUpdateRequest;
import io.shulie.takin.adapter.api.model.request.watchman.WatchmanResourceRequest;
import io.shulie.takin.adapter.api.model.request.watchman.WatchmanStatusRequest;
import io.shulie.takin.adapter.api.model.response.watchman.WatchmanCluster;
import io.shulie.takin.adapter.api.model.response.watchman.WatchmanNode;
import io.shulie.takin.adapter.api.model.response.watchman.WatchmanRegisterResponse;
import io.shulie.takin.adapter.api.service.CloudApiSenderService;
import io.shulie.takin.cloud.model.response.ApiResult;
import io.shulie.takin.cloud.model.response.WatchmanStatusResponse;
import org.springframework.stereotype.Service;

@Service
public class CloudWatchmanApiImpl implements CloudWatchmanApi {

    @Resource
    private CloudApiSenderService cloudApiSenderService;

    @Override
    public List<WatchmanCluster> list(WatchmanListRequest request) {
        return cloudApiSenderService.post(
            EntrypointUrl.join(EntrypointUrl.MODULE_WATCHMAN, EntrypointUrl.MATHOD_WATCHMAN_LIST),
            request, new TypeReference<ApiResult<List<WatchmanCluster>>>() {}).getData();
    }

    @Override
    public WatchmanStatusResponse status(WatchmanStatusRequest request) {
        return cloudApiSenderService.get(
            EntrypointUrl.join(EntrypointUrl.MODULE_WATCHMAN, EntrypointUrl.MATHOD_WATCHMAN_STATUS),
            request, new TypeReference<ApiResult<WatchmanStatusResponse>>() {}).getData();
    }

    @Override
    public Map<String, WatchmanStatusResponse> statusBatch(WatchmanBatchRequest request) {
        return cloudApiSenderService.post(
            EntrypointUrl.join(EntrypointUrl.MODULE_WATCHMAN, EntrypointUrl.MATHOD_WATCHMAN_STATUS_BATCH),
            request, new TypeReference<ApiResult<Map<String, WatchmanStatusResponse>>>() {}).getData();
    }

    @Override
    public List<WatchmanNode> resource(WatchmanResourceRequest request) {
        return cloudApiSenderService.get(
            EntrypointUrl.join(EntrypointUrl.MODULE_WATCHMAN, EntrypointUrl.MATHOD_WATCHMAN_RESOURCE),
            request, new TypeReference<ApiResult<List<WatchmanNode>>>() {}).getData();
    }

    @Override
    public Map<String, List<WatchmanNode>> resourceBatch(WatchmanBatchRequest request) {
        return cloudApiSenderService.post(
            EntrypointUrl.join(EntrypointUrl.MODULE_WATCHMAN, EntrypointUrl.MATHOD_WATCHMAN_RESOURCE_BATCH),
            request, new TypeReference<ApiResult<Map<String, List<WatchmanNode>>>>() {}).getData();
    }

    @Override
    public WatchmanRegisterResponse register(WatchmanRegisterRequest request) {
        return cloudApiSenderService.post(
            EntrypointUrl.join(EntrypointUrl.MODULE_WATCHMAN, EntrypointUrl.MATHOD_WATCHMAN_REGISTE),
            request, new TypeReference<ApiResult<WatchmanRegisterResponse>>() {}).getData();
    }

    @Override
    public Boolean updateRegister(WatchmanRegisterUpdateRequest request) {
        return cloudApiSenderService.post(
            EntrypointUrl.join(EntrypointUrl.MODULE_WATCHMAN, EntrypointUrl.MATHOD_WATCHMAN_UPDATE),
            request, new TypeReference<ApiResult<Boolean>>() {}).getData();
    }

    @Override
    public Map<String, Boolean> updateRegisterBatch(WatchmanRegisterUpdateBatchRequest request) {
        return cloudApiSenderService.post(
            EntrypointUrl.join(EntrypointUrl.MODULE_WATCHMAN, EntrypointUrl.MATHOD_WATCHMAN_UPDATE_BATCH),
            request, new TypeReference<ApiResult<Map<String, Boolean>>>() {}).getData();
    }
}
