package com.pamirs.takin.entity.domain.dto.linkmanage.mapping;

import java.util.ArrayList;
import java.util.List;

import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.enums.LinkChangeEum;

/**
 * @author  vernon
 * @date 2019/12/23 22:35
 */
public class LinkChangeEnumMapping {
    public static LinkChangeEum getByCode(String code) {
        for (LinkChangeEum statusEnum : LinkChangeEum.values()) {
            if (statusEnum.getCode().equalsIgnoreCase(code)) {
                return statusEnum;
            }
        }
        return LinkChangeEum.NORMAL;
    }

    public static EnumResult parse(LinkChangeEum linkChangeEum) {
        EnumResult result = new EnumResult();
        if (linkChangeEum != null) {
            switch (linkChangeEum) {
                case NORMAL:
                    result.label(linkChangeEum.getDesc()).value(linkChangeEum.getCode()).num(0);
                    break;
                case CHANGED:
                    result.label(linkChangeEum.getDesc()).value(linkChangeEum.getCode()).num(1);
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
        for (LinkChangeEum statusEnum : LinkChangeEum.values()) {
            enumResults.add(parse(statusEnum));
        }
        return enumResults;
    }
}
