package io.shulie.takin.adapter.api.entrypoint.common;

import io.shulie.takin.adapter.api.model.request.common.CloudCommonInfoWrapperReq;
import io.shulie.takin.adapter.api.model.response.common.CommonInfosResp;

/**
 * 公共信息接口Api
 *
 * @author lipeng
 * @date 2021-06-24 4:07 下午
 */
public interface CommonInfoApi {

    /**
     * 获取cloud配置信息
     *
     * @param request 入参
     * @return -
     */
    CommonInfosResp getCloudConfigurationInfos(CloudCommonInfoWrapperReq request);
}
