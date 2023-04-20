package io.shulie.takin.web.biz.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.shulie.takin.cloud.common.pojo.Pair;

import java.util.*;

public class TestUtils {

    public static List<Pair<Integer, Integer>> calcCostLevelByFive(Integer minRt, Integer maxRt) {
        List<Pair<Integer, Integer>> pairList = new ArrayList<>();
        if(minRt == maxRt) {
            return pairList;
        }
        int step = Math.max(1, ((maxRt - minRt) / 5));
        for(int i = 0; i < 5; i++) {
            if(i == 4) {
                pairList.add(new Pair<>(minRt, maxRt));
            } else {
                if (minRt + step >= maxRt) {
                    pairList.add(new Pair<>(minRt, maxRt));
                    break;
                }
                pairList.add(new Pair<>(minRt, minRt + step));
                minRt += step;
            }
        }
        return pairList;
    }

    public static void main(String[] args) {
        Map<String, Object> data1Map = new HashMap<>();
        data1Map.put("startTime", "001");
        data1Map.put("rt", 1);

        Map<String, Object> data2Map = new HashMap<>();
        data2Map.put("startTime", "002");
        data2Map.put("rt", 2);

        List<Map<String, Object>> dataList = Arrays.asList(data1Map, data2Map);

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("step", dataList);
        String jsonString = JSON.toJSONString(dataMap);

        JSONObject jsonObject = JSON.parseObject(jsonString);
        JSONArray jsonArray = jsonObject.getJSONArray("step");
        for(Object obj : jsonArray) {
            JSONObject jsonObj = (JSONObject) obj;
            System.out.println(jsonObj);
        }

        //System.out.println(JSON.toJSONString(calcCostLevelByFive(0, 0)));
        System.out.println(JSON.toJSONString(calcCostLevelByFive(0, 2)));
        System.out.println(JSON.toJSONString(calcCostLevelByFive(0, 5)));
        System.out.println(JSON.toJSONString(calcCostLevelByFive(0, 8)));
    }
}
