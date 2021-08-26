package com.pamirs.takin.entity.domain.dto.linkmanage.mapping.enums;

import lombok.Getter;

/**
 * @author  vernon
 * @date 2019/12/23 22:31
 */
@Getter
public enum HttpTypeEnum {

    PUT("1", "PUT"),
    GET("2", "GET"),
    POST("3", "POST"),
    OPTIONS("4", "OPTIONS"),
    DELETE("5", "DELETE"),
    HEAD("6", "HEAD"),
    TRACE("7", "TRACE"),
    CONNECT("8", "CONNECT"),
    DEFAULT("0", " ");

    private String code;
    private String desc;

    HttpTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
