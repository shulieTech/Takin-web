package io.shulie.takin.adapter.api.entrypoint.watchman;

import java.util.List;

import io.shulie.takin.adapter.api.model.request.watchman.WatchmanResourceRequest;
import io.shulie.takin.adapter.api.model.request.watchman.WatchmanStatusRequest;
import io.shulie.takin.adapter.api.model.response.watchman.WatchmanNode;
import io.shulie.takin.cloud.model.response.WatchmanStatusResponse;

public interface CloudWatchmanApi {

    WatchmanStatusResponse status(WatchmanStatusRequest request);

    List<WatchmanNode> resource(WatchmanResourceRequest request);
}
