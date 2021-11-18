package io.shulie.takin.web.common.enums.application;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * @Description 探针根目录配置有效性枚举
 * @Author 南风
 * @Date 2021/11/10 16:24
 */
@AllArgsConstructor
@Getter
public enum ApplicationAgentPathValidStatusEnum {
    TO_BE_CHECKED(0, "待检查",null),
    CHECK_PASSED(2, "检查通过",true),
    CHECK_FAILED(1, "检查失败",false),
    ;

    private Integer val;
    private String desc;
    private Boolean state;


    private static final Map<Integer, ApplicationAgentPathValidStatusEnum> INSTANCES = Maps.newHashMap();
    private static final Map<String, ApplicationAgentPathValidStatusEnum> DESC_INSTANCES = Maps.newHashMap();
    private static final Map<Boolean, ApplicationAgentPathValidStatusEnum> STATE_INSTANCES = Maps.newHashMap();

    static {
        for (ApplicationAgentPathValidStatusEnum pathTypeEnum : ApplicationAgentPathValidStatusEnum.values()) {
            INSTANCES.put(pathTypeEnum.getVal(), pathTypeEnum);
        }
        for (ApplicationAgentPathValidStatusEnum pathTypeEnum : ApplicationAgentPathValidStatusEnum.values()) {
            DESC_INSTANCES.put(pathTypeEnum.getDesc(), pathTypeEnum);
        }
        for (ApplicationAgentPathValidStatusEnum pathTypeEnum : ApplicationAgentPathValidStatusEnum.values()) {
            STATE_INSTANCES.put(pathTypeEnum.getState(), pathTypeEnum);
        }
    }

    public static ApplicationAgentPathValidStatusEnum getEnumByDesc(String desc) {
        if(!DESC_INSTANCES.containsKey(desc)){
            return null;
        }
        return DESC_INSTANCES.get(desc);
    }

    public static ApplicationAgentPathValidStatusEnum getEnumByVal(Integer val) {
        if(!INSTANCES.containsKey(val)){
            return null;
        }
        return INSTANCES.get(val);
    }

    public static Integer getValByState(Boolean state) {
        if(!STATE_INSTANCES.containsKey(state)){
            return null;
        }
        return INSTANCES.get(state).getVal();
    }
}
