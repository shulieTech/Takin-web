package io.shulie.takin.web.biz.service.interfaceperformance.impl;

import com.google.common.collect.Maps;
import io.shulie.takin.cloud.ext.content.enginecall.ThreadGroupConfigExt;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceConfigCreateInput;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PerformanceDataFileRequest;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.PressureConfigRequest;
import io.shulie.takin.web.biz.pojo.request.scene.NewSceneRequest;
import io.shulie.takin.web.biz.pojo.request.scene.SceneDetailResponse;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * @Author: vernon
 * @Date: 2022/5/26 01:05
 * @Description:
 */
public class PerformancePressureAdaptor {


    public NewSceneRequest.BasicInfo buildBaseInfo(SceneDetailResponse in) {
        if (in == null) {
            return null;
        }
        NewSceneRequest.BasicInfo basicInfo = new NewSceneRequest.BasicInfo();
       /* basicInfo.setSceneId(in.getBasicInfo().getSceneId());*/
        basicInfo.setName(in.getBasicInfo().getName());
        basicInfo.setBusinessFlowId(in.getBasicInfo().getBusinessFlowId());
        basicInfo.setExecuteTime(null);
        basicInfo.setIsScheduler(in.getBasicInfo().getIsScheduler());
        return basicInfo;
    }

    public NewSceneRequest.PtConfig buildPtConfig(SceneDetailResponse in) {
        if (in == null) {
            return null;
        }
        NewSceneRequest.PtConfig ptConfig = new NewSceneRequest.PtConfig();
        ptConfig.setDuration(in.getConfig().getDuration());
        ptConfig.setEstimateFlow(in.getConfig().getEstimateFlow());
        ptConfig.setPodNum(in.getConfig().getPodNum());
        ptConfig.setThreadGroupConfigMap(convertThreadGroupConfig(in.getConfig().getThreadGroupConfigMap()));
        ptConfig.setUnit(in.getConfig().getUnit());
        return ptConfig;
    }

    private Map<String, NewSceneRequest.ThreadGroupConfig> convertThreadGroupConfig(Map<String, ThreadGroupConfigExt> in) {
        if (Objects.isNull(in) || in.isEmpty()) {
            return null;
        }
        Map<String, NewSceneRequest.ThreadGroupConfig> returnObj = Maps.newHashMap();
        Iterator<Map.Entry<String, ThreadGroupConfigExt>> iterator = in.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ThreadGroupConfigExt> entry = iterator.next();
            String key = entry.getKey();
            ThreadGroupConfigExt source = entry.getValue();
            NewSceneRequest.ThreadGroupConfig target = new NewSceneRequest.ThreadGroupConfig();
            target.setEstimateFlow(source.getEstimateFlow());
            target.setMode(source.getMode());
            target.setRampUp(source.getRampUp());
            target.setThreadNum(source.getThreadNum());
            target.setSteps(source.getSteps());
            target.setType(source.getType());
            target.setRampUpUnit(source.getRampUpUnit());
            returnObj.put(key, target);
        }
        return returnObj;
    }

}
