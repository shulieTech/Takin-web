package com.pamirs.takin.entity.domain.dto.linkmanage.mapping;

import java.util.ArrayList;
import java.util.List;

import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.enums.LinkChangeTypeEnum;

/**
 * @author  vernon
 * @date 2019/12/23 22:37
 */
public class LinkChangeTypeEnumMapping {
    public static LinkChangeTypeEnum getByCode(String code) {
        for (LinkChangeTypeEnum linkChangeTypeEnum : LinkChangeTypeEnum.values()) {
            if (linkChangeTypeEnum.getCode().equalsIgnoreCase(code)) {
                return linkChangeTypeEnum;
            }
        }
        return LinkChangeTypeEnum.NO_FLOW_;
    }

    public static EnumResult parse(LinkChangeTypeEnum linkChangeTypeEnum) {
        EnumResult result = new EnumResult();
        if (linkChangeTypeEnum != null) {
            switch (linkChangeTypeEnum) {
                case NO_FLOW_:
                    result.label(linkChangeTypeEnum.getDesc()).value(linkChangeTypeEnum.getCode()).num(0);
                    break;
                case ADD_LINK_:
                    result.label(linkChangeTypeEnum.getDesc()).value(linkChangeTypeEnum.getCode()).num(1);
                    break;
                default:
                    break;
            }
        } else {
        }
        return result;
    }

    public static List<EnumResult> neededEnumResults() {
        List<EnumResult> enumResults = new ArrayList<>();
        for (LinkChangeTypeEnum statusEnum : LinkChangeTypeEnum.values()) {
            enumResults.add(parse(statusEnum));
        }
        return enumResults;
    }
}
