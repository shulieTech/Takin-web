package com.pamirs.takin.common.enums.ds;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author fanxx
 * @date 2020/11/26 4:57 下午
 */
@AllArgsConstructor
public enum DsTypeEnum {

    /**
     * 影子库
     */
    SHADOW_DB(0, "影子库"),

    /**
     * 影子表
     */
    SHADOW_TABLE(1, "影子表"),

    /**
     * 影子server
     */
    SHADOW_REDIS_SERVER(2, "影子server"),

    /**
     * ES影子server集群
     */
    SHADOW_ES_SERVER(3, "影子集群"),

    /**
     * Hbase 影子server集群
     */
    SHADOW_HBASE_SERVER(4, "影子集群"),

    /**
     *kafka 影子kafka集群
     */
    SHADOW_KAFKA_CLUSTER(5, "影子kafka集群"),


    SHADOW_REDIS_KEY(6, "影子key"),

    SHADOW_REDIS_CLUSTER(7, "影子集群");

    @Getter
    private final Integer code;

    @Getter
    private final String desc;

    /**
     * 根据 desc 获得枚举
     * @param desc desc
     * @return 枚举
     */
    public static DsTypeEnum getEnumByDesc(String desc) {
        return Arrays.stream(values())
                .filter(dsTypeEnum -> dsTypeEnum.getDesc().equals(desc))
                .findFirst().orElse(null);
    }

    /**
     * 根据 code 获得枚举
     * @param code code
     * @return 枚举
     */
    public static DsTypeEnum getEnumByCode(Integer code) {
        return Arrays.stream(values())
                .filter(dsTypeEnum -> dsTypeEnum.getCode().equals(code))
                .findFirst().orElse(null);
    }

    /**
     * 根据 code, 获得描述
     * @param code code
     * @return 描述
     */
    public static String getDescByCode(Integer code) {
        return Arrays.stream(values())
                .filter(dsTypeEnum -> dsTypeEnum.getCode().equals(code))
                .findFirst()
                .map(DsTypeEnum::getDesc).orElse("");
    }

}
