package io.shulie.takin.adapter.api.entrypoint.resource;

import io.shulie.takin.adapter.api.model.request.resource.ResourceCheckRequest;
import io.shulie.takin.adapter.api.model.request.resource.ResourceLockRequest;
import io.shulie.takin.adapter.api.model.request.resource.ResourceUnLockRequest;
import io.shulie.takin.adapter.api.model.response.cloud.resources.CloudResource;

import java.util.List;

public interface CloudResourceApi {

    List<CloudResource> getDetails(int taskId, String resourceId);

    Boolean check(ResourceCheckRequest request);

    String lock(ResourceLockRequest request);

    void unLock(ResourceUnLockRequest request);
}
