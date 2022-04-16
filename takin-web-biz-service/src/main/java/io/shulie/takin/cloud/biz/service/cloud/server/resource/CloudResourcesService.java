package io.shulie.takin.cloud.biz.service.cloud.server.resource;

import io.shulie.takin.adapter.api.model.response.cloud.resources.CloudResource;
import io.shulie.takin.adapter.api.model.response.cloud.resources.Resource;

import java.util.List;

public interface CloudResourcesService {
    Resource getDetail(List<CloudResource> resources, Integer taskId, String resourceId, String sortField, String sortType, Integer currentPage, Integer pageSize);
}
