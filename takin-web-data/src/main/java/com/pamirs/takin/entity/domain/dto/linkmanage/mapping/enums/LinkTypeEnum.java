package com.pamirs.takin.entity.domain.dto.linkmanage.mapping.enums;

import lombok.Getter;

/**
 * @author  vernon
 * @date 2019/12/23 22:19
 */
@Getter
public enum LinkTypeEnum {

    CORE_LINK("0", "核心链路"),

    NOT_CORE_LINK("1", "非核心链路"),
    ;

    private String code;
    private String desc;

    LinkTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
