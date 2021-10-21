package io.shulie.takin.web.biz.utils;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;

/**
 * map排序工具类
 * @author fanxx
 * @date 2020/11/7 下午3:33
 */
public class MapOrderByUtils {
    /**
     * 根据map的key排序
     * @author danrenying
     * @Param: [map, isDesc: true：降序，false：升序]
     * @Return: java.util.Map<K, V>
     * @date 2020/7/13
     */
    public static <K extends Comparable<? super K>, V> Map<K, V> orderByKey(Map<K, V> map, boolean isDesc) {
        if (map == null || map.isEmpty()) {
            return new HashMap<>(1);
        }

        Map<K, V> result = Maps.newLinkedHashMap();
        if (isDesc) {
            map.entrySet().stream().sorted(Map.Entry.<K, V>comparingByKey().reversed())
                .forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        } else {
            map.entrySet().stream().sorted(Map.Entry.<K, V>comparingByKey())
                .forEachOrdered(e -> result.put(e.getKey(), e.getValue()));
        }
        return result;
    }

}
