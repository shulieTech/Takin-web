package io.shulie.takin.web.common.enums.application;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

/**
 * @author 南风
 * @date 2021/8/25 3:16 下午
 */
@AllArgsConstructor
@Getter
public enum AppRemoteCallTypeTemplateEnum {
    /**
     * HTTP
     */
    HTTP(0, "http"),
    /**
     * RPC
     */
    RPC(1, "rpc");

    private Integer type;
    private String desc;


    private static final Map<Integer, AppRemoteCallTypeTemplateEnum> INSTANCES = Maps.newHashMap();
    private static final Map<String, AppRemoteCallTypeTemplateEnum> DESC_INSTANCES = Maps.newHashMap();

    static {
        for (AppRemoteCallTypeTemplateEnum callConfigEnum : AppRemoteCallTypeTemplateEnum.values()) {
            INSTANCES.put(callConfigEnum.getType(), callConfigEnum);
        }
        for (AppRemoteCallTypeTemplateEnum callConfigEnum : AppRemoteCallTypeTemplateEnum.values()) {
            DESC_INSTANCES.put(callConfigEnum.getDesc(), callConfigEnum);
        }
    }

    public static AppRemoteCallTypeTemplateEnum getEnum(Integer type) {
        return INSTANCES.get(type);
    }

    public static AppRemoteCallTypeTemplateEnum getEnumByDesc(String desc) {
        return DESC_INSTANCES.get(desc);
    }


}
