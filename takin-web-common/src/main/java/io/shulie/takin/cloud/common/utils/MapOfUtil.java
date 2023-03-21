package io.shulie.takin.cloud.common.utils;

import java.util.HashMap;
import java.util.Map;

public class MapOfUtil {

    public static Map<String, String> of(String key, String value){
        Map<String, String> dataMap = new HashMap<>();
        dataMap.put(key, value);
        return dataMap;
    }
}
