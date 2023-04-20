package io.shulie.takin.web.biz.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestUtils {

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
    }
}
