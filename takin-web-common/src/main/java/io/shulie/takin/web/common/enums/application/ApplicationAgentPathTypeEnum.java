package io.shulie.takin.web.common.enums.application;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * @Description 探针根目录类型枚举
 * @Author 南风
 * @Date 2021/11/10 16:24
 */
@AllArgsConstructor
@Getter
public enum ApplicationAgentPathTypeEnum {
    OSS(0, "OSS"),
    FTP(1, "FTP"),
    NGINX(2, "Nginx"),
    ;

    private Integer val;
    private String desc;

    private static final Map<Integer, ApplicationAgentPathTypeEnum> INSTANCES = Maps.newHashMap();
    private static final Map<String, ApplicationAgentPathTypeEnum> DESC_INSTANCES = Maps.newHashMap();

    static {
        for (ApplicationAgentPathTypeEnum pathTypeEnum : ApplicationAgentPathTypeEnum.values()) {
            INSTANCES.put(pathTypeEnum.getVal(), pathTypeEnum);
        }
        for (ApplicationAgentPathTypeEnum pathTypeEnum : ApplicationAgentPathTypeEnum.values()) {
            DESC_INSTANCES.put(pathTypeEnum.getDesc(), pathTypeEnum);
        }
    }

    public static ApplicationAgentPathTypeEnum getEnumByDesc(String desc) {
        if(!DESC_INSTANCES.containsKey(desc)){
            return null;
        }
        return DESC_INSTANCES.get(desc);
    }

    public static ApplicationAgentPathTypeEnum getEnumByVal(Integer val) {
        if(!INSTANCES.containsKey(val)){
            return null;
        }
        return INSTANCES.get(val);
    }
}
