package io.shulie.takin.web.diff.api.common;

import io.shulie.takin.adapter.api.model.response.common.CommonInfosResp;
import io.shulie.takin.adapter.api.model.request.common.CloudCommonInfoWrapperReq;
import io.shulie.takin.common.beans.response.ResponseResult;

public interface CloudCommonApi {
    ResponseResult<CommonInfosResp> getCloudConfigurationInfos(CloudCommonInfoWrapperReq req);
}
