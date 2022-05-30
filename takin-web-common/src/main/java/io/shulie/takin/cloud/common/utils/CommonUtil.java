/*
 * Copyright 2021 Shulie Technology, Co.Ltd
 * Email: shulie@shulie.io
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.shulie.takin.cloud.common.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * 公用但不好归属的静态方法
 *
 * @author liyuanba
 * @date 2021/9/27 9:12 下午
 */
public class CommonUtil {

    /**
     * 从list中对某个字段的数字进行累加
     */
    public static <T> Integer sum(Collection<T> list, Function<T, Integer> func) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.stream().filter(Objects::nonNull)
            .map(func)
            .filter(Objects::nonNull)
            .mapToInt(d -> d)
            .sum();
    }

    /**
     * 从list中取出对象某个字段的值
     */
    public static <T, R> List<R> getList(Collection<T> list, Function<T, R> func) {
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }
        return list.stream().filter(Objects::nonNull)
            .map(func)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    /**
     * 取值选择器
     *
     * @param defValue 默认值
     * @param t        取值对象
     * @param func     取值方法，从对象中取值的方法
     * @param <T>      取值对象类型
     * @param <R>      返回值对象类型
     */
    public static <T, R> R getValue(R defValue, T t, Function<T, R> func) {
        R result = defValue;
        if (null != t) {
            R r = func.apply(t);
            if (null != r) {
                if (r instanceof String) {
                    if (StringUtils.isNotBlank((String)r)) {
                        result = r;
                    }
                } else if (r instanceof List) {
                    if (CollectionUtils.isNotEmpty((List<?>)r)) {
                        result = r;
                    }
                } else if (r instanceof Map) {
                    if (MapUtils.isNotEmpty((Map<?, ?>)r)) {
                        result = r;
                    }
                } else {
                    result = r;
                }
            }
        }
        return result;
    }
}
