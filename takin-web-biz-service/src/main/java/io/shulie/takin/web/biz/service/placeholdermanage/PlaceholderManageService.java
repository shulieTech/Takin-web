package io.shulie.takin.web.biz.service.placeholdermanage;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.placeholdermanage.PlaceholderManagePageRequest;
import io.shulie.takin.web.biz.pojo.request.placeholdermanage.PlaceholderManageRequest;
import io.shulie.takin.web.biz.pojo.response.placeholdermanage.PlaceholderManageResponse;

import java.util.Map;

public interface PlaceholderManageService {

    void createPlaceholder(PlaceholderManageRequest createRequest);

    void updatePlaceholder(PlaceholderManageRequest request);

    void deletePlaceholder(Long id);

    PagingList<PlaceholderManageResponse> listPlaceholder(PlaceholderManagePageRequest request);

    Map<String, String> getKvValue();
}
