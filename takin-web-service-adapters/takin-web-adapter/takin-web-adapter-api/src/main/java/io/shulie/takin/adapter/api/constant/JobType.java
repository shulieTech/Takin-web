package io.shulie.takin.adapter.api.constant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum JobType {
    INITIAL(0, "常规模式"),
    DEBUG(3, "调试模式"),
    PATROL(4, "巡检模式"),
    TRY(5, "试跑模式");

    @JsonValue
    private final Integer code;
    private final String description;
    private static final Map<Integer, JobType> CODE_MAPPING = new HashMap<>(8);

    @JsonCreator
    public static JobType of(Integer code) {
        return CODE_MAPPING.get(code);
    }

    JobType(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    static {
        Arrays.stream(values()).forEach(t -> CODE_MAPPING.put(t.getCode(), t));
    }

    public String toString() {
        return this.code + ":" + this.description;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getDescription() {
        return this.description;
    }
}