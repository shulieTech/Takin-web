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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * @author liyuanba
 * @date 2021/9/24 4:31 下午
 */
@Slf4j
public class NumberUtil {
    public static <T> double maxDouble(Collection<T> list, Function<T, Double> func) {
        return maxDouble(list, func, 0d);
    }
    public static <T> Double maxDouble(Collection<T> list, Function<T, Double> func, Double defValue) {
        if (CollectionUtils.isEmpty(list)) {
            return defValue;
        }
        OptionalDouble maxOpt = list.stream().filter(Objects::nonNull)
                .map(func)
                .filter(Objects::nonNull)
                .mapToDouble(d -> d)
                .max();
        return maxOpt.isPresent() ? maxOpt.getAsDouble() : defValue;
    }

    public static <T> int maxInt(Collection<T> list, Function<T, Integer> func) {
        return maxInt(list, func, 0);
    }
    public static <T> Integer maxInt(Collection<T> list, Function<T, Integer> func, Integer defValue) {
        if (CollectionUtils.isEmpty(list)) {
            return defValue;
        }
        OptionalInt maxOpt = list.stream().filter(Objects::nonNull)
                .map(func)
                .filter(Objects::nonNull)
                .mapToInt(d -> d)
                .max();
        return maxOpt.isPresent() ? maxOpt.getAsInt() : defValue;
    }

    public static <T> double minDouble(Collection<T> list, Function<T, Double> func) {
        return maxDouble(list, func, 0d);
    }
    public static <T> Double minDouble(Collection<T> list, Function<T, Double> func, Double defValue) {
        if (CollectionUtils.isEmpty(list)) {
            return defValue;
        }
        OptionalDouble minOpt = list.stream().filter(Objects::nonNull)
                .map(func)
                .filter(Objects::nonNull)
                .mapToDouble(d -> d)
                .min();
        return minOpt.isPresent() ? minOpt.getAsDouble() : defValue;
    }

    public static <T> int minInt(Collection<T> list, Function<T, Integer> func) {
        return minInt(list, func, 0);
    }
    public static <T> Integer minInt(Collection<T> list, Function<T, Integer> func, Integer defValue) {
        if (CollectionUtils.isEmpty(list)) {
            return defValue;
        }
        OptionalInt minOpt = list.stream().filter(Objects::nonNull)
                .map(func)
                .filter(Objects::nonNull)
                .mapToInt(d -> d)
                .min();
        return minOpt.isPresent() ? minOpt.getAsInt() : defValue;
    }
    /**
     * 从list中对某个字段的数字进行累加
     * 区别CommUtil.sum方法，一个是返回默认值0，一个返回null
     */
    public static <T> int sum(Collection<T> list, Function<T, Integer> func) {
        return sum(list, func, 0);
    }

    public static <T> Integer sum(Collection<T> list, Function<T, Integer> func, Integer defValue) {
        if (CollectionUtils.isEmpty(list)) {
            return defValue;
        }
        return list.stream().filter(Objects::nonNull)
                .map(func)
                .filter(Objects::nonNull)
                .mapToInt(d -> d)
                .sum();
    }

    public static <T> Long sumLong(Collection<T> list, Function<T, Long> func) {
        return sumLong(list, func, 0L);
    }

    public static <T> Long sumLong(Collection<T> list, Function<T, Long> func, Long defValue) {
        if (CollectionUtils.isEmpty(list)) {
            return defValue;
        }
        return list.stream().filter(Objects::nonNull)
                .map(func)
                .filter(Objects::nonNull)
                .mapToLong(d -> d)
                .sum();
    }

    public static <T> Double sumDouble(Collection<T> list, Function<T, Double> func) {
        return sumDouble(list, func, 0d);
    }

    public static <T> Double sumDouble(Collection<T> list, Function<T, Double> func, Double devValue) {
        if (CollectionUtils.isEmpty(list)) {
            return devValue;
        }
        return list.stream().filter(Objects::nonNull)
                .map(func)
                .filter(Objects::nonNull)
                .mapToDouble(d -> d)
                .sum();
    }

    public static int sum(Integer a, Integer b) {
        if (null == a) {
            a = 0;
        }
        if (null == b) {
            b = 0;
        }
        return a+b;
    }

    public static int parseInt(String s) {
        return parseInt(s, 0);
    }

    public static Integer parseInt(String s, Integer defValue) {
        if (StringUtils.isBlank(s) || !StringUtils.isNumeric(s)) {
            return defValue;
        }
        if (null != defValue) {
            return NumberUtils.toInt(s, defValue);
        } else {
            int a = NumberUtils.toInt(s, Integer.MIN_VALUE);
            if (a == Integer.MIN_VALUE) {
                return defValue;
            }
            return a;
        }
    }

    public static long parseLong(String s) {
        return parseLong(s, 0L);
    }

    public static Long parseLong(String s, Long defValue) {
        if (StringUtils.isBlank(s) || !StringUtils.isNumeric(s)) {
            return defValue;
        }
        if (null != defValue) {
            return NumberUtils.toLong(s, defValue);
        } else {
            long a = NumberUtils.toLong(s, Long.MAX_VALUE);
            if (a == Long.MAX_VALUE) {
                return defValue;
            }
        }
        return 0L;
    }

    /**
     * 计算a在b中的比例
     */
    public static double getRate(Number a, Number b) {
        return getRate(a, b, 0d);
    }

    public static double getRate(Number a, Number b, double defValue) {
        return getRate(a, b, false, defValue, 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 计算a在b中的百分比
     */
    public static double getPercentRate(Number a, Number b) {
        return getPercentRate(a, b, 0d);
    }

    public static double getPercentRate(Number a, Number b, double defValue) {
        return getRate(a, b, true, defValue, 2, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * 计算a在b中的比例
     *
     * @param a         被除数
     * @param b         除数
     * @param percent   是否是百分比
     * @param defValue  默认值
     * @param scale     小数点后精度, null表示不做精度取舍
     * @param roundMode 小数取整的模式，默认四舍五入
     * @return a在b中的比例
     */
    public static Double getRate(Number a, Number b, boolean percent, Double defValue, Integer scale, Integer roundMode) {
        try {
            if (null == b || isZero(b)) {
                return defValue;
            }
            if (null == a) {
                return defValue;
            }
            BigDecimal aa = BigDecimal.valueOf(a.doubleValue());
            BigDecimal bb = BigDecimal.valueOf(b.doubleValue());
            if (percent) {
                aa = aa.multiply(new BigDecimal(100));
            }
            if (null == roundMode) {
                roundMode = BigDecimal.ROUND_HALF_UP;
            }
            if (null == scale) {
                return aa.divide(bb, roundMode).doubleValue();
            }
            return aa.divide(bb, scale, roundMode).doubleValue();
        } catch (Exception e) {
            log.error("getRate error!a=" + a + ", b=" + b, e);
        }
        return defValue;
    }

    public static boolean isZero(Number a) {
        return isZero(a.doubleValue());
    }

    public static boolean isZero(double a) {
        BigDecimal zero = new BigDecimal(0);
        BigDecimal aa = new BigDecimal(a);
        return aa.compareTo(zero) == 0;
    }

    public static String decimalToString(BigDecimal decimal, int scale, RoundingMode roundingMode) {
        if (Objects.isNull(decimal)) {
            return "";
        }
        return decimal.setScale(scale, roundingMode).toString();
    }

    public static String decimalToString(BigDecimal decimal) {
        return decimalToString(decimal, 2, RoundingMode.HALF_DOWN);
    }

    public static void main(String[] args) {
        System.out.println("isZero=" + decimalToString(null));
        System.out.println("getRate=" + getRate(1, 0.00000d));
    }
}
