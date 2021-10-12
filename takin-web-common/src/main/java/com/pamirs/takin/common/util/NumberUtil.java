package com.pamirs.takin.common.util;

import java.math.BigDecimal;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * 数字相关的工具类
 *
 * @author shulie
 * @version v1.0
 * @date 2018年5月21日
 */
public class NumberUtil {

    /**
     * BigDecimal 转long
     *
     * @param bigDecimal 要转换的bigDecimal
     * @return long类型结果
     */
    public static long getLong(BigDecimal bigDecimal) {
        return bigDecimal == null ? 0L : bigDecimal.longValue();
    }

    /**
     * BigDecimal 转 float
     *
     * @param bigDecimal -
     * @return -
     */
    public static float getFloat(BigDecimal bigDecimal) {
        return bigDecimal == null ? 0f : bigDecimal.floatValue();
    }

    /**
     * String 转 float
     *
     * @param str -
     * @return -
     */
    public static float getFloat(String str) {

        return NumberUtils.isCreatable(str) ? 0f : Float.parseFloat(str);
    }

    public static Double transStrToDouble(String str) {
        return NumberUtils.isCreatable(str) ? 0 : Double.parseDouble(str);
    }

}
