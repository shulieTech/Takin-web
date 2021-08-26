package com.pamirs.takin.entity.domain.dto.linkmanage.mapping.enums.fastdebug;

import lombok.Getter;

/**
 * @author 无涯
 * @date 2020/12/31 2:40 下午
 */
@Getter
public enum RequestTypeEnum {
    TEXT("Text","text/plain"),
    JSON("JSON","application/json"),
    HTML("HTML","text/html"),
    XML("XML","text/xml"),
    JAVASCRIPT("JavaScript","application/javascript");
    private String code;
    private String desc;
    RequestTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }


}
