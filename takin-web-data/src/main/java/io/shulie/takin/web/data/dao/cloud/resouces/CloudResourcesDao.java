package io.shulie.takin.web.data.dao.cloud.resouces;

import io.shulie.takin.adapter.api.model.response.cloud.resources.Resource;

public interface CloudResourcesDao {
    void getResourceStatus(Resource resource);
}
