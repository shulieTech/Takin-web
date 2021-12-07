package io.shulie.takin.web.diff.cloud.impl.common;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import io.shulie.takin.web.diff.api.common.CloudCommonApi;
import io.shulie.takin.cloud.entrypoint.common.CommonInfoApi;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.cloud.sdk.model.response.common.CommonInfosResp;
import io.shulie.takin.cloud.sdk.model.request.common.CloudCommonInfoWrapperReq;

/**
 * @author caijianying
 */
@Component
public class CloudCommonInfoApiImpl implements CloudCommonApi {

    @Resource(type = CommonInfoApi.class)
    private CommonInfoApi cloudInfoApi;

    @Override
    public ResponseResult<CommonInfosResp> getCloudConfigurationInfos(CloudCommonInfoWrapperReq req) {
        try {
            return ResponseResult.success(cloudInfoApi.getCloudConfigurationInfos(req));
        } catch (Throwable e) {
            return ResponseResult.fail(e.getMessage(), "");
        }
    }
}
