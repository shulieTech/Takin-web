package io.shulie.takin.web.biz.service.webide;

import io.shulie.takin.web.biz.pojo.request.webide.WebIDESyncScriptRequest;

import java.util.List;

/**
 * @Author: 南风
 * @Date: 2022/6/20 1:48 下午
 */
public interface WebIDESyncService {

    void syncScript(WebIDESyncScriptRequest request);
}
