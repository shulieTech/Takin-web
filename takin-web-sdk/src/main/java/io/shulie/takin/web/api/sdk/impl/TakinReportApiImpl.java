package io.shulie.takin.web.api.sdk.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

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
import org.springframework.util.CollectionUtils;

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
    public ResponseResult<List<ScriptNodeSummaryModel>> queryReportSummary(AbstractTakinWebClient webClient, Long reportId) {
        final Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("reportId", reportId);
        final String withBody = webClient.executeWithBody(
            new TakinWebRequest(webClient.webUrl + "/" + RemoteUrls.QUERY_REPORT_RESULT, dataMap, Method.GET));
        log.debug("queryReportSummary response:{}", withBody);
        final ResponseResult result = JSON.parseObject(withBody, ResponseResult.class);
        final JSONObject parseObject = JSON.parseObject(JSON.toJSONString(result.getData()));
        if (parseObject != null) {
            final JSONArray summaryBeans = parseObject.getJSONArray("scriptNodeSummaryBeans");
            if (!CollectionUtils.isEmpty(summaryBeans)) {
                final List<ScriptNodeSummaryModel> summaryModel = summaryBeans.toJavaList(ScriptNodeSummaryModel.class);
                result.setData(summaryModel);
            }
        }
        return result;
    }
}
