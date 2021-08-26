package com.pamirs.takin.entity.domain.dto.linkmanage.mapping;

import java.util.ArrayList;
import java.util.List;

import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.enums.LinkLevelEnum;

/**
 * @author  vernon
 * @date 2019/12/23 22:42
 */
public class LinkLevelEnumMapping {
    public static LinkLevelEnum getByCode(String code) {
        for (LinkLevelEnum linkLevelEnum : LinkLevelEnum.values()) {
            if (linkLevelEnum.getCode().equalsIgnoreCase(code)) {
                return linkLevelEnum;
            }
        }
        return LinkLevelEnum.p0;
    }

    public static EnumResult parse(LinkLevelEnum linkLevelEnum) {
        EnumResult result = new EnumResult();
        if (linkLevelEnum != null) {
            switch (linkLevelEnum) {
                case p0:
                    result.label(linkLevelEnum.getDesc()).value(linkLevelEnum.getCode()).num(0);
                    break;
                case p1:
                    result.label(linkLevelEnum.getDesc()).value(linkLevelEnum.getCode()).num(1);
                    break;
                case p2:
                    result.label(linkLevelEnum.getDesc()).value(linkLevelEnum.getCode()).num(2);
                    break;
                case p3:
                    result.label(linkLevelEnum.getDesc()).value(linkLevelEnum.getCode()).num(3);
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
        for (LinkLevelEnum statusEnum : LinkLevelEnum.values()) {
            enumResults.add(parse(statusEnum));
        }
        return enumResults;
    }
}
