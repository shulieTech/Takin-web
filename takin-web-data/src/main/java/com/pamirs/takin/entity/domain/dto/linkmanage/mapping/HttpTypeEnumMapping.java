package com.pamirs.takin.entity.domain.dto.linkmanage.mapping;

import java.util.ArrayList;
import java.util.List;

import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.enums.HttpTypeEnum;

/**
 * @author  vernon
 * @date 2019/12/23 22:37
 */
public class HttpTypeEnumMapping {
    public static HttpTypeEnum getByCode(String code) {
        for (HttpTypeEnum httpTypeEnum : HttpTypeEnum.values()) {
            if (httpTypeEnum.getCode().equalsIgnoreCase(code)) {
                return httpTypeEnum;
            }
        }
        return HttpTypeEnum.GET;
    }

    public static EnumResult parse(HttpTypeEnum httpTypeEnum) {
        EnumResult result = new EnumResult();
        if (httpTypeEnum != null) {
            switch (httpTypeEnum) {
                case DEFAULT:
                    result.label(httpTypeEnum.getDesc()).value(httpTypeEnum.getCode()).num(0);
                    break;
                case PUT:
                    result.label(httpTypeEnum.getDesc()).value(httpTypeEnum.getCode()).num(1);
                    break;
                case GET:
                    result.label(httpTypeEnum.getDesc()).value(httpTypeEnum.getCode()).num(2);
                    break;
                case POST:
                    result.label(httpTypeEnum.getDesc()).value(httpTypeEnum.getCode()).num(3);
                    break;
                case OPTIONS:
                    result.label(httpTypeEnum.getDesc()).value(httpTypeEnum.getCode()).num(4);
                    break;
                case DELETE:
                    result.label(httpTypeEnum.getDesc()).value(httpTypeEnum.getCode()).num(5);
                    break;
                case HEAD:
                    result.label(httpTypeEnum.getDesc()).value(httpTypeEnum.getCode()).num(6);
                    break;
                case TRACE:
                    result.label(httpTypeEnum.getDesc()).value(httpTypeEnum.getCode()).num(7);
                    break;
                case CONNECT:
                    result.label(httpTypeEnum.getDesc()).value(httpTypeEnum.getCode()).num(8);
                    break;
                default:
                    break;
            }
        } else {
            result.label(httpTypeEnum.getDesc()).value(httpTypeEnum.getCode()).num(0);
        }
        return result;
    }

    public static List<EnumResult> neededEnumResults() {
        List<EnumResult> enumResults = new ArrayList<>();
        for (HttpTypeEnum linkEnum : HttpTypeEnum.values()) {
            enumResults.add(parse(linkEnum));
        }
        return enumResults;
    }
}
