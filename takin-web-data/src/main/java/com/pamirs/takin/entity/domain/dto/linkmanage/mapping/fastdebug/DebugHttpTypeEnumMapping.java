package com.pamirs.takin.entity.domain.dto.linkmanage.mapping.fastdebug;

import java.util.List;
import java.util.ArrayList;

import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.EnumResult;
import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.enums.fastdebug.HttpTypeEnum;

/**
 * @author 无涯
 * @date 2020/12/31 2:25 下午
 */
public class DebugHttpTypeEnumMapping {

    public static EnumResult parse(HttpTypeEnum httpTypeEnum) {
        EnumResult result = new EnumResult();
        if (httpTypeEnum != null) {
            switch (httpTypeEnum) {
                case GET:
                    result.label(httpTypeEnum.name()).value(httpTypeEnum.name()).num(0);
                    break;
                case POST:
                    result.label(httpTypeEnum.name()).value(httpTypeEnum.name()).num(1);
                    break;
                case PUT:
                    result.label(httpTypeEnum.name()).value(httpTypeEnum.name()).num(2);
                    break;
                case DELETE:
                    result.label(httpTypeEnum.name()).value(httpTypeEnum.name()).num(3);
                    break;
                default:
                    break;
            }
        }
        return result;
    }

    public static List<EnumResult> neededEnumResults() {
        List<EnumResult> enumResults = new ArrayList<>();
        for (HttpTypeEnum httpTypeEnum : HttpTypeEnum.values()) {
            enumResults.add(parse(httpTypeEnum));
        }
        return enumResults;
    }
}
