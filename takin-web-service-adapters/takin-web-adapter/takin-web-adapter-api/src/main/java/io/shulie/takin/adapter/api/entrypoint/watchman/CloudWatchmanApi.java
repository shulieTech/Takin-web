package io.shulie.takin.adapter.api.entrypoint.watchman;

import java.util.List;
import java.util.Map;

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
import io.shulie.takin.cloud.model.response.WatchmanStatusResponse;

public interface CloudWatchmanApi {

    List<WatchmanCluster> list(WatchmanListRequest request);

    WatchmanStatusResponse status(WatchmanStatusRequest request);

    Map<String, WatchmanStatusResponse> statusBatch(WatchmanBatchRequest request);

    List<WatchmanNode> resource(WatchmanResourceRequest request);

    Map<String, List<WatchmanNode>> resourceBatch(WatchmanBatchRequest request);

    WatchmanRegisterResponse register(WatchmanRegisterRequest request);

    Boolean updateRegister(WatchmanRegisterUpdateRequest request);

    Map<String, Boolean> updateRegisterBatch(WatchmanRegisterUpdateBatchRequest request);
}
