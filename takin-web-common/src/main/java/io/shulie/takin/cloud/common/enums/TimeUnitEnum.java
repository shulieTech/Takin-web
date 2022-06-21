package io.shulie.takin.cloud.common.enums;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

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
    private TimeUnit unit;
    @Getter
    private String value;
    @Getter
    private String name;

    TimeUnitEnum(TimeUnit unit, String value, String name) {
        this.unit = unit;
        this.value = value;
        this.name = name;
    }

    private static final Map<String, TimeUnitEnum> pool = new HashMap<>();

    static {
        for (TimeUnitEnum e : TimeUnitEnum.values()) {
            pool.put(e.value.toLowerCase(), e);
        }
    }

    public static TimeUnitEnum value(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return pool.get(value.toLowerCase());
    }

}
