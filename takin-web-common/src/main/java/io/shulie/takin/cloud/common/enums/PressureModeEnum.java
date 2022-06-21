package io.shulie.takin.cloud.common.enums;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

/**
 * @author shiyajian
 * create: 2020-07-30
 */
public enum PressureModeEnum {

    /**
     * 自定义模式，用于调试，巡检等
     */
    CUS("cus", 0),
    /**
     * 固定模式
     */
    FIXED("fixed", 1),

    /**
     * 线性增长
     */
    LINEAR("linear", 2),

    /**
     * 阶梯增长
     */
    STAIR("stair", 3);

    /**
     * 名称
     */
    @Getter
    private final String text;

    /**
     * 编码
     */
    @Getter
    private final int code;

    PressureModeEnum(String text, int code) {
        this.text = text;
        this.code = code;
    }

    public boolean equals(Integer code) {
        PressureModeEnum mode = PressureModeEnum.value(code);
        return this == mode;
    }

    private static final Map<Integer, PressureModeEnum> pool = new HashMap<>();

    static {
        for (PressureModeEnum e : PressureModeEnum.values()) {
            pool.put(e.getCode(), e);
        }
    }

    public static PressureModeEnum value(Integer code) {
        if (null == code) {
            return null;
        }
        return pool.get(code);
    }

}
