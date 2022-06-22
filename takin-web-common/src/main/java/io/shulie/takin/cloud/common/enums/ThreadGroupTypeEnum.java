package io.shulie.takin.cloud.common.enums;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

/**
 * 线程组类型
 *
 * @author liyuanba
 * @date 2021/11/1 2:02 下午
 */
public enum ThreadGroupTypeEnum {
    CONCURRENCY(0, "并发模式"),

    TPS(1, "TPS模式"),

    CUSTOMIZE(2, "自定义模式"),
    ;
    @Getter
    private int code;
    @Getter
    private String description;

    ThreadGroupTypeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    private static final Map<Integer, ThreadGroupTypeEnum> pool = new HashMap<>();

    static {
        for (ThreadGroupTypeEnum e : ThreadGroupTypeEnum.values()) {
            pool.put(e.getCode(), e);
        }
    }

    public static ThreadGroupTypeEnum value(Integer code) {
        if (null == code) {
            return null;
        }
        return pool.get(code);
    }

    public boolean equals(Integer code) {
        ThreadGroupTypeEnum type = value(code);
        return this == type;
    }
}
