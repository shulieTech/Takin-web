package io.shulie.takin.web.biz.service.application;

import io.shulie.takin.common.beans.component.SelectVO;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationPluginDownloadPathInput;
import io.shulie.takin.web.biz.pojo.input.application.ApplicationPluginDownloadPathUpdateInput;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationPluginPathDetailResponse;
import io.shulie.takin.web.common.common.Response;

import java.util.List;

/**
 * @Author: 南风
 * @Date: 2021/11/10 4:25 下午
 */
public interface ApplicationAgentPathTypeService {

    List<SelectVO> supportType();

    Response<ApplicationPluginPathDetailResponse> queryConfigDetail();

    Response createConfig(ApplicationPluginDownloadPathInput createInput);

    Response updateConfig(ApplicationPluginDownloadPathUpdateInput updateInput);
}
