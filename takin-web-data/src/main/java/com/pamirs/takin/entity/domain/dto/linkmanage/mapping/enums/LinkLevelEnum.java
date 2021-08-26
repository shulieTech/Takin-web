package com.pamirs.takin.entity.domain.dto.linkmanage.mapping.enums;

import lombok.Getter;

/**
 * @author  vernon
 * @date 2019/12/23 21:56
 */
@Getter
public enum LinkLevelEnum {

    p0("p0", "p0"),
    p1("p1", "p1"),
    p2("p2", "p2"),
    p3("p3", "p3"),
    ;

    private String code;
    private String desc;

    LinkLevelEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
