package io.shulie.takin.web.diff.api.common;

import io.shulie.takin.cloud.open.req.common.CloudCommonInfoWrapperReq;
import io.shulie.takin.cloud.open.resp.common.CommonInfosResp;
import io.shulie.takin.common.beans.response.ResponseResult;

public interface CloudCommonApi {
    ResponseResult<CommonInfosResp> getCloudConfigurationInfos(CloudCommonInfoWrapperReq req);
}
