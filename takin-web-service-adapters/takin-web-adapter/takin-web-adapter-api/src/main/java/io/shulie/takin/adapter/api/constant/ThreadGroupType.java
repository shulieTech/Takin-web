package io.shulie.takin.adapter.api.constant;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ThreadGroupType {
    CONSTANT(100, "固定", 0, 1),
    LINEAR_GROWTH(101, "线性递增", 0, 2),
    STAGE_GROWTH(102, "阶段递增", 0, 3),
    TPS(200, "TPS模式", 1, 1),
    DIY(300, "自定义", 2, 0),
    /**
     * 流量调试
     */
    FLOW_DEBUG(403, "流量调试", 3, 0),
    /**
     * 巡检模式
     */
    INSPECTION_MODE(404, "巡检模式", 4, 0),
    /**
     * 试跑模式
     */
    TRY_RUN(405, "试跑模式", 5, 0),;

    @JsonValue
    private final Integer code;
    private final String name;
    private final Integer type;
    private final Integer model;

    private static final Map<String, ThreadGroupType> TYPE_MODEL_MAPPING = new HashMap<>(16);
    private static final Map<Integer, ThreadGroupType> CODE_MAPPING = new HashMap<>(16);

    public String toString() {
        return this.code + ":" + this.name + "(" + this.type + "," + this.model + ")";
    }

    @JsonCreator
    public static ThreadGroupType of(Integer code) {
        return CODE_MAPPING.get(code);
    }

    public static ThreadGroupType of(Integer type, Integer model) {
        return TYPE_MODEL_MAPPING.get(type + "_" + model);
    }

    ThreadGroupType(Integer code, String name, Integer type, Integer model) {
        this.code = code;
        this.name = name;
        this.type = type;
        this.model = model;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

    public Integer getType() {
        return this.type;
    }

    public Integer getModel() {
        return this.model;
    }

    static {
        for (ThreadGroupType value : values()) {
            TYPE_MODEL_MAPPING.put(value.getType() + "_" + value.getModel(), value);
            CODE_MAPPING.put(value.getCode(), value);
        }
    }
}