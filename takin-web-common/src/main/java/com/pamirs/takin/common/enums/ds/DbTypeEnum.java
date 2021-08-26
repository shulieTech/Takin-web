package com.pamirs.takin.common.enums.ds;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author fanxx
 * @date 2020/11/26 4:56 下午
 */
@AllArgsConstructor
@Getter
public enum DbTypeEnum {
    /**
     * 数据库
     */
    DB(0, "数据库"),

    /**
     * 缓存
     */
    CACHE(1, "缓存"),

    /**
     * 影子Server
     */
    ES_SERVER(2, "搜索引擎（ES）"),

    /**
     * 影子集群
     */
    HBASE_SERVER(3, "数据库(HBase)"),

    /**
     * kafka
     */
    KAFKA_SERVER(4, "消息队列(Kafka)");

    private final Integer code;

    private final String desc;

    /**
     * 根据 desc 获得枚举
     * @param desc desc
     * @return 枚举
     */
    public static DbTypeEnum getEnumByDesc(String desc) {
        return Arrays.stream(values())
                .filter(dbTypeEnum -> dbTypeEnum.getDesc().equals(desc))
                .findFirst().orElse(null);
    }

    /**
     * 根据 code 获得枚举
     * @param code code
     * @return 枚举
     */
    public static DbTypeEnum getEnumByCode(Integer code) {
        return Arrays.stream(values())
                .filter(dbTypeEnum -> dbTypeEnum.getCode().equals(code))
                .findFirst().orElse(null);
    }

    /**
     * 根据 code, 获得描述
     * @param code code
     * @return 描述
     */
    public static String getDescByCode(Integer code) {
        return Arrays.stream(values())
                .filter(dbTypeEnum -> dbTypeEnum.getCode().equals(code))
                .findFirst()
                .map(DbTypeEnum::getDesc).orElse("");
    }

}
