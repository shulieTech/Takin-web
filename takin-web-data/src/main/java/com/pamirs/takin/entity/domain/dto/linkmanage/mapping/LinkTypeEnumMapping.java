package com.pamirs.takin.entity.domain.dto.linkmanage.mapping;

import java.util.ArrayList;
import java.util.List;

import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.enums.LinkTypeEnum;

/**
 * @author  vernon
 * @date 2019/12/23 22:45
 */
public class LinkTypeEnumMapping {
    public static LinkTypeEnum getByCode(String code) {
        for (LinkTypeEnum linkTypeEnum : LinkTypeEnum.values()) {
            if (linkTypeEnum.getCode().equalsIgnoreCase(code)) {
                return linkTypeEnum;
            }
        }
        return LinkTypeEnum.NOT_CORE_LINK;
    }

    public static EnumResult parse(LinkTypeEnum linkTypeEnum) {
        EnumResult result = new EnumResult();
        if (linkTypeEnum != null) {
            switch (linkTypeEnum) {
                case CORE_LINK:
                    result.label(linkTypeEnum.getDesc()).value(linkTypeEnum.getCode()).num(0);
                    break;
                case NOT_CORE_LINK:
                    result.label(linkTypeEnum.getDesc()).value(linkTypeEnum.getCode()).num(1);
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
        for (LinkTypeEnum statusEnum : LinkTypeEnum.values()) {
            enumResults.add(parse(statusEnum));
        }
        return enumResults;
    }
}
