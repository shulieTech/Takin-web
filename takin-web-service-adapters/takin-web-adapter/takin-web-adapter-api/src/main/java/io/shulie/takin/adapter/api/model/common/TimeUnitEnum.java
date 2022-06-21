package io.shulie.takin.adapter.api.model.common;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

/**
 * @author qianshui
 * @date 2020/5/11 下午7:58
 */
public enum TimeUnitEnum {
    /**
     * 天
     */
    DAY(TimeUnit.DAYS, "d", "天"),
    /**
     * 时
     */
    HOUR(TimeUnit.HOURS, "h", "时"),
    /**
     * 分
     */
    MINUTE(TimeUnit.MINUTES, "m", "分"),
    /**
     * 秒
     */
    SECOND(TimeUnit.SECONDS, "s", "秒");

    @Getter
    private final TimeUnit unit;
    @Getter
    private final String value;
    @Getter
    private final String name;

    TimeUnitEnum(TimeUnit unit, String value, String name) {
        this.unit = unit;
        this.value = value;
        this.name = name;
    }

    private static final Map<String, TimeUnitEnum> POOL = new HashMap<>();

    static {
        for (TimeUnitEnum e : TimeUnitEnum.values()) {
            POOL.put(e.value.toLowerCase(), e);
        }
    }

    public static TimeUnitEnum value(String value) {
        if (StrUtil.isBlank(value)) {return null;}
        return POOL.get(value.toLowerCase());
    }

}
