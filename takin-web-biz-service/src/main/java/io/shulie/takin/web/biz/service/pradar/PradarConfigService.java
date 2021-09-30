package io.shulie.takin.web.biz.service.pradar;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.pradar.PradarZKConfigCreateRequest;
import io.shulie.takin.web.biz.pojo.request.pradar.PradarZKConfigDeleteRequest;
import io.shulie.takin.web.biz.pojo.request.pradar.PradarZKConfigQueryRequest;
import io.shulie.takin.web.biz.pojo.request.pradar.PradarZKConfigUpdateRequest;
import io.shulie.takin.web.biz.pojo.response.pradar.PradarZKConfigResponse;

public interface PradarConfigService {

    /**
     * 配置列表
     */
    PagingList<PradarZKConfigResponse> list(PradarZKConfigQueryRequest queryRequest);

    void initZooKeeperData();

    int updateConfig(PradarZKConfigUpdateRequest updateRequest);

    int addConfig(PradarZKConfigCreateRequest createRequest);

    int deleteConfig(PradarZKConfigDeleteRequest deleteRequest);
}
