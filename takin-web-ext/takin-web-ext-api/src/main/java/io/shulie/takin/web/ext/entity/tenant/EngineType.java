package io.shulie.takin.web.ext.entity.tenant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

/**
 * 集群模式
 */
@Getter
public enum EngineType {

    COMMON(0, "公网"),
    PRIVATE(1, "私网");

    private final int type;
    private final String name;

    EngineType(int type, String name) {
        this.type = type;
        this.name = name;
    }

    private static final Map<Integer, EngineType> ALL = new HashMap<>(4);

    static {
        Arrays.stream(values()).forEach(val -> ALL.put(val.getType(), val));
    }

    public static EngineType of(Integer type) {
        return ALL.get(type);
    }
}
