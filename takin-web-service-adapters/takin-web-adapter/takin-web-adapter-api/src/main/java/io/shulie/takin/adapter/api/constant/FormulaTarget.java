package io.shulie.takin.adapter.api.constant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum FormulaTarget {
    RT(0, "接口响应时间"),
    TPS(1, "每秒吞吐量"),
    SUCCESS_RATE(2, "成功率"),
    SA(3, "符合RT标准的比例"),
    CPU_USAGE(4,"cpu利用率"),
    MEMORY_USAGE(5,"内存使用率");

    @JsonValue
    private final Integer code;
    private final String description;
    private static final Map<Integer, FormulaTarget> TARGET_MAPPING = new HashMap<>(8);

    @JsonCreator
    public static FormulaTarget of(Integer code) {
        return TARGET_MAPPING.get(code);
    }

    FormulaTarget(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    static {
        Arrays.stream(values()).forEach(t -> TARGET_MAPPING.put(t.getCode(), t));
    }
}