package com.pamirs.takin.common.constant;

/**
 * 说明：时间单位枚举类
 *
 * @author shulie
 * @version 1.0
 * @date 2017年12月13日
 */
public enum TimeUnits {

    YEAR,
    MONTH,
    DAY,
    HOUR,
    MINUTES,
    SECOND;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
