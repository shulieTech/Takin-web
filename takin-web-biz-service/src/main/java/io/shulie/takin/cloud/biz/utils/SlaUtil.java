package io.shulie.takin.cloud.biz.utils;

import java.math.BigDecimal;
import java.util.Map;

import com.google.common.collect.Maps;
import io.shulie.takin.cloud.biz.input.scenemanage.SceneSlaRefInput;
import io.shulie.takin.cloud.common.bean.collector.SendMetricsEvent;

/**
 * @author qianshui
 * @date 2020/5/19 下午4:03
 */
public class SlaUtil {

    public static Map<String, Object> matchCondition(SceneSlaRefInput dto, SendMetricsEvent metricsEvent) {
        Map<String, Object> resultMap = Maps.newHashMap();
        Integer targetType = dto.getRule().getIndexInfo();
        switch (targetType) {
            case 0:
                resultMap.put("type", "RT");
                resultMap.put("unit", "ms");
                matchCompare(resultMap, metricsEvent.getAvgRt(), dto.getRule().getDuring(),
                    dto.getRule().getCondition());
                break;
            case 1:
                resultMap.put("type", "TPS");
                resultMap.put("unit", "");
                matchCompare(resultMap, metricsEvent.getAvgTps(), dto.getRule().getDuring(),
                    dto.getRule().getCondition());
                break;
            case 2:
                resultMap.put("type", "成功率");
                resultMap.put("unit", "%");
                matchCompare(resultMap, metricsEvent.getSuccessRate(), dto.getRule().getDuring(),
                    dto.getRule().getCondition());
                break;
            case 3:
                resultMap.put("type", "SA");
                resultMap.put("unit", "%");
                matchCompare(resultMap, metricsEvent.getSa(), dto.getRule().getDuring(), dto.getRule().getCondition());
                break;
            default:
                resultMap.put("result", false);
        }
        return resultMap;
    }

    private static void matchCompare(Map<String, Object> resultMap, Double realValue, BigDecimal goalValue,
        Integer compareType) {
        if (realValue == null) {
            realValue = 0d;
        }
        switch (compareType) {
            case 0:
                resultMap.put("compare", ">=");
                resultMap.put("real", realValue);
                resultMap.put("result", new BigDecimal(realValue).compareTo(goalValue) >= 0);
                break;
            case 1:
                resultMap.put("compare", ">");
                resultMap.put("real", realValue);
                resultMap.put("result", new BigDecimal(realValue).compareTo(goalValue) > 0);
                break;
            case 2:
                resultMap.put("compare", "=");
                resultMap.put("real", realValue);
                resultMap.put("result", new BigDecimal(realValue).compareTo(goalValue) == 0);
                break;
            case 3:
                resultMap.put("compare", "<=");
                resultMap.put("real", realValue);
                resultMap.put("result", new BigDecimal(realValue).compareTo(goalValue) <= 0);
                break;
            case 4:
                resultMap.put("compare", "<");
                resultMap.put("real", realValue);
                resultMap.put("result", new BigDecimal(realValue).compareTo(goalValue) < 0);
                break;
            default:
                resultMap.put("result", false);
        }
    }
}
