package io.shulie.takin.web.biz.service.webide;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowPageQueryRequest;
import io.shulie.takin.web.biz.pojo.request.webide.WebIDESyncScriptRequest;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessActivityInfoResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowListWebIDEResponse;

import java.util.List;

/**
 * @Author: 南风
 * @Date: 2022/6/20 1:48 下午
 */
public interface WebIDESyncService {

    void syncScript(WebIDESyncScriptRequest request);

    PagingList<BusinessFlowListWebIDEResponse> sceneList(BusinessFlowPageQueryRequest queryRequest);

    List<BusinessActivityInfoResponse> activityList(Long businessFlowId);
}
