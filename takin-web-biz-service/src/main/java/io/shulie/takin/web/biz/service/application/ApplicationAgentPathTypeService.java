package io.shulie.takin.web.biz.service.application;

import io.shulie.takin.web.biz.pojo.input.application.ApplicationPluginDownloadPathInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationPluginDownloadPathUpdateInput;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationPluginPathDetailResponse;
import io.shulie.takin.web.common.common.Response;

/**
 * @Author: 南风
 * @Date: 2021/11/10 4:25 下午
 */
public interface ApplicationAgentPathTypeService {


    Response<ApplicationPluginPathDetailResponse> queryConfigDetail();

    Response createConfig(ApplicationPluginDownloadPathInput createInput);

    Response updateConfig(ApplicationPluginDownloadPathUpdateInput updateInput);

    Response validEfficient();
}
