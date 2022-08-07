package io.shulie.takin.web.biz.engine.extractor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;

@Getter
public enum ExtractorType {

    PRESSURE(0, "压测"),
    TRY_RUN(1, "调试"),
    INTERFACE_PERFORMANCE(2, "takin压测"),
    ;

    private final int type;
    private final String desc;

    ExtractorType(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    private static final Map<Integer, ExtractorType> ALL = new HashMap<>(8);

    static {
        Arrays.stream(values()).forEach(type -> ALL.put(type.getType(), type));
    }

    public static ExtractorType of(int type) {
        return ALL.get(type);
    }
}
