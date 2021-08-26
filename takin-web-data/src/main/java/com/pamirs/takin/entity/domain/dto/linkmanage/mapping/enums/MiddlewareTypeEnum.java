package com.pamirs.takin.entity.domain.dto.linkmanage.mapping.enums;

import lombok.Getter;

/**
 * 链路拓扑图-中间件类型
 *
 * @author fanxx
 * @date 2020/7/6 下午5:44
 */
@Getter
public enum MiddlewareTypeEnum {
    MySQL("0", "MySQL"),

    Oracle("1", "Oracle"),

    SQLServer("2", "SQL Server"),

    Cassandra("3", "Cassandra"),

    Elasticsearch("4", "Elasticsearch"),

    HBase("5", "HBase"),

    Redis("6", "Redis"),

    Memcache("7", "Memcache"),

    MongoDB("8", "MongoDB"),

    RocketMQ("9", "RocketMQ"),

    Kafka("10", "Kafka"),

    ActiveMQ("11", "ActiveMQ"),

    RabbitMQ("12", "RabbitMQ"),

    Dubbo("13", "Dubbo"),

    Other("1000", "其他");

    private String code;
    private String desc;

    MiddlewareTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
