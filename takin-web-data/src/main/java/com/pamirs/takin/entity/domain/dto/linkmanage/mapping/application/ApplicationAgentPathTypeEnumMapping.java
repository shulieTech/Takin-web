package com.pamirs.takin.entity.domain.dto.linkmanage.mapping.application;

import com.pamirs.takin.entity.domain.dto.linkmanage.mapping.EnumResult;
import io.shulie.takin.web.common.enums.application.ApplicationAgentPathTypeEnum;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: 南风
 * @Date: 2021/11/17 2:52 下午
 */
public class ApplicationAgentPathTypeEnumMapping {

    public static EnumResult parse(ApplicationAgentPathTypeEnum pathTypeEnum) {
        EnumResult result = new EnumResult();
        if (pathTypeEnum != null) {
            switch (pathTypeEnum) {
                case OSS:
                    result.label(pathTypeEnum.getDesc()).value(pathTypeEnum.getVal().toString()).num(0);
                    break;
                case FTP:
                    result.label(pathTypeEnum.getDesc()).value(pathTypeEnum.getVal().toString()).num(1);
                    break;
            }
        }
        return result;
    }

    public static List<EnumResult> neededEnumResults() {
        List<EnumResult> enumResults = new ArrayList<>();
        for (ApplicationAgentPathTypeEnum pathTypeEnum : ApplicationAgentPathTypeEnum.values()) {
            enumResults.add(parse(pathTypeEnum));
        }
        return enumResults;
    }
}
