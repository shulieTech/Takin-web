package io.shulie.takin.cloud.common.enums;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhaoyong
 */
@Getter
@AllArgsConstructor
public enum PressureSceneEnum {
    /**
     * 常规模式
     */
    DEFAULT(0, "常规模式"),

    /**
     * 流量调试
     */
    FLOW_DEBUG(3, "流量调试"),
    /**
     * 巡检模式
     */
    INSPECTION_MODE(4, "巡检模式"),
    /**
     * 试跑模式
     */
    TRY_RUN(5, "试跑模式");

    private final Integer code;
    private final String description;

    private static final Map<Integer, PressureSceneEnum> INSTANCES = new HashMap<>();

    static {
        for (PressureSceneEnum e : PressureSceneEnum.values()) {
            INSTANCES.put(e.getCode(), e);
        }
        //为了兼容老版本数据，将1，2转化为常规模式
        INSTANCES.put(1, DEFAULT);
        INSTANCES.put(2, DEFAULT);
    }

    /**
     * 转换
     *
     * @param code code
     * @return 枚举值
     */
    public static PressureSceneEnum of(Integer code) {
        return INSTANCES.get(code);
    }
}
