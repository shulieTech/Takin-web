package io.shulie.takin.web.api.sdk.impl;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

import cn.hutool.http.Method;
import io.shulie.takin.web.api.client.AbstractTakinWebClient;
import io.shulie.takin.web.api.constant.RemoteUrls;
import io.shulie.takin.web.api.model.SceneStatusModel;
import io.shulie.takin.web.api.model.ScriptNodeSummaryModel;
import io.shulie.takin.web.api.request.TakinWebRequest;
import io.shulie.takin.web.api.response.ResponseResult;
import io.shulie.takin.web.api.sdk.TakinReportApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author caijianying
 */
@Slf4j
@Service
public class TakinReportApiImpl implements TakinReportApi {
    @Override
    public ResponseResult<SceneStatusModel> queryStatus(AbstractTakinWebClient webClient, Long reportId) {
        final Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("reportId", reportId);
        final String withBody = webClient.executeWithBody(
            new TakinWebRequest(webClient.webUrl + "/" + RemoteUrls.QUERY_REPORT_STATUS, dataMap, Method.GET));
        log.debug("queryStatus response:{}", withBody);
        final ResponseResult result = JSON.parseObject(withBody, ResponseResult.class);

        final SceneStatusModel statusModel = JSON.parseObject(JSON.toJSONString(result.getData()),
            SceneStatusModel.class);
        result.setData(statusModel);
        return result;
    }

    @Override
    public ResponseResult<ScriptNodeSummaryModel> queryReportSummary(AbstractTakinWebClient webClient, Long reportId) {
        final Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("reportId", reportId);
        final String withBody = webClient.executeWithBody(
            new TakinWebRequest(webClient.webUrl + "/" + RemoteUrls.QUERY_REPORT_RESULT, dataMap, Method.GET));
        log.debug("queryReportSummary response:{}", withBody);
        final ResponseResult result = JSON.parseObject(withBody, ResponseResult.class);
        final ScriptNodeSummaryModel summaryModel = JSON.parseObject(JSON.toJSONString(result.getData()),
            ScriptNodeSummaryModel.class);
        result.setData(summaryModel);
        return result;
    }
}
