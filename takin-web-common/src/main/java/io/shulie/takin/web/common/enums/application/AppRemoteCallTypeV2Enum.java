package io.shulie.takin.web.common.enums.application;

import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author 南风
 * @date 2021/8/25 3:16 下午
 * 新老版本接口类型枚举映射
 */
@AllArgsConstructor
@Getter
public enum AppRemoteCallTypeV2Enum {
    /**
     * HTTP
     */
    HTTP_3(0, "httpclient3",AppRemoteCallTypeEnum.HTTP),

    HTTP_4(0, "httpclient4",AppRemoteCallTypeEnum.HTTP),

    JDK_HTTP(0, "jdk-http",AppRemoteCallTypeEnum.HTTP),

    ASYNC_HTTP(0, "async-httpclient",AppRemoteCallTypeEnum.HTTP),

    GOOGLE_HTTP(0, "google-httpclient",AppRemoteCallTypeEnum.HTTP),

    HTTP(0,"HTTP",AppRemoteCallTypeEnum.HTTP),

    OK_HTTP(0, "ok-http",AppRemoteCallTypeEnum.HTTP),

    /**
     * DUBBO
     */
    DUBBO(1, "dubbo",AppRemoteCallTypeEnum.DUBBO),

    /**
     * DUBBO
     */
    DUBBO_OLD(1, "DUBBO",AppRemoteCallTypeEnum.DUBBO),

    /**
     * FEIGN
     */
    FEIGN(2, "feign",AppRemoteCallTypeEnum.FEIGN),

    /**
     * FEIGN
     */
    FEIGN_OLD(2, "FEIGN",AppRemoteCallTypeEnum.FEIGN),

    /**
     * GRPC
     */
    GRPC_OLD(3,"GRPC",AppRemoteCallTypeEnum.GRPC),
    GRPC(3,"grpc",AppRemoteCallTypeEnum.GRPC);



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
        if(!DESC_INSTANCES.containsKey(desc)){
            return null;
        }
        return DESC_INSTANCES.get(desc);
    }


}
