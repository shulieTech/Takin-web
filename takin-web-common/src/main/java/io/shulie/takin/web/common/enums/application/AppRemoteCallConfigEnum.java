package io.shulie.takin.web.common.enums.application;

import java.util.Map;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 无涯
 * @date 2021/5/29 1:37 上午
 */
@AllArgsConstructor
@Getter
public enum AppRemoteCallConfigEnum {
    /**
     * 未配置
     */
    CLOSE_CONFIGURATION(0, "未配置", ""),
    /**
     * 白名单
     */
    OPEN_WHITELIST(1, "白名单", "white"),
    /**
     * 返回值mock
     */
    RETURN_MOCK(2, "返回值mock", "mock"),
    /**
     * 转发mock
     */
    FORWARD_MOCK(3, "转发mock", "forward");
    private Integer type;
    private String configName;
    /**
     * mock
     * forward
     * white
     */
    private String agentCheckType;


    private static final Map<Integer, AppRemoteCallConfigEnum> INSTANCES = Maps.newHashMap();

    static {
        for (AppRemoteCallConfigEnum callConfigEnum : AppRemoteCallConfigEnum.values()) {
            INSTANCES.put(callConfigEnum.getType(), callConfigEnum);
        }
    }

    public static AppRemoteCallConfigEnum getEnum(Integer type) {
        return INSTANCES.get(type);
    }

}
