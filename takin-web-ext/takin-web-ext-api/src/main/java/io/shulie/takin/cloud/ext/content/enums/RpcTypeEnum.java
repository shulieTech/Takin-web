package io.shulie.takin.cloud.ext.content.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RpcTypeEnum {

    APP("0"),
    HTTP("0"),
    HTTP2("1"),
    DUBBO("1"),
    MQ("3"),
    DB("4"),
    CACHE("5"),
    UNKNOWN("6"),
    ;

    private final String value;

}
