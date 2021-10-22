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
public enum AppRemoteCallTypeEnum {
    /**
     * HTTP
     */
    HTTP(0, "HTTP",0),
    /**
     * DUBBO
     */
    DUBBO(1, "DUBBO",1),
    /**
     * FEIGN
     */
    FEIGN(2, "FEIGN",1),

    GRPC(3, "GRPC",1);

    private Integer type;
    private String desc;
    private Integer convert;


    private static final Map<Integer, AppRemoteCallTypeEnum> INSTANCES = Maps.newHashMap();
    private static final Map<String, AppRemoteCallTypeEnum> DESC_INSTANCES = Maps.newHashMap();

    static {
        for (AppRemoteCallTypeEnum callConfigEnum : AppRemoteCallTypeEnum.values()) {
            INSTANCES.put(callConfigEnum.getType(), callConfigEnum);
        }
        for (AppRemoteCallTypeEnum callConfigEnum : AppRemoteCallTypeEnum.values()) {
            DESC_INSTANCES.put(callConfigEnum.getDesc(), callConfigEnum);
        }
    }

    public static AppRemoteCallTypeEnum getEnum(Integer type) {
        return INSTANCES.get(type);
    }

    public static AppRemoteCallTypeEnum getEnumByDesc(String desc) {
        return DESC_INSTANCES.get(desc);
    }


}
