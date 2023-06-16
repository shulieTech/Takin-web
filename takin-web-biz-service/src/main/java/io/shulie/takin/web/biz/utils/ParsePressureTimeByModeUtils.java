package io.shulie.takin.web.biz.utils;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pamirs.takin.entity.domain.dto.report.PressureTestTimeDTO;
import io.shulie.takin.cloud.common.enums.PressureModeEnum;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class ParsePressureTimeByModeUtils {

    private static List<PressureTestTimeDTO> parseThreadGroupConfig2List(Date startTime, Date endTime, JSONObject jsonThreadGroupConfig) {
        List<PressureTestTimeDTO> timeList = new ArrayList<>();
        Integer mode = jsonThreadGroupConfig.getInteger("mode");
        if(mode == PressureModeEnum.FIXED.getCode()) {
            timeList.add(new PressureTestTimeDTO(startTime, endTime));
        } else if (mode == PressureModeEnum.LINEAR.getCode()) {
            //线性以分位计算单位
            Integer incrDuration = jsonThreadGroupConfig.getInteger("rampUp");
            Date calcEndTime = DateUtil.offsetMinute(startTime, incrDuration);
            if(DateUtil.offsetMinute(calcEndTime, 1).before(endTime)) {
                timeList.add(new PressureTestTimeDTO(startTime, calcEndTime));
                timeList.add(new PressureTestTimeDTO(calcEndTime, endTime));
            } else {
                timeList.add(new PressureTestTimeDTO(startTime, endTime));
            }
        } else if(mode == PressureModeEnum.STAIR.getCode()) {
            Integer allDuration = jsonThreadGroupConfig.getInteger("rampUp");
            Integer steps = jsonThreadGroupConfig.getInteger("steps");
            //阶梯以s为计算单位
            Integer incrDuration = (allDuration * 60) / steps;
            for(int i = 1; i <= steps; i++) {
                Date calcEndTime = DateUtil.offsetSecond(startTime, incrDuration);
                //计算时间超出了截止时间，返回最终数据并直接break
                if(calcEndTime.before(endTime)) {
                    if (i < steps) {
                        timeList.add(new PressureTestTimeDTO(startTime, calcEndTime));
                        startTime = calcEndTime;
                    } else {
                        timeList.add(new PressureTestTimeDTO(startTime, endTime));
                    }
                } else {
                    timeList.add(new PressureTestTimeDTO(startTime, endTime));
                    break;
                }
            }
        }
        return timeList;
    }

    public static Map<String, List<PressureTestTimeDTO>> parsePtConfig2Map(Date startTime, Date endTime, String jsonPtConfig) {
        Map<String, List<PressureTestTimeDTO>> timeMap = new HashMap<>();
        if(StringUtils.isBlank(jsonPtConfig)) {
            return timeMap;
        }
        JSONObject jsonObject = JSON.parseObject(jsonPtConfig);
        Map<String, Object> threadGroupMap = jsonObject.getObject("threadGroupConfigMap", Map.class);
        if(MapUtils.isEmpty(threadGroupMap)) {
            return timeMap;
        }
        threadGroupMap.forEach((key, value) -> {
            timeMap.put(key, parseThreadGroupConfig2List(startTime, endTime, (JSONObject) value));
        });
        return timeMap;
    }

    public static void main(String[] args) {
        String fixJson = "{\"duration\":10,\"podNum\":1,\"threadGroupConfigMap\":{\"7dae7383a28b5c45069b528a454d1164\":{\"estimateFlow\":6.0,\"mode\":1,\"rampUpUnit\":\"m\",\"threadNum\":2,\"type\":0}},\"unit\":\"m\"}";
        String lineJson = "{\"duration\":10,\"podNum\":1,\"threadGroupConfigMap\":{\"7dae7383a28b5c45069b528a454d1164\":{\"estimateFlow\":15.0,\"mode\":2,\"rampUp\":2,\"rampUpUnit\":\"m\",\"threadNum\":5,\"type\":0}},\"unit\":\"m\"}";
        String stepJson = "{\"duration\":10,\"podNum\":1,\"threadGroupConfigMap\":{\"7dae7383a28b5c45069b528a454d1164\":{\"estimateFlow\":5.0,\"mode\":3,\"rampUp\":8,\"rampUpUnit\":\"m\",\"steps\":4,\"threadNum\":8,\"type\":0}},\"unit\":\"m\"}";
        String mulThreadGroupJson = "{\"duration\":10,\"podNum\":1,\"threadGroupConfigMap\":{\"cec45d27c5e20cca29526c54b4c9ad34\":{\"estimateFlow\":20.0,\"mode\":1,\"rampUpUnit\":\"m\",\"threadNum\":2,\"type\":0},\"3e28e54a021a746688a31e176c67224f\":{\"estimateFlow\":10.0,\"mode\":2,\"rampUp\":5,\"rampUpUnit\":\"m\",\"threadNum\":10,\"type\":0}},\"unit\":\"m\"}";
        Date endTime = DateUtil.parseDateTime("2023-04-19 17:48:00");
        parsePtConfig2Map(new Date(), endTime, fixJson);
        parsePtConfig2Map(new Date(), endTime, lineJson);
        parsePtConfig2Map(new Date(), endTime, stepJson);
        parsePtConfig2Map(new Date(), endTime, mulThreadGroupJson);

        System.out.println(new Date().getTime());

    }
}
