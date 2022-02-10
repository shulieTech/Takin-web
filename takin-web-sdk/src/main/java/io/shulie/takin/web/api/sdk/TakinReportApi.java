package io.shulie.takin.web.api.sdk;

import java.util.List;

import io.shulie.takin.web.api.client.AbstractTakinWebClient;
import io.shulie.takin.web.api.model.SceneStatusModel;
import io.shulie.takin.web.api.model.ScriptNodeSummaryModel;
import io.shulie.takin.web.api.response.ResponseResult;

/**
 * @author caijianying
 */
public interface TakinReportApi {
    /**
     * 获取报告状态
     * @param webClient
     * @param reportId
     * @return
     */
    ResponseResult<SceneStatusModel> queryStatus(AbstractTakinWebClient webClient,Long reportId);

    /**
     * 获取报告的详细结果
     * @param webClient
     * @param reportId
     * @return
     */
    ResponseResult<List<ScriptNodeSummaryModel>>  queryReportSummary(AbstractTakinWebClient webClient,Long reportId);
}
