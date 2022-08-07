package io.shulie.takin.adapter.api.entrypoint.resource;

import java.util.List;
import java.util.Map;

import io.shulie.takin.adapter.api.model.request.resource.ResourceCheckRequest;
import io.shulie.takin.adapter.api.model.request.resource.ResourceLockRequest;
import io.shulie.takin.adapter.api.model.request.resource.ResourceUnLockRequest;
import io.shulie.takin.adapter.api.model.response.cloud.resources.CloudResource;

public interface CloudResourceApi {

    List<CloudResource> getDetails(int taskId, String resourceId);

    /**
     * 返回值表示：集群Id-分配pod数
     */
    Map<String, Integer> check(ResourceCheckRequest request);

    String lock(ResourceLockRequest request);

    void unLock(ResourceUnLockRequest request);
}
