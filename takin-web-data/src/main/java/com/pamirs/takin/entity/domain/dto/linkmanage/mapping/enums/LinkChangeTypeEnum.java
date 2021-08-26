package com.pamirs.takin.entity.domain.dto.linkmanage.mapping.enums;

import lombok.Getter;

/**
 * @author  vernon
 * @date 2019/12/23 22:31
 */
@Getter
public enum LinkChangeTypeEnum {

    /*  NO_FLOW_("1", "无调用流量通知"),
      ADD_LINK_("2", "添加调用关系通知");*/
    NO_FLOW_("1", "入口变更"),
    ADD_LINK_("2", "关联链路变更");

    private String code;
    private String desc;

    LinkChangeTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
