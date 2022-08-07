package io.shulie.takin.web.biz.engine.selector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public enum EngineSelectorStrategy {

    RANDOM("random", "随机"),
    PRIVATE_PRIORITY("private_priority", "私网优先(不强制私网类型)"),
    COMMON_PRIORITY("common_priority", "公网优先(不强制公网类型)");

    private final String type;
    private final String desc;

    EngineSelectorStrategy(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    private static final Map<String, EngineSelectorStrategy> ALL = new HashMap<>(8);

    static {
        Arrays.stream(values()).forEach(strategy -> ALL.put(strategy.getType(), strategy));
    }

    public static EngineSelectorStrategy of(String type) {
        return ALL.getOrDefault(type, EngineSelectorStrategy.PRIVATE_PRIORITY);
    }
}
