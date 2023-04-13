package io.shulie.takin.web.biz.service.pts;

import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.request.pts.PtsSceneRequest;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowDetailResponse;
import io.shulie.takin.web.biz.pojo.response.pts.JmeterJavaRequestResponse;
import io.shulie.takin.web.biz.pojo.response.pts.PtsDebugResponse;
import io.shulie.takin.web.biz.pojo.response.pts.PtsSceneResponse;

/**
 * @author junshi
 * @ClassName PtsProcessService
 * @Description
 * @createTime 2023年03月16日 15:26
 */
public interface PtsProcessService {

    BusinessFlowDetailResponse saveProcess(PtsSceneRequest request);

    PtsSceneResponse detailProcess(Long id);

    ResponseResult debugProcess(Long id);

    PtsDebugResponse getDebugRecord(Long id);

    String getDebugLog(Long id);
}
