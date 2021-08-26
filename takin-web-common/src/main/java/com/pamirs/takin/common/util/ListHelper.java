package com.pamirs.takin.common.util;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Function;

import com.alibaba.fastjson.JSON;

import com.google.common.collect.Maps;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;

/**
 * 工具类
 *
 * @author qianshui
 * @date 2019-06-21 15:40
 */
public class ListHelper {

    public static <T, K, V> Map<K, V> transferToMap(List<T> list, Function<T, K> keyFunc, Function<T, V> valueFunc) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.EMPTY_MAP;
        }
        Map<K, V> result = Maps.newLinkedHashMap();
        for (T t : list) {
            K key = keyFunc.apply(t);
            V value = valueFunc.apply(t);
            result.put(key, value);
        }
        return result;
    }

    public static <T, K, V> Map<K, List<V>> transferToListMap(List<T> list, Function<T, K> keyFunc,
        Function<T, V> valueFunc) {
        Map<K, List<V>> result = Maps.newLinkedHashMap();
        for (T t : list) {
            K key = keyFunc.apply(t);
            V value = valueFunc.apply(t);
            List<V> values = result.computeIfAbsent(key, k -> Lists.newArrayList());
            values.add(value);
        }
        return result;
    }

    /**
     * copyList
     *
     * @param list   -
     * @param tClass -
     * @return java.util.List
     * @author ZhangXT
     * @date 2019/6/11 20:13
     */
    public static <T> List<T> copyList(List<T> list, Class<T> tClass) {
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return JSON.parseArray(JSON.toJSONString(list), tClass);
    }

    public static String[] listToArray(List<String> list) {
        if (CollectionUtils.isEmpty(list)) {
            return new String[] {};
        }
        return list.toArray(new String[list.size()]);
    }
}
