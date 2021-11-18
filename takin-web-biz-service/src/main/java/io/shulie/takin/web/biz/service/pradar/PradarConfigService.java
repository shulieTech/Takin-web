package io.shulie.takin.web.biz.service.pradar;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.pradar.PradarZkConfigCreateRequest;
import io.shulie.takin.web.biz.pojo.request.pradar.PradarZkConfigDeleteRequest;
import io.shulie.takin.web.biz.pojo.request.pradar.PradarZKConfigQueryRequest;
import io.shulie.takin.web.biz.pojo.request.pradar.PradarZkConfigUpdateRequest;
import io.shulie.takin.web.biz.pojo.response.pradar.PradarZKConfigResponse;

public interface PradarConfigService {

    /**
     * 配置列表
     */
    PagingList<PradarZKConfigResponse> page(PradarZKConfigQueryRequest queryRequest);

    void initZooKeeperData();

    void updateConfig(PradarZkConfigUpdateRequest updateRequest);

    void addConfig(PradarZkConfigCreateRequest createRequest);

    void deleteConfig(PradarZkConfigDeleteRequest deleteRequest);
}
