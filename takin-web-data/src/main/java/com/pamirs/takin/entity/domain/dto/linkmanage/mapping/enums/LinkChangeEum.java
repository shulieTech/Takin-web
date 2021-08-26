package com.pamirs.takin.entity.domain.dto.linkmanage.mapping.enums;

import lombok.Getter;

/**
 * @author  vernon
 * @date 2019/12/23 22:22
 */
@Getter
public enum LinkChangeEum {

    NORMAL("0", "正常"),

    CHANGED("1", "已变更");

    private String code;
    private String desc;

    LinkChangeEum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
