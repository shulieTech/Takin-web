package io.shulie.takin.web.api.sdk.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.hutool.http.Method;
import io.shulie.takin.web.api.client.AbstractTakinWebClient;
import io.shulie.takin.web.api.constant.RemoteUrls;
import io.shulie.takin.web.api.model.SceneGoalModel;
import io.shulie.takin.web.api.model.SceneStatusModel;
import io.shulie.takin.web.api.request.TakinWebRequest;
import io.shulie.takin.web.api.request.TakinWebSceneRequest;
import io.shulie.takin.web.api.response.ResponseResult;
import io.shulie.takin.web.api.response.SceneDetailResponse;
import io.shulie.takin.web.api.sdk.TakinSceneApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @author caijianying
 */
@Slf4j
@Service
public class TakinSceneApiImpl implements TakinSceneApi {

    @Override
    public ResponseResult getSceneListPage(AbstractTakinWebClient webClient, TakinWebSceneRequest request) {
        final Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("current", request.getPageNo());
        dataMap.put("pageSize", request.getPageSize());
        dataMap.put("sceneName", request.getSceneName());
        dataMap.put("sceneId", request.getSceneId());
        final String withBody = webClient.execute(
            new TakinWebRequest(webClient.webUrl + "/" + RemoteUrls.SCENE_LIST, dataMap, Method.GET));
        log.debug("getSceneListPage response:{}", withBody);
        final ResponseResult result = JSON.parseObject(withBody, ResponseResult.class);
        return result;
    }

    @Override
    public ResponseResult<SceneDetailResponse> getSceneDetail(AbstractTakinWebClient webClient, Long sceneId) {
        final Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("sceneId", sceneId);
        final String withBody = webClient.execute(
            new TakinWebRequest(webClient.webUrl + "/" + RemoteUrls.SCENE_DETAIL, dataMap, Method.GET));
        log.debug("getSceneDetail response:{}", withBody);
        final ResponseResult result = JSON.parseObject(withBody, ResponseResult.class);

        final SceneDetailResponse response = new SceneDetailResponse();
        //组装返回
        final JSONObject dataJSON = JSON.parseObject(JSON.toJSONString(result.getData()));
        final JSONObject info = dataJSON.getJSONObject("basicInfo");
        final JSONObject content = dataJSON.getJSONObject("content");
        final JSONObject goals = dataJSON.getJSONObject("goal");

        response.setSceneId(info.getLong("sceneId"));
        response.setSceneName(info.getString("name"));
        response.setScriptId(info.getLong("scriptId"));
        response.setBusinessFlowId(info.getLong("businessFlowId"));

        List<SceneGoalModel> goalModelList = new ArrayList<>();
        for (Entry<String, Object> goalEntry : goals.entrySet()) {
            final SceneGoalModel goalModel = JSON.parseObject(JSON.toJSONString(goalEntry.getValue()),
                SceneGoalModel.class);
            final JSONObject contentValue = content.getJSONObject(goalEntry.getKey());
            goalModel.setBusinessActivityId(contentValue.getLong("businessActivityId"));
            goalModel.setBusinessActivityName(contentValue.getString("name"));
            goalModelList.add(goalModel);
        }
        response.setGoals(goalModelList);
        result.setData(response);
        return result;
    }

    @Override
    public ResponseResult startTask(AbstractTakinWebClient webClient, Long sceneId) {
        final Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("sceneId", sceneId);
        final String withBody = webClient.execute(
            new TakinWebRequest(webClient.webUrl + "/" + RemoteUrls.START_TASK, dataMap, Method.POST));
        log.debug("startTask response:{}", withBody);
        final ResponseResult result = JSON.parseObject(withBody, ResponseResult.class);
        return result;
    }
}
