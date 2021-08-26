package io.shulie.takin.web.biz.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.pamirs.takin.common.constant.TakinDictTypeEnum;

/**
 * 全局变量存放地
 *
 * @author shulie
 * @description
 * @create 2018-08-18 08:50:15
 */
public class GlobalVariableUtil {

    public static ConcurrentMap<String, Object> createCacheMap() {
        return InnerConcurrentMap.cacheMap;
    }

    public static void setValue(TakinDictTypeEnum takinDictTypeEnum, Map<String, Object> map) {
        InnerConcurrentMap.cacheMap.put(takinDictTypeEnum.toString(), map);
    }

    public static Map<String, Object> getValue(TakinDictTypeEnum takinDictTypeEnum) {
        return (Map<String, Object>)InnerConcurrentMap.cacheMap.get(takinDictTypeEnum.toString());
    }

    public static class InnerConcurrentMap {
        public static ConcurrentMap<String, Object> cacheMap = new ConcurrentHashMap<>();
    }
}
