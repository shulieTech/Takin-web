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
public enum AppRemoteCallTypeV2Enum {
    /**
     * HTTP
     */
    HTTP(0, "http",AppRemoteCallTypeTemplateEnum.HTTP),
    /**
     * DUBBO
     */
    DUBBO(1, "dubbo",AppRemoteCallTypeTemplateEnum.RPC),

    /**
     * FEIGN
     */
    FEIGN(2, "feign",AppRemoteCallTypeTemplateEnum.RPC),

    /**
     * GRPC
     */
    GRPC(3,"grpc",AppRemoteCallTypeTemplateEnum.RPC);

    private Integer type;
    private String desc;
    private Enum parentEnum;


    private static final Map<Integer, AppRemoteCallTypeV2Enum> INSTANCES = Maps.newHashMap();
    private static final Map<String, AppRemoteCallTypeV2Enum> DESC_INSTANCES = Maps.newHashMap();

    static {
        for (AppRemoteCallTypeV2Enum callConfigEnum : AppRemoteCallTypeV2Enum.values()) {
            INSTANCES.put(callConfigEnum.getType(), callConfigEnum);
        }
        for (AppRemoteCallTypeV2Enum callConfigEnum : AppRemoteCallTypeV2Enum.values()) {
            DESC_INSTANCES.put(callConfigEnum.getDesc(), callConfigEnum);
        }
    }

    public static AppRemoteCallTypeV2Enum getEnum(Integer type) {
        return INSTANCES.get(type);
    }

    public static AppRemoteCallTypeV2Enum getEnumByDesc(String desc) {
        return DESC_INSTANCES.get(desc);
    }


}
