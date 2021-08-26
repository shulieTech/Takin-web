package io.shulie.takin.web.diff.cloud.impl.common;

import io.shulie.takin.cloud.open.api.common.CommonInfoApi;
import io.shulie.takin.cloud.open.req.common.CloudCommonInfoWrapperReq;
import io.shulie.takin.cloud.open.resp.common.CommonInfosResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.diff.api.common.CloudCommonApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author caijianying
 */
@Component
public class CloudCommonInfoApiImpl implements CloudCommonApi {

    @Autowired
    private CommonInfoApi cloudInfoApi;

    @Override
    public ResponseResult<CommonInfosResp> getCloudConfigurationInfos(CloudCommonInfoWrapperReq req) {
        return cloudInfoApi.getCloudConfigurationInfos(req);
    }
}
