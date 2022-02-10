package io.shulie.takin.web.api.sdk;

import io.shulie.takin.web.api.client.AbstractTakinWebClient;
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
    ResponseResult queryStatus(AbstractTakinWebClient webClient,Long reportId);

    /**
     * 获取报告的详细结果
     * @param webClient
     * @param reportId
     * @return
     */
    ResponseResult queryReportSummary(AbstractTakinWebClient webClient,Long reportId);
}
